package scot.gov.payment.service.worldpay;

import scot.gov.payment.service.*;
import scot.gov.payment.service.worldpay.responseurls.PaymentUrlFormatter;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

public class WorldpayPaymentService implements PaymentService {

    @Inject
    WebTarget target;

    @Inject
    WorldpayDocumentBuilder worldpayDocumentBuilder;

    @Inject
    WorldpayDocumentParser worldpayDocumentParser;

    @Inject
    PaymentUrlFormatter paymentUrlFormatter;

    @Inject
    AmountConverter amountConverter;

    @Inject
    WorldpayPaymentService() {
        // Default constructor
    }

    @Override
    public void makePayment(PaymentRequest request, String siteUrl, PaymentCallback callback) {

        try {
            Entity<String> entity = paymentEntity(request);
            doPost(entity, request, siteUrl, callback);
        } catch (PaymentException e) {
            callback.onPaymentException(request, new PaymentException("Failed to post to worldpay", e));
        }
    }

    void doPost(Entity<String> entity, PaymentRequest request, String siteUrl, PaymentCallback paymentCallback) {
        InvocationCallback<Response> callback = new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                handleResponse(request, siteUrl, response, paymentCallback);
            }

            @Override
            public void failed(Throwable throwable) {
                paymentCallback.onPaymentException(request, new PaymentException("Failed to make payment", throwable));
            }
        };
        target.request().async().post(entity, callback);
    }

    void handleResponse(PaymentRequest request, String siteUrl, Response response, PaymentCallback callback) {
        try {
            PaymentResult result = responseToResult(response, siteUrl);
            callback.onPaymentResult(request, result);
        } catch (PaymentException e) {
            callback.onPaymentException(request, e);
        }
    }

    PaymentResult responseToResult(Response response, String siteUrl) throws PaymentException {
        return response.getStatus() == 200
                ? extractResult(response, siteUrl)
                : extractError(response);
    }

    Entity<String> paymentEntity(PaymentRequest request) throws PaymentException {
        try {
            // convert the amount from pounds and pence to pence as worldpay expect
            String convertedAmount = amountConverter.convert(request.getAmount());
            request.setAmount(convertedAmount);

            String worldpayPaymentDocument = worldpayDocumentBuilder.buildPaymentDocument(request);
            return Entity.entity(worldpayPaymentDocument, MediaType.APPLICATION_XML_TYPE);
        } catch (Exception e) {
            throw new PaymentException("Unable to format xml", e);
        }
    }

    PaymentResult extractError(Response response) {
        String error = String.format("Failed to make payment: %s", response.getStatus());
        return PaymentResultBuilder.error(error).build();
    }

    PaymentResult extractResult(Response response, String siteUrl) throws PaymentException {
        InputStream inputStream = (InputStream) response.getEntity();
        PaymentResult result = worldpayDocumentParser.parseResponse(inputStream);

        if (result.isSuccess()) {
            // add the response urls to the end of the payment url that worldpay gave us
            String paymentUrlWithResponseUrls =
                    paymentUrlFormatter.formatPaymentUrl(
                            result.getPaymentUrl(),
                            result.getOrderCode(),
                            siteUrl);
            result.setPaymentUrl(paymentUrlWithResponseUrls);
        }
        return result;
    }



}
