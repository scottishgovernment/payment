package scot.gov.payment.service;

public class PaymentRequest {

    private String orderCode;

    private String description;

    private String amount;

    private String emailAddress;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEmailAddress() { return emailAddress; }

    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "orderCode='" + orderCode + '\'' +
                ", description='" + description + '\'' +
                ", amount='" + amount + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
