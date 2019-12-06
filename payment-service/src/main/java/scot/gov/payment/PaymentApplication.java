package scot.gov.payment;

import scot.gov.payment.rest.ErrorHandler;
import scot.gov.payment.rest.HealthCheck;
import scot.gov.payment.rest.PaymentResource;
import scot.gov.payment.rest.RequestLogger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@Singleton
public class PaymentApplication extends Application {

    @Inject
    PaymentApplication() {
        // Default constructor
    }

    @Inject
    PaymentResource paymentResource;

    @Inject
    HealthCheck healthCheck;

    @Inject
    RequestLogger logger;

    @Inject
    ErrorHandler errorHandler;

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(asList(paymentResource, healthCheck, logger, errorHandler));
    }

}
