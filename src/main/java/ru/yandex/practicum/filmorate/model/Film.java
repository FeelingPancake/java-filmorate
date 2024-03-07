package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.annotations.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Film.
 */
@Value
@Builder(toBuilder = true)
public class Film {
    @NotBlank(message = "Имя не должно быть пустым")
    String name;
    @Size(max = 200, message = "Description must not be grater 200 characters")
    String description;
    @ReleaseDateConstraint(minimumDate = "1895-12-28", message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительной")
    int duration;
    Long id;
}
