package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class User {
    @Email(message = "Email должен быть формата 'example@mail.com'")
    String email;
    @NotBlank
    @Pattern(regexp = "^\\S+$", message = "Логин должен быть без пробелов")
    String login;
    @PastOrPresent(message = "Нельзя родиться в будущем")
    LocalDate birthday;
    Long id;
    String name;

}
