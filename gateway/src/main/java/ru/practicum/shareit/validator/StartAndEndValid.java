package ru.practicum.shareit.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartAndEndValidator.class)
public @interface StartAndEndValid {
    String message() default "error start date should be before end date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
