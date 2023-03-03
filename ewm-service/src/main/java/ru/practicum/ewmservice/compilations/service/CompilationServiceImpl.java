package ru.practicum.ewmservice.compilations.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.compilations.dto.CompilationDto;
import ru.practicum.ewmservice.compilations.dto.NewCompilationDto;
import ru.practicum.ewmservice.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewmservice.compilations.mapper.CompilationMapper;
import ru.practicum.ewmservice.compilations.model.Compilation;
import ru.practicum.ewmservice.compilations.repository.CompilationRepository;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.events.service.EventService;
import ru.practicum.ewmservice.exception.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation newCompilation = compilationMapper.convertFromDto(newCompilationDto);
        List<Event> eventsForCompilation = eventService.getEventsByIds(newCompilationDto.getEvents());
        newCompilation.setEvents(eventsForCompilation);
        Compilation compilationSaved = compilationRepository.save(newCompilation);
        log.info("Compilation saved in repository, compSaved={}", compilationSaved);
        CompilationDto compilationDto = compilationMapper.convertToDto(compilationSaved);
        compilationDto.setEvents(eventService.getEventShortDtos(compilationSaved.getEvents()));
        log.info("CompilationDto prepared for response, compDto={}", compilationDto);
        return compilationDto;
    }

    @Override
    public void deleteCompilation(long compId) {
        getCompilationById(compId);
        compilationRepository.deleteById(compId);
        log.info("Compilation with id={} deleted", compId);
    }

    @Override
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = findCompilationById(compId);
        Boolean pined = updateCompilationRequest.getPinned();
        String title = updateCompilationRequest.getTitle();
        List<Long> events = updateCompilationRequest.getEvents();
        if (pined != null) {
            compilation.setPinned(pined);
        }
        if (title != null) {
            compilation.setTitle(title);
        }
        if (events != null) {
            List<Event> eventsForCompilation = eventService.getEventsByIds(events);
            compilation.setEvents(eventsForCompilation);
        }
        Compilation compilationUpdated = compilationRepository.save(compilation);
        log.info("Compilation updated, compUpdated={}", compilationUpdated);
        CompilationDto compilationDto = compilationMapper.convertToDto(compilationUpdated);
        compilationDto.setEvents(eventService.getEventShortDtos(compilationUpdated.getEvents()));
        log.info("CompilationDto prepared for response, compDto={}", compilationDto);
        return compilationDto;
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = findCompilationById(compId);
        CompilationDto compilationDto = compilationMapper.convertToDto(compilation);
        compilationDto.setEvents(eventService.getEventShortDtos(compilation.getEvents()));
        log.info("CompilationDto prepared for response, compDto={}", compilationDto);
        return compilationDto;
    }

    @Override
    public List<CompilationDto> getCompilations(int from, int size, Boolean pinned) {
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned,
                    PageRequest.of(from / size, size)).stream().collect(Collectors.toList());
        } else {
            compilations = compilationRepository.findAll(PageRequest.of(from / size, size)).stream()
                    .collect(Collectors.toList());
        }
        List<CompilationDto> compilationDto = compilations.stream()
                .map(compilationMapper::convertToDto)
                .collect(Collectors.toList());
        log.info("All Compilations got from repository, compDto={}", compilationDto);
        return compilationDto;
    }

    private Compilation findCompilationById(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Compilation with id=%d was not found", compId)));
    }
}
