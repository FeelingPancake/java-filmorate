package ru.yandex.practicum.filmorate.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDateConstraint {
    String message() default "Invalid release date";

    String minimumDate();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
