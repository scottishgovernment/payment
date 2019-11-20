package scot.gov.payment;

import scot.gov.payment.rest.PaymentResource;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Hello world!
 *
 */
public class PaymentApplication extends Application {

    @Inject
    PaymentResource paymentResource;

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(asList(paymentResource));
    }

}
