package scot.gov.payment.service.worldpay;

import scot.gov.payment.service.*;
import scot.gov.payment.service.worldpay.responseurls.PaymentUrlFormatter;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
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

    @Override
    public PaymentResult makePayment(PaymentRequest request, String siteUrl) throws PaymentException {
        try {
            return doPayment(request, siteUrl);
        } catch (Exception e) {
            throw new PaymentException("Failed to post to worldpay", e);
        }
    }

    PaymentResult doPayment(PaymentRequest request, String siteUrl) throws PaymentException {
        Response response = postToWorldPay(request);
        return response.getStatus() == 200
                ? extractResult(response, siteUrl)
                : extractError(response);
    }

    Response postToWorldPay(PaymentRequest request) throws PaymentException {
        Entity<String> entity = paymentEntity(request);
        return target.request().post(entity);
    }

    Entity<String> paymentEntity(PaymentRequest request) throws PaymentException {
        try {
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
