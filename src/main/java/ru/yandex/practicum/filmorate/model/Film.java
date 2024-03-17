package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.annotations.Marker;
import ru.yandex.practicum.filmorate.annotations.ReleaseDateConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Film.
 */
@Value
@Builder(toBuilder = true)
public class Film {
    @NotNull(groups = Marker.OnUpdate.class, message = "ID должен присутствовать при обновлении объекта")
    @Null(groups = Marker.OnCreate.class, message = "ID должен отсутсвовать при создании объекта")
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
