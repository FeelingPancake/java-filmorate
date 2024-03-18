package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Ошибка валидации: {}", errors);

        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyExistsException.class)
    public Map<String, String> handleELementExistsExceptions(AlreadyExistsException ex) {
        log.warn("Ошибка добавления: {}", ex.getMessageAll());
        return ex.getMessageAll();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UpdateException.class)
    public Map<String, String> handleUpdateExceptions(UpdateException ex) {
        log.warn("Ошибка обновления: {}", ex.getMessageAll());
        return ex.getMessageAll();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class})
    public Map<String, String> handleNotFoundExceptions(RuntimeException ex) {
        log.warn("Ошибка отсутствия: {}", Map.of(ex.toString(), ex.getMessage()));
        return Map.of(ex.toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleOthersExceptions(Throwable ex) {
        log.warn("Ошибка: {}", Map.of(ex.toString(), ex.getMessage()));
        return Map.of(ex.toString(), ex.getMessage());
    }
}
