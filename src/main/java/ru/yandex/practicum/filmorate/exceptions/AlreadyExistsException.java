package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;;

@Slf4j
public class AlreadyExistsException extends RestException {
    public AlreadyExistsException(String mes) {
        super(mes);
    }
}
