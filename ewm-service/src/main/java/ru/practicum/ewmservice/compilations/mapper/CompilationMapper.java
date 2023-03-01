package ru.practicum.ewmservice.compilations.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.compilations.dto.CompilationDto;
import ru.practicum.ewmservice.compilations.dto.NewCompilationDto;
import ru.practicum.ewmservice.compilations.model.Compilation;

@Component
public class CompilationMapper {
    private final ModelMapper modelMapper;

    public CompilationMapper() {
        modelMapper = new ModelMapper();
    }

    public CompilationDto convertToDto(Compilation compilation) {
        return modelMapper.map(compilation, CompilationDto.class);
    }

    public Compilation convertFromDto(NewCompilationDto newCompilationDto) {
        return modelMapper.map(newCompilationDto, Compilation.class);
    }
}
