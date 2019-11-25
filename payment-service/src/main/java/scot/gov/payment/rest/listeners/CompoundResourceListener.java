package scot.gov.payment.rest.listeners;

import scot.gov.payment.rest.PaymentResourceListener;
import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentRequest;
import scot.gov.payment.service.PaymentResult;

import java.util.Arrays;
import java.util.Collection;

public class CompoundResourceListener implements PaymentResourceListener {

    final Collection<PaymentResourceListener> listeners;

    public CompoundResourceListener(PaymentResourceListener ... listeners) {
        this.listeners = Arrays.asList(listeners);
    }

    @Override
    public void onPaymentRequest(PaymentRequest request) {
        listeners.stream().forEach(l -> l.onPaymentRequest(request));
    }

    @Override
    public void onPaymentResult(PaymentRequest request, PaymentResult result) {
        listeners.stream().forEach(l -> l.onPaymentResult(request, result));
    }

    @Override
    public void onPaymentException(PaymentRequest request, PaymentException exception) {
        listeners.stream().forEach(l -> l.onPaymentException(request, exception));
    }
}
