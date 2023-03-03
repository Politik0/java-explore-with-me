package ru.practicum.ewmservice.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.events.model.State;

@Component
public class StateEnumConverter implements Converter<String, State> {

    @Override
    public State convert(String source) {
        try {
            return State.valueOf(source);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unknown state: %S", source));
        }
    }
}
