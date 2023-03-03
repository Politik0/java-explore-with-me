package ru.practicum.ewmservice.compilations.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.compilations.dto.CompilationDto;
import ru.practicum.ewmservice.compilations.service.CompilationService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "false") Boolean pinned) {
        log.info("Getting all compilations, from={}, size={}, pinned={}", from, size, pinned);
        return compilationService.getCompilations(from, size, pinned);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.info("Getting compilation, compId={}", compId);
        return compilationService.getCompilationById(compId);
    }
}
