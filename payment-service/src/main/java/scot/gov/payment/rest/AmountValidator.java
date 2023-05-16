package scot.gov.payment.rest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class AmountValidator implements ConstraintValidator<ValidAmount, String> {
    public boolean isValid(String amount, ConstraintValidatorContext cxt) {
        try {
            String poundAmount = String.format("£%s", amount);
            Number number = NumberFormat.getCurrencyInstance(Locale.UK).parse(poundAmount);
            return number.doubleValue() >= 0.01 && number.doubleValue() <= 5000.00;
        } catch (ParseException e) {
            return false;
        }
    }
}