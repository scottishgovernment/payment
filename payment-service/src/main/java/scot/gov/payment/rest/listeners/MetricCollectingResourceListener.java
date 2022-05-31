package scot.gov.payment.rest.listeners;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import scot.gov.payment.rest.PaymentResource;
import scot.gov.payment.rest.PaymentResourceListener;
import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentRequest;
import scot.gov.payment.service.PaymentResult;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import java.util.Set;

import static scot.gov.payment.rest.listeners.MetricName.*;
import static scot.gov.payment.rest.listeners.MetricName.ERROR_RATE;
import static scot.gov.payment.rest.listeners.MetricName.EXCEPTION_RATE;

/**
 * Listen to the PaymentResource in order to collect metrics.
 */
public class MetricCollectingResourceListener implements PaymentResourceListener {

    @Inject
    MetricRegistry metricRegistry;

    Counter requestCounter;

    Counter exceptionCounter;

    Counter invalidRequestCounter;

    Counter errorCounter;

    Meter requestMeter;

    Meter errorMeter;

    Meter exceptionMeter;

    @Inject
    public MetricCollectingResourceListener(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
        this.requestCounter = registerCounter(metricRegistry, REQUESTS);
        this.errorCounter = registerCounter(metricRegistry, ERRORS);
        this.exceptionCounter = registerCounter(metricRegistry, MetricName.EXCEPTIONS);
        this.invalidRequestCounter = registerCounter(metricRegistry, MetricName.INVALID_REQUESTS);
        this.requestMeter = registerMeter(metricRegistry, REQUEST_RATE);
        this.errorMeter = registerMeter(metricRegistry, ERROR_RATE);
        this.exceptionMeter = registerMeter(metricRegistry, EXCEPTION_RATE);
    }

    @Override
    public void onPaymentRequest(PaymentRequest request) {
        requestCounter.inc();
        requestMeter.mark();
    }

    @Override
    public void onPaymentResult(PaymentRequest request, PaymentResult result) {
        if (!result.isSuccess()) {
            errorCounter.inc();
            errorMeter.mark();
        }
    }

    @Override
    public void onInvalidPaymentRequest(PaymentRequest request, Set<ConstraintViolation<PaymentRequest>> violations) {
        invalidRequestCounter.inc();
    }

    @Override
    public void onPaymentException(PaymentRequest request, PaymentException exception) {
        exceptionCounter.inc();
        exceptionMeter.mark();
    }

    Counter registerCounter(MetricRegistry registry, MetricName name) {
        return registry.counter(name.name(PaymentResource.class));
    }

    Meter registerMeter(MetricRegistry registry, MetricName name) {
        return registry.meter(name.name(PaymentResource.class));
    }
}
