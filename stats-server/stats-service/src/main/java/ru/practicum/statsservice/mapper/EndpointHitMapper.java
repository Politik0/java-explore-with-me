package ru.practicum.statsservice.mapper;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EndpointHitMapper {
    private final ModelMapper modelMapper;
    Converter<String, LocalDateTime> fromStringDate;

    public EndpointHitMapper() {
        modelMapper = new ModelMapper();
        fromStringDate = new AbstractConverter<>() {
            @Override
            protected LocalDateTime convert(String source) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(source, format);
            }
        };
        modelMapper.addConverter(fromStringDate);
    }

    public EndpointHitDto convertToDto(EndpointHit endpointHit) {
        return modelMapper.map(endpointHit, EndpointHitDto.class);
    }

    public EndpointHit convertFromDto(EndpointHitDto endpointHitDto) {
        return modelMapper.map(endpointHitDto, EndpointHit.class);
    }

    public ViewStats convertToViewStats(EndpointHit endpointHit) {
        return modelMapper.map(endpointHit, ViewStats.class);
    }
}
