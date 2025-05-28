package star.carsharing.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstPassName;
    private String secondPassName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstPassName = constraintAnnotation.firstPassName();
        secondPassName = constraintAnnotation.secondPassName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object firstValue = new BeanWrapperImpl(value).getPropertyValue(firstPassName);
        Object secondValue = new BeanWrapperImpl(value).getPropertyValue(secondPassName);
        return Objects.equals(firstValue, secondValue);
    }
}
