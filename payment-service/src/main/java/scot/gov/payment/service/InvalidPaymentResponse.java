package scot.gov.payment.service;

import jakarta.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class InvalidPaymentResponse {

    List<Violation> violations = new ArrayList<>();

    public List<Violation> getViolations() {
        return violations;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }

    public static InvalidPaymentResponse invalidResponse(Set<ConstraintViolation<PaymentRequest>> violations) {
        InvalidPaymentResponse response = new InvalidPaymentResponse();
        response.violations = violations.stream().map(InvalidPaymentResponse::violation).collect(toList());
        return response;
    }

    public static Violation violation(ConstraintViolation<PaymentRequest> constraintViolation) {
        Violation violation = new Violation();
        violation.setField(constraintViolation.getPropertyPath().toString());
        violation.setMessage(constraintViolation.getMessage());
        return violation;
    }

    public static class Violation {

        String field;

        String message;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Violation{" +
                    "field='" + field + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
