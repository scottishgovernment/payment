package scot.gov.payment.rest;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;

@Target( { FIELD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AmountValidator.class)
public @interface ValidAmount {

    String message() default "Invalid amount: minimum payment is £0.01, maximum payment is £5000";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}