package star.carsharing.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldMatchValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface FieldMatch {
    String message() default "The password must match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String firstPassName();

    String secondPassName();
}
