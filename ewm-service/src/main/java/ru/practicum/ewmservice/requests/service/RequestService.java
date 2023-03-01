package ru.practicum.ewmservice.requests.service;

import ru.practicum.ewmservice.requests.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.requests.dto.RequestWithCount;
import ru.practicum.ewmservice.requests.model.Request;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getParticipationRequests(long userId);

    ParticipationRequestDto addParticipationRequest(long userId, long eventId);

    ParticipationRequestDto cancelParticipationRequest(long userId, long requestId);

    long countConfirmedRequests(long eventId);

    Request updateStatusRequest(Request request);

    List<ParticipationRequestDto> getAllRequestsDtoByEventId(long eventId);

    List<Request> getRequestsByIdAndEventId(List<Long> ids, long eventId);

    List<RequestWithCount> getConfirmedRequestsCount(List<Long> ids);
}
