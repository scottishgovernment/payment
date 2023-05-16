package scot.gov.payment.rest;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Target( { FIELD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = OrderCodeValidator.class)
public @interface ValidOrderCode {

    String message() default "Payment Reference cannot contains spaces, and must be less than 65 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}