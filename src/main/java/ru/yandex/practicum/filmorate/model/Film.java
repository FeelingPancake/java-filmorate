package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.annotations.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Film.
 */
@Value
@Builder(toBuilder = true)
public class Film {
    Long id;
    @NotBlank(message = "Имя не должно быть пустым")
    String name;
    @Size(max = 200, message = "Description must not be grater 200 characters")
    @NotBlank
    String description;
    @ReleaseDateConstraint(minimumDate = "1895-12-28", message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    @NotNull
    LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительной")
    @NotNull
    int duration;

}
