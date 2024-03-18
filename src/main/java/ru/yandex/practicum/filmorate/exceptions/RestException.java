package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

public class RestException extends RuntimeException {
    public RestException(String mes) {
        super(mes);
    }

    public Map<String, String> getMessageAll() {
        Map<String, String> errors = new HashMap<>();
        errors.put(this.toString(), this.getMessage());
        return errors;
    }
}

