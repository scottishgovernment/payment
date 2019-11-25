package scot.gov.payment.service;

public class PaymentException extends Exception {

    public PaymentException(String msg) {
        super(msg);
    }

    public PaymentException(String msg, Throwable t) {
        super(msg, t);
    }
}
