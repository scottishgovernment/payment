package scot.gov.payment;

import scot.gov.payment.rest.HealthCheck;
import scot.gov.payment.rest.PaymentResource;
import scot.gov.payment.rest.RequestLogger;

import javax.inject.Inject;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class PaymentApplication extends Application {

    @Inject
    PaymentResource paymentResource;

    @Inject
    HealthCheck healthCheck;

    @Inject
    RequestLogger logger;

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(asList(paymentResource, healthCheck, logger));
    }

}
