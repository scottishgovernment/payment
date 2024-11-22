package scot.gov.payment.rest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.containsWhitespace;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.contains;

public class OrderCodeValidator implements ConstraintValidator<ValidOrderCode, String> {
    public boolean isValid(String code, ConstraintValidatorContext cxt) {
        // order code should not contain spaces or an ampersand, and the max length is 64
        return !containsWhitespace(code) && isNotBlank(code) && !contains(code, "&") && code.length() <= 64;
    }
}