package scot.gov.payment.service.worldpay;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentResult;
import scot.gov.payment.service.PaymentResultBuilder;

import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;

public class WorldpayDocumentParser {

    public PaymentResult parseResponse(InputStream inputStream) throws PaymentException {

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactorySource.get().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            return extractResultFromDocument(document);
        } catch (Exception e) {
            throw new PaymentException("Failed to process result from worldpay", e);
        }
    }

    PaymentResult extractResultFromDocument(Document document) {
        return isError(document)
                ? extractErrorFromDocument(document)
                : extractSuccessFromDocument(document);
    }

    boolean isError(Document document) {
        return getErrorElements(document).getLength() >= 1;
    }

    PaymentResult extractErrorFromDocument(Document document) {
        NodeList errors = getErrorElements(document);
        Element errorElement = (Element) errors.item(0);
        String error = errorElement.getTextContent();

        return PaymentResultBuilder
                .error(error)
                .build();
    }

    NodeList getErrorElements(Document document) {
        return document.getElementsByTagName("error");
    }

    PaymentResult extractSuccessFromDocument(Document document) {
        Element orderStatusElement = (Element) document.getElementsByTagName("orderStatus").item(0);
        Element referenceElement = (Element) document.getElementsByTagName("reference").item(0);
        String orderCode = orderStatusElement.getAttribute("orderCode");
        String referenceId = referenceElement.getAttribute("id");
        String paymentUrl = referenceElement.getTextContent();

        return PaymentResultBuilder
                .success()
                .orderCode(orderCode)
                .referenceId(referenceId)
                .paymentUrl(paymentUrl)
                .build();
    }
}
