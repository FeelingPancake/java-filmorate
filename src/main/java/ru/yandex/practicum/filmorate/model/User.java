package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class User {
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
