package scot.gov.payment.service;

import scot.gov.payment.rest.ValidAmount;
import scot.gov.payment.rest.ValidOrderCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class PaymentRequest {

    @ValidOrderCode
    private String orderCode;

    @NotBlank(message= "Description cannot be blank")
    private String description;

    @ValidAmount
    private String amount;

    @Email
    @NotBlank(message= "Email cannot be blank")
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
