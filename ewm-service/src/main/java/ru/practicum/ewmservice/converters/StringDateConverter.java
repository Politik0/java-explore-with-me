package ru.practicum.ewmservice.converters;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StringDateConverter implements Converter<String, LocalDateTime> {
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime convert(@NonNull String source) {
        return LocalDateTime.parse(source, FORMAT);
    }

    public String convert(LocalDateTime source) {
        return source.format(FORMAT);
    }
}