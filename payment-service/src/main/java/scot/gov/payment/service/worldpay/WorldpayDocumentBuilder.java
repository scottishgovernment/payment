package scot.gov.payment.service.worldpay;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.PaymentRequest;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Builds a Document suitable for posting to Worldpay containing the relevant values from the configuration
 * and a PaymentRequest
 */
public class WorldpayDocumentBuilder {

    private static final String DOCTYPE_PUBLIC = "-//Worldpay//DTD Worldpay PaymentService v1//EN";

    private static final String DOCTYPE_PRIVATE = "http://dtd.worldpay.com/paymentService_v1.dtd";

    @Inject
    PaymentConfiguration configuration;

    @Inject
    WorldpayDocumentBuilder() {
        // Default constructor
    }

    public String buildPaymentDocument(PaymentRequest request)
            throws ParserConfigurationException, TransformerException {

        DocumentBuilder documentBuilder = DocumentBuilderFactorySource.get().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        appendDTD(document);
        appendPaymentService(document, request);
        return toXML(document);
    }

    void appendDTD(Document document) {
        DOMImplementation domImpl = document.getImplementation();
        DocumentType doctype = domImpl.createDocumentType("paymentService", DOCTYPE_PUBLIC, DOCTYPE_PRIVATE);
        document.appendChild(doctype);
    }

    Element appendPaymentService(Document document, PaymentRequest request) {
        Element element   = document.createElement("paymentService");
        element.setAttribute("version", "1.4");
        element.setAttribute("merchantCode", configuration.getWorldpay().getMerchantCode());
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
        element.setAttribute("installationId", configuration.getWorldpay().getInstallationId());
        appendDescription(document, element, request);
        appendAmount(document, element, request);
        appendPaymentMethodMask(document, element);
        appendShopper(document, element, request);
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
        element.setAttribute("exponent", "2");
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

    Element appendShopper(Document document, Element parent, PaymentRequest request) {
        Element element = document.createElement("shopper");
        parent.appendChild(element);
        appendShopperEmail(document, element, request);
        return element;
    }

    Element appendShopperEmail(Document document, Element parent, PaymentRequest request) {
        Element element = document.createElement("shopperEmailAddress");
        element.appendChild(document.createTextNode(request.getEmailAddress()));
        parent.appendChild(element);
        return element;
    }

    String toXML(Document document) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, DOCTYPE_PUBLIC);
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DOCTYPE_PRIVATE);
        DOMSource source = new DOMSource(document);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        transformer.transform(source, result);
        return out.toString(UTF_8);
    }

}
