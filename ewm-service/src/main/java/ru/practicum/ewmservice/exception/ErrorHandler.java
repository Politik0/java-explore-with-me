package ru.practicum.ewmservice.exception;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewmservice.converters.StringDateConverter;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
@AllArgsConstructor
public class ErrorHandler {
    private final StringDateConverter converter;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class, ConstraintViolationException.class})
    public ApiError handleNotValidArgumentException(Exception e) {
        log.warn(e.getClass().getSimpleName(), e);
        String message;
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException eValidation = (MethodArgumentNotValidException) e;
            message = Objects.requireNonNull(eValidation.getBindingResult().getFieldError()).getDefaultMessage();
        } else {
            message = e.getMessage();
        }
        String reason = "Incorrectly made request.";
        return new ApiError(message, reason, HttpStatus.BAD_REQUEST.toString(),
                converter.convert(LocalDateTime.now()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DataExistException.class, InvalidRequestException.class})
    public ApiError handleDataExistExceptionException(RuntimeException e) {
        log.warn(e.getClass().getSimpleName(), e);
        String reason = "For the requested operation the conditions are not met.";
        return new ApiError(e.getMessage(), reason, HttpStatus.CONFLICT.toString(),
                converter.convert(LocalDateTime.now()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ObjectNotFoundException.class})
    public ApiError handleObjectNotFoundException(RuntimeException e) {
        log.warn(e.getClass().getSimpleName(), e);
        String reason;
        if (e instanceof ObjectNotFoundException) {
            reason = "The required object was not found.";
        } else {
            reason = "Something was wrong.";
        }
        return new ApiError(e.getMessage(), reason, HttpStatus.NOT_FOUND.toString(),
                converter.convert(LocalDateTime.now()));
    }
}