package ru.practicum.ewmservice.requests.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.requests.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.requests.model.Request;

@Component
public class RequestMapper {
    private final ModelMapper modelMapper;

    public RequestMapper() {
        modelMapper = new ModelMapper();
    }

    public ParticipationRequestDto convertToDto(Request request) {
        modelMapper.typeMap(Request.class, ParticipationRequestDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getEvent().getId(), ParticipationRequestDto::setEvent);
            mapper.map(src -> src.getRequester().getId(), ParticipationRequestDto::setRequester);
        });
        return modelMapper.map(request, ParticipationRequestDto.class);
    }

    public Request convertFromDto(ParticipationRequestDto requestDto) {
        return modelMapper.map(requestDto, Request.class);
    }
}
