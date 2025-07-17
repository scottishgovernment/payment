package scot.gov.payment.service.worldpay;

import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.PaymentRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 10/05/2022.
 */
public class WorldpayDocumentBuilderTest {

    @Test
    public void blah() throws Exception {
        WorldpayDocumentBuilder sut = new WorldpayDocumentBuilder();
        sut.configuration = Mockito.mock(PaymentConfiguration.class);
        PaymentConfiguration.Worldpay worldpay = Mockito.mock(PaymentConfiguration.Worldpay.class);
        when(worldpay.getMerchantCode()).thenReturn("merchant");
        when(worldpay.getInstallationId()).thenReturn("installId");
        when(sut.configuration.getWorldpay()).thenReturn(worldpay);

        PaymentRequest request = new PaymentRequest();
        request.setAmount("10.0");
        request.setDescription("");
        request.setOrderCode("order");
        request.setEmailAddress("blah@google.com");
        String str = sut.buildPaymentDocument(request);

        Document document = parseXml(str);
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        assertEquals("10.0", xPath.evaluate("/paymentService/submit/order/amount/@value", document));
    }

    private static Document parseXml(String str) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
    }
}
