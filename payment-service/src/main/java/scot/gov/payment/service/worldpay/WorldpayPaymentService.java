package scot.gov.payment.service.worldpay;

import org.w3c.dom.Document;
import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.*;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;

public class WorldpayPaymentService implements PaymentService {

    @Inject
    PaymentConfiguration configuration;

    @Inject
    WebTarget target;

    @Inject
    WorldpayDocumentBuilder worldpayDocumentBuilder;

    @Inject
    WorldpayDocumentParser worldpayDocumentParser;

    @Override
    public PaymentResult makePayment(PaymentRequest request) throws PaymentException {
        try {
            Response response = postToWorldPay(request);
            InputStream inputStream = (InputStream) response.getEntity();

            PaymentResult result = worldpayDocumentParser.parseResponse(inputStream);
            return result;
        } catch (Exception e) {
            throw new PaymentException("Failed to post to worldpay", e);
        }
    }

    Response postToWorldPay(PaymentRequest request) throws PaymentException {
        Entity<Document> entity = paymentEntity(request);
        return target.request().post(entity);
    }

    Entity<Document> paymentEntity(PaymentRequest request) throws PaymentException {
        try {
            Document worldpayPaymentDocument = worldpayDocumentBuilder.buildPaymentDocuemnt(request);
            return Entity.entity(worldpayPaymentDocument, MediaType.APPLICATION_XML_TYPE);
        } catch (ParserConfigurationException e) {
            throw new PaymentException("Unable to format xml", e);
        }
    }

}
