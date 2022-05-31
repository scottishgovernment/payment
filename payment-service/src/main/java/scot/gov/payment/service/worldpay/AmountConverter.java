package scot.gov.payment.service.worldpay;

import scot.gov.payment.service.PaymentException;

import javax.inject.Inject;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Worldpay expects the amount to be specified as pence.
 *
 * This class converts the punds and pence value to pence.
 */
public class AmountConverter {

    @Inject
    public AmountConverter() {
        // defautl constructor
    }

    public String convert(String amount) throws PaymentException {
        try {
            String poundAmount = String.format("£%s", amount);
            Number number = NumberFormat.getCurrencyInstance(Locale.UK).parse(poundAmount);
            int penceAmount = (int) (number.doubleValue() * 100);
            return Integer.toString(penceAmount);
        } catch (ParseException e) {
            throw new PaymentException("Unable to parse amount", e);
        }
    }

}
