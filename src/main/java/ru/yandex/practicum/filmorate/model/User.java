package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import ru.yandex.practicum.filmorate.annotations.Marker;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class User {
    @NotNull(groups = Marker.OnUpdate.class, message = "ID должен присутствовать при обновлении объекта")
    @Null(groups = Marker.OnCreate.class, message = "ID должен отсутсвовать при создании объекта")
    Long id;
    @Email(message = "Email должен быть формата 'example@mail.com'")
    @NotBlank
    String email;
    @NotBlank
    @Pattern(regexp = "^\\S+$", message = "Логин должен быть без пробелов")
    String login;
    String name;
    @PastOrPresent(message = "Нельзя родиться в будущем")
    @NotNull
    LocalDate birthday;



}
