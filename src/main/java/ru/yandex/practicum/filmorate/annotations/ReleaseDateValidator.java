package ru.yandex.practicum.filmorate.annotations;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {
    private LocalDate minimumDate;

    @Override
    public void initialize(ReleaseDateConstraint constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.minimumDate(), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null && !value.isBefore(minimumDate);
    }

}
