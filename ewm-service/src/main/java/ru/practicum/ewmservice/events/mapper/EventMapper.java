package ru.practicum.ewmservice.events.mapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.events.dto.EventFullDto;
import ru.practicum.ewmservice.events.dto.EventShortDto;
import ru.practicum.ewmservice.events.dto.NewEventDto;
import ru.practicum.ewmservice.events.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {
    private final ModelMapper modelMapper;
    private final Converter<String, LocalDateTime> fromStringDate;
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventMapper() {
        modelMapper = new ModelMapper();
        fromStringDate = new AbstractConverter<>() {
            @Override
            protected LocalDateTime convert(String source) {

                return LocalDateTime.parse(source, format);
            }
        };

        modelMapper.addConverter(fromStringDate);
    }

    public EventShortDto convertToShortDto(Event event) {
        EventShortDto eventShortDto = modelMapper.map(event, EventShortDto.class);
        if (event.getEventDate() != null) {
            eventShortDto.setEventDate(event.getEventDate().format(format));
        }
        return eventShortDto;
    }

    public EventFullDto convertToFullDto(Event event) {
        EventFullDto eventFullDto = modelMapper.map(event, EventFullDto.class);
        if (event.getEventDate() != null) {
            eventFullDto.setEventDate(event.getEventDate().format(format));
        }
        if (event.getCreatedOn() != null) {
            eventFullDto.setCreatedOn(event.getCreatedOn().format(format));
        }
        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(event.getPublishedOn().format(format));
        }
        return eventFullDto;
    }

    public EventShortDto convertFromFullToShortDto(EventFullDto eventFullDto) {
        return modelMapper.map(eventFullDto, EventShortDto.class);
    }

    public Event convertFromDto(NewEventDto newEventDto) {
        return modelMapper.map(newEventDto, Event.class);
    }
}