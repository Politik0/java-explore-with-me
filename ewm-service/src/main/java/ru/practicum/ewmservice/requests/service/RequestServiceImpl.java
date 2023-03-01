package ru.practicum.ewmservice.requests.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.events.model.State;
import ru.practicum.ewmservice.events.repository.EventRepository;
import ru.practicum.ewmservice.exception.DataExistException;
import ru.practicum.ewmservice.exception.InvalidRequestException;
import ru.practicum.ewmservice.exception.ObjectNotFoundException;
import ru.practicum.ewmservice.requests.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.requests.dto.RequestWithCount;
import ru.practicum.ewmservice.requests.mapper.RequestMapper;
import ru.practicum.ewmservice.requests.model.Request;
import ru.practicum.ewmservice.requests.model.Status;
import ru.practicum.ewmservice.requests.repository.RequestRepository;
import ru.practicum.ewmservice.users.model.User;
import ru.practicum.ewmservice.users.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final UserService userService;

    @Override
    public List<ParticipationRequestDto> getParticipationRequests(long userId) {
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        List<ParticipationRequestDto> requestDtos = new ArrayList<>();
        if (requests != null) {
            requestDtos = requests.stream().map(requestMapper::convertToDto).collect(Collectors.toList());
        }
        log.info("All participation requests of current user got from repository, user={}, requestsDtos={}",
                userId, requestDtos);
        return requestDtos;
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(long userId, long eventId) {
        User user = userService.getUserById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id=%d was not found",
                        eventId)));
        Request request = new Request(0, LocalDateTime.now(), event, user, Status.PENDING);
        if (!event.isRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        }
        validateRequest(userId, event);
        Request requestSaved = requestRepository.save(request);
        log.info("Request saved in repository, requestSaved={}", requestSaved);
        return requestMapper.convertToDto(requestSaved);
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(long userId, long requestId) {
        userService.getUserById(userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Request with requestId=%d and requesterId=%d was not found",
                        requestId, userId)));
        request.setStatus(Status.CANCELED);
        Request requestUpdated = requestRepository.save(request);
        log.info("Request canceled, requestUpdated={}", requestUpdated);
        return requestMapper.convertToDto(requestUpdated);
    }

    @Override
    public long countConfirmedRequests(long eventId) {
        return requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED);
    }

    @Override
    public Request updateStatusRequest(Request request) {
        Request requestSaved = requestRepository.save(request);
        log.info("RequestStatus updated, requestSaved={}", requestSaved);
        return requestSaved;
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestsDtoByEventId(long eventId) {
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        log.info("Requests for event with id={} got, requests={}", eventId, requests);
        return requests.stream()
                .map(requestMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Request> getRequestsByIdAndEventId(List<Long> ids, long eventId) {
        List<Request> requests = requestRepository.findAllByIdInAndEventId(ids, eventId);
        log.info("Requests with ids={} for event with id={} got", ids, eventId);
        return requests;
    }

    @Override
    public List<RequestWithCount> getConfirmedRequestsCount(List<Long> ids) {
        List<RequestWithCount> requestWithCounts = requestRepository.getRequestCount(ids, Status.CONFIRMED);
        log.info("Confirmed requests with counts for events with ids={} got", ids);
        return requestWithCounts;
    }

    private void validateRequest(long userId, Event event) {
        long eventId = event.getId();
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new DataExistException(String.format("Request from user=%d for event=%d exists", userId, eventId));
        }
        if (event.getInitiator().getId() == userId) {
            throw new InvalidRequestException(String.format("User cannot make a request for his event, userId=%d, " +
                    "eventId=%d", userId, eventId));
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new InvalidRequestException(String.format("User cannot make a request for unpublished event, userId=%d, " +
                    "eventId=%d", userId, eventId));
        }
        long confirmedRequests = countConfirmedRequests(eventId);
        if (event.getParticipantLimit() == confirmedRequests) {
            throw new InvalidRequestException(String.format("Participant limit exceeded for event with id=%d", eventId));
        }
    }
}
