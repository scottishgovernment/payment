package scot.gov.payment.rest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.containsWhitespace;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OrderCodeValidator implements ConstraintValidator<ValidOrderCode, String> {
    public boolean isValid(String code, ConstraintValidatorContext cxt) {
        // order code should not contains spaces, and the max length is 64
        return !containsWhitespace(code) && isNotBlank(code) && code.length() <= 64;
    }
}