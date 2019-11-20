package scot.gov.payment.service.worldpay;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.PaymentRequest;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Builds a Document suitable for posting to WorldPay containing the relevant values from the configuration
 * and a PaymentRequest
 */
public class WorldpayDocumentBuilder {

    @Inject
    PaymentConfiguration configuration;

    public Document buildPaymentDocuemnt(PaymentRequest request) throws ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactorySource.get().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        appendDTD(document);
        appendPaymentService(document, request);
        return document;
    }

    void appendDTD(Document document) {
        DOMImplementation domImpl = document.getImplementation();
        DocumentType doctype = domImpl.createDocumentType(
                "paymentService",
                "-//WorldPay//DTD WorldPay PaymentService v1//EN",
                "http://dtd.worldpay.com/paymentService_v1.dtd");
        document.appendChild(doctype);
    }

    Element appendPaymentService(Document document, PaymentRequest request) {
        Element element   = document.createElement("paymentService");
        element.setAttribute("version", "1.4");
        element.setAttribute("merchantCode", configuration.getWorldPay().getMerchantCode());
        appendSubmit(document, element, request);
        document.appendChild(element);
        return element;
    }

    Element appendSubmit(Document document, Element parent, PaymentRequest request) {
        Element element = document.createElement("submit");
        appendOrder(document, element, request);
        parent.appendChild(element);
        return element;
    }

    Element appendOrder(Document document, Element parent, PaymentRequest request) {
        Element element = document.createElement("order");
        element.setAttribute("orderCode", request.getOrderCode());
        element.setAttribute("instalationId", configuration.getWorldPay().getInstalationId());
        appendDescription(document, element, request);
        appendAmount(document, element, request);
        appendPaymentMethodMask(document, element);
        parent.appendChild(element);
        return element;
    }

    Element appendDescription(Document document, Element parent, PaymentRequest request) {
        Element element = document.createElement("description");
        element.appendChild(document.createTextNode(request.getDescription()));
        parent.appendChild(element);
        return element;
    }

    Element appendAmount(Document document, Element parent, PaymentRequest request) {
        Element element = document.createElement("amount");
        element.setAttribute("currencyCode", "GBP");
        element.setAttribute("exponent", "2"); // TODO: find explanation in docs
        element.setAttribute("value", request.getAmount());
        parent.appendChild(element);
        return element;
    }

    Element appendPaymentMethodMask(Document document, Element parent) {
        Element element = document.createElement("paymentMethodMask");
        parent.appendChild(element);
        appendInclude(document, element);
        return element;
    }

    Element appendInclude(Document document, Element parent) {
        Element element = document.createElement("include");
        element.setAttribute("code", "ALL");
        parent.appendChild(element);
        return element;
    }

}
