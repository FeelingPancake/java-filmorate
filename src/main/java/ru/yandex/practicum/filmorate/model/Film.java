package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.Marker;
import ru.yandex.practicum.filmorate.annotations.ReleaseDateConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder(toBuilder = true)
public class Film {
    @NotNull(groups = Marker.OnUpdate.class, message = "ID должен присутствовать при обновлении объекта")
    @Null(groups = Marker.OnCreate.class, message = "ID должен отсутсвовать при создании объекта")
    private final Long id;
    @NotBlank(message = "Имя не должно быть пустым")
    private final String name;
    @Size(max = 200, message = "Description must not be grater 200 characters")
    @NotBlank
    private final String description;
    @ReleaseDateConstraint(minimumDate = "1895-12-28", message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    @NotNull
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительной")
    @NotNull
    private final int duration;
    private final Set<Long> likedBy = new HashSet<>();
    private int rating = 0;


}
