package ru.practicum.ewmservice.events.service;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.categories.model.Category;
import ru.practicum.ewmservice.categories.service.CategoryService;
import ru.practicum.ewmservice.converters.StateEnumConverter;
import ru.practicum.ewmservice.converters.StringDateConverter;
import ru.practicum.ewmservice.events.dto.*;
import ru.practicum.ewmservice.events.mapper.EventMapper;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.events.model.EventLocation;
import ru.practicum.ewmservice.events.model.QEvent;
import ru.practicum.ewmservice.events.model.State;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.events.repository.LocationRepository;
import ru.practicum.ewmservice.exception.InvalidRequestException;
import ru.practicum.ewmservice.exception.ObjectNotFoundException;
import ru.practicum.ewmservice.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewmservice.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmservice.requests.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.requests.dto.RequestWithCount;
import ru.practicum.ewmservice.requests.mapper.RequestMapper;
import ru.practicum.ewmservice.requests.model.Request;
import ru.practicum.ewmservice.requests.model.Status;
import ru.practicum.ewmservice.requests.service.RequestService;
import ru.practicum.ewmservice.users.model.User;
import ru.practicum.ewmservice.users.service.UserService;
import ru.practicum.statsclient.EndpointHitsClient;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final EndpointHitsClient endpointHitsClient;
    private final StringDateConverter converter;
    private final CategoryService categoryService;
    private final LocationRepository locationRepository;
    private final RequestService requestService;
    private final RequestMapper requestMapper;
    private final StateEnumConverter enumConverter;
    private static final String URI_PREFIX = "/events";
    private static final int HOURS_TO_EVENT_DATE = 2;
    private static final int HOURS_FROM_PUBLISH_DATE_TO_EVENT_DATE = 1;

    @Override
    public EventFullDto addNewEvent(long userId, NewEventDto newEventDto) {
        Event event = eventMapper.convertFromDto(newEventDto);
        User initiator = userService.getUserById(userId);
        event.setInitiator(initiator);
        event.setState(State.PENDING);
        validateUpdateEventUserRequest(event);
        event.setCreatedOn(LocalDateTime.now());
        Category category = categoryService.getCategory(newEventDto.getCategory());
        event.setCategory(category);
        event.setLocation(locationRepository.save(newEventDto.getLocation()));
        Event eventSaved = eventRepository.save(event);
        log.info("Event saved in repository, eventSaved={}", eventSaved);
        return eventMapper.convertToFullDto(eventSaved);
    }

    @Override
    public EventFullDto getEventByIdOfCurrentUser(long userId, long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=%d and initiatorId=%d was not found",
                        eventId, userId)));
        return setEventInfo(event);
    }

    @Override
    public EventFullDto getPublicEventInfoById(long id, HttpServletRequest request) {
        Event event = getEventById(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new InvalidRequestException(String.format("Unpublished event with id=%d are not available", id));
        }
        saveStatsView(request);
        return setEventInfo(event);
    }

    @Override
    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=%d was not found",
                        eventId)));
    }

    @Override
    public EventFullDto updateEventOfCurrentUser(long userId, long eventId,
                                                 UpdateEventUserRequest updateEventUserRequest) {
        userService.getUserById(userId);
        return updateEventData(eventId, updateEventUserRequest);
    }

    @Override
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        EventFullDto eventFullDto = updateEventData(eventId, updateEventAdminRequest);
        log.info("Event updated by admin, eventFullDto={}", eventFullDto);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getAllEventsOfCurrentUser(long userId, int from, int size) {
        userService.getUserById(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size)).stream()
                .collect(Collectors.toList());
        List<EventFullDto> eventFullDtos = setReviewsAndConfirmedRequests(events, null, null);
        List<EventShortDto> eventShortDtos = eventFullDtos.stream()
                .map(eventMapper::convertFromFullToShortDto)
                .collect(Collectors.toList());
        log.info("EventShortDtos prepared for response, EventShortDtos={}", eventShortDtos);
        return eventShortDtos;
    }

    @Override
    public List<EventShortDto> getAllEventsWithFilter(String text, List<Long> categories, Boolean paid,
                                                      String rangeStart, String rangeEnd, String sort,
                                                      Boolean onlyAvailable, int from, int size,
                                                      HttpServletRequest httpServletRequest) {
        saveStatsView(httpServletRequest);
        QEvent event = QEvent.event;
        List<String> states = List.of(State.PUBLISHED.toString());
        BooleanBuilder builder = buildQuery(event, text, categories, paid, rangeStart, rangeEnd, states,
                null);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("eventDate").descending());
        List<Event> events = eventRepository.findAll(builder, pageable).stream().collect(Collectors.toList());
        List<EventFullDto> eventFullDtos = setReviewsAndConfirmedRequests(events, rangeStart, rangeEnd);
        if (sort.equals("VIEWS")) {
            eventFullDtos.sort(Comparator.comparing(EventFullDto::getViews));
        }
        List<EventShortDto> eventShortDtos = eventFullDtos.stream()
                .map(eventMapper::convertFromFullToShortDto)
                .collect(Collectors.toList());
        log.info("EventShortDtos prepared for response, EventShortDtos={}", eventShortDtos);
        return eventShortDtos;
    }

    @Override
    public List<EventShortDto> getEventShortDtos(List<Event> events) {
        List<EventFullDto> eventFullDtos = setReviewsAndConfirmedRequests(events, null, null);
        List<EventShortDto> eventShortDtos = eventFullDtos.stream()
                .map(eventMapper::convertFromFullToShortDto)
                .collect(Collectors.toList());
        log.info("EventShortDtos got from repository, EventShortDtos={}", eventShortDtos);
        return eventShortDtos;
    }

    @Override
    public List<Event> getEventsByIds(List<Long> ids) {
        return eventRepository.findByIdIn(ids);
    }

    @Override
    public List<EventFullDto> findEventsByAdmin(List<Long> users, List<String> statesValues, List<Long> categories,
                                                String rangeStart, String rangeEnd, int from, int size) {
        QEvent event = QEvent.event;
        BooleanBuilder builder = buildQuery(event, null, categories, null, rangeStart, rangeEnd, statesValues,
                users);
        List<Event> events = eventRepository.findAll(builder, PageRequest.of(from / size, size)).stream()
                .collect(Collectors.toList());
        log.info("Searching events in repository, event={}", events);
        List<EventFullDto> eventFullDtos = setReviewsAndConfirmedRequests(events, rangeStart, rangeEnd);
        log.info("EventFullDtos prepared for response, EventShortDtos={}", eventFullDtos);
        return eventFullDtos;
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (user.getId() != event.getInitiator().getId()) {
            throw new InvalidRequestException(String.format("User with id=%d is not initiator of event with id=%d",
                    userId, eventId));
        }
        return requestService.getAllRequestsDtoByEventId(eventId);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(long userId, long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (user.getId() != event.getInitiator().getId()) {
            throw new InvalidRequestException(String.format("User with id=%d is not initiator of event with id=%d",
                    userId, eventId));
        }
        List<Request> requests = requestService.getRequestsByIdAndEventId(updateRequest.getRequestIds(), eventId);
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        Status status = updateRequest.getStatus();
        long confirmedRequestsCount = requestService.countConfirmedRequests(eventId);
        if (event.isRequestModeration() || event.getParticipantLimit() > 0) {
            for (Request request : requests) {
                if (request.getStatus().equals(Status.PENDING) && status.equals(Status.CONFIRMED)) {
                    if (confirmedRequestsCount < event.getParticipantLimit()) {
                        request.setStatus(Status.CONFIRMED);
                        confirmedRequests.add(requestService.updateStatusRequest(request));
                        confirmedRequestsCount++;
                    } else {
                        request.setStatus(Status.REJECTED);
                        rejectedRequests.add(requestService.updateStatusRequest(request));
                        throw new InvalidRequestException(String.format("Event with id=%d has already reached the " +
                                "limit of applications", eventId));
                    }
                } else if (request.getStatus().equals(Status.PENDING) && status.equals(Status.REJECTED)) {
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(requestService.updateStatusRequest(request));
                } else {
                    throw new InvalidRequestException("Status can only be changed for applications that are in the " +
                            "pending state");
                }
            }
        }
        List<ParticipationRequestDto> confirmedRequestsDto = confirmedRequests.stream()
                .map(requestMapper::convertToDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequestsDto = rejectedRequests.stream()
                .map(requestMapper::convertToDto)
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(confirmedRequestsDto, rejectedRequestsDto);
    }

    private EventFullDto setEventInfo(Event event) {
        log.info("Event got from repository, event={}", event);
        EventFullDto eventFullDto = eventMapper.convertToFullDto(event);
        List<ViewStats> viewStats = getStatsViews(null, event, null, null);
        eventFullDto.setViews(setViews(eventFullDto, viewStats));
        eventFullDto.setConfirmedRequests(requestService.countConfirmedRequests(event.getId()));
        log.info("EventFullDto prepared for response, eventFullDto={}", eventFullDto);
        return eventFullDto;
    }

    private List<ViewStats> getStatsViews(List<Event> events, Event event, String rangeStart, String rangeEnd) {
        List<ViewStats> viewStats = new ArrayList<>();
        if (events == null && event != null) {
            events = new ArrayList<>();
            events.add(event);
        }
        if (events != null) {
            List<String> eventUris = events.stream()
                    .map(e -> URI_PREFIX + "/" + e.getId())
                    .collect(Collectors.toList());
            if (rangeStart == null) {
                rangeStart = events.stream()
                        .map(Event::getEventDate).min(LocalDateTime::compareTo)
                        .map(converter::convert)
                        .orElse(converter.convert(LocalDateTime.now()));
            }
            if (rangeEnd == null)  {
                rangeEnd = converter.convert(LocalDateTime.now());
            }
            ResponseEntity<Object> responseEntity = endpointHitsClient.getViewStats(rangeStart, rangeEnd,
                    eventUris, false);
            viewStats = ((List<ViewStats>) responseEntity.getBody());
            log.info("ViewStats={}", viewStats);
        }
        return viewStats;
    }

    private List<EventFullDto> setReviewsAndConfirmedRequests(List<Event> events, String rangeStart, String rangeEnd) {
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<ViewStats> viewStats = getStatsViews(events, null, rangeStart, rangeEnd);
        List<RequestWithCount> requestWithCounts = requestService.getConfirmedRequestsCount(eventIds);
        List<EventFullDto> eventFullDtos = events.stream()
                .map(eventMapper::convertToFullDto)
                .collect(Collectors.toList());
        eventFullDtos.forEach(event -> {
            event.setViews(setViews(event, viewStats));
            event.setConfirmedRequests(setConfirmedRequests(event, requestWithCounts));
        });
        return eventFullDtos;
    }

    private long setViews(EventFullDto eventFullDto, List<ViewStats> viewStats) {
        if (viewStats != null) {
            return viewStats.stream()
                    .filter(stats -> stats.getUri().equals(URI_PREFIX + "/" + eventFullDto.getId()))
                    .map(ViewStats::getHits)
                    .findFirst().orElse(0L);
        } else {
            return 0L;
        }
    }

    private long setConfirmedRequests(EventFullDto eventFullDto,  List<RequestWithCount> requestWithCounts) {
        if (requestWithCounts != null) {
            return requestWithCounts.stream()
                    .filter(r -> r.getEventId() == eventFullDto.getId())
                    .map(RequestWithCount::getViews)
                    .findFirst().orElse(0L);
        } else {
            return 0L;
        }
    }

    private void saveStatsView(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("Explore with me")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(converter.convert(LocalDateTime.now()))
                .build();
        endpointHitsClient.saveEndpointHit(endpointHitDto);
        log.info("Saving endpoint hit to StatsService, endpointHitDto={}", endpointHitDto);
    }

    private EventFullDto updateEventData(long eventId, UpdateEventRequest updateEventRequest) {
        Event event = getEventById(eventId);
        if (updateEventRequest instanceof UpdateEventUserRequest) {
            validateUpdateEventUserRequest(event);
        }
        String annotation = updateEventRequest.getAnnotation();
        Long category = updateEventRequest.getCategory();
        String description = updateEventRequest.getDescription();
        String eventDateString = updateEventRequest.getEventDate();
        EventLocation location = updateEventRequest.getLocation();
        Boolean paid = updateEventRequest.getPaid();
        Long participantLimit = updateEventRequest.getParticipantLimit();
        Boolean requestModeration = updateEventRequest.getRequestModeration();
        State stateAction = updateEventRequest.getStateAction();
        String title = updateEventRequest.getTitle();

        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        if (category != null) {
            event.setCategory(categoryService.getCategory(category));
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (stateAction != null) {
            if (updateEventRequest instanceof UpdateEventAdminRequest) {
                boolean isAvailableForPublish = event.getState().equals(State.PENDING);
                boolean isAvailableForReject = !event.getState().equals(State.PUBLISHED);

                if (updateEventRequest.getStateAction().equals(State.REJECT_EVENT) && isAvailableForReject) {
                    event.setState(State.REJECTED);
                } else if (updateEventRequest.getStateAction().equals(State.REJECT_EVENT) && !isAvailableForReject) {
                    throw new InvalidRequestException("The event cannot be canceled because it has already " +
                            "been published");
                }
                if (updateEventRequest.getStateAction().equals(State.PUBLISH_EVENT) && isAvailableForPublish) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                } else if (updateEventRequest.getStateAction().equals(State.PUBLISH_EVENT) && !isAvailableForPublish) {
                    throw new InvalidRequestException("The event can only be published if it is in the publish " +
                            "pending state");
                }
            } else {
                if (updateEventRequest.getStateAction().equals(State.CANCEL_REVIEW)) {
                    event.setState(State.CANCELED);
                }
                if (updateEventRequest.getStateAction().equals(State.SEND_TO_REVIEW)) {
                    event.setState(State.PENDING);
                }
            }
        }
        if (eventDateString != null) {
            LocalDateTime eventDate = converter.convert(eventDateString);
            if (eventDate.isBefore(LocalDateTime.now())) {
                throw new InvalidRequestException("The event date to be changed must be no " +
                        "earlier than now date");
            } else if (event.getPublishedOn() != null && updateEventRequest instanceof UpdateEventAdminRequest && event.getPublishedOn()
                    .plusHours(HOURS_FROM_PUBLISH_DATE_TO_EVENT_DATE).isAfter(eventDate)) {
                throw new InvalidRequestException("The event date to be changed must be no " +
                        "earlier than one hour from the publication date");
            }
            event.setEventDate(eventDate);
        }
        if (location != null) {
            event.setLocation(locationRepository.save(location));
        }
        if (paid != null) {
            event.setPaid(paid);
        }
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        if (title != null) {
            event.setTitle(title);
        }

        Event eventUpdated = eventRepository.save(event);
        log.info("Event updated, eventUpdated={}", eventUpdated);
        return eventMapper.convertToFullDto(eventUpdated);
    }

    private void validateUpdateEventUserRequest(Event event) {
        if (event.getState().equals(State.PUBLISHED)) {
            throw new InvalidRequestException(String.format("Only canceled events or events pending moderation can be changed, " +
                    "eventId=%d, eventState=%s", event.getId(), event.getState()));
        }
        if (LocalDateTime.now().plusHours(HOURS_TO_EVENT_DATE).isAfter(event.getEventDate())) {
            throw new InvalidRequestException(String.format("The date and time of the event cannot be earlier than two " +
                    "hours from the current moment, eventId=%d, eventDate=%s", event.getId(), event.getEventDate()));
        }
    }

    private BooleanBuilder buildQuery(QEvent event, String text, List<Long> categories, Boolean paid,
                                      String rangeStart, String rangeEnd, List<String> stateValues, List<Long> users) {
        BooleanBuilder builder = new BooleanBuilder();
        if (stateValues != null) {
            List<State> states = stateValues.stream()
                    .map(enumConverter::convert)
                    .collect(Collectors.toList());
            builder.and(event.state.in(states));
        }
        if (users != null) {
            builder.and(event.initiator.id.in(users));
        }
        if (text != null) {
            builder.and((event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text))));
        }
        if (categories != null) {
            builder.and(event.category.id.in(categories));
        }
        if (paid != null) {
            builder.and(event.paid.eq(paid));
        }
        if (rangeStart != null) {
            builder.and(event.eventDate.after(converter.convert(rangeStart)));
        } else {
            builder.and(event.eventDate.after(LocalDateTime.now()));
        }
        if (rangeEnd != null) {
            builder.and(event.eventDate.before(converter.convert(rangeEnd)));
        }
        return builder;
    }
}
