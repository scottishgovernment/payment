package scot.gov.payment.service.worldpay;

import org.junit.Test;
import scot.gov.payment.service.PaymentException;
import scot.gov.payment.service.PaymentResult;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by z418868 on 20/11/2019.
 */
public class WorldpayDocumentParserTest {

    @Test
    public void canParseSuccessResponse() throws Exception {

        // ARRANGE
        WorldpayDocumentParser sut = new WorldpayDocumentParser();
        InputStream in = WorldpayDocumentParserTest.class.getResourceAsStream("/successResponse.xml");

        // ACT
        PaymentResult result = sut.parseResponse(in);

        // ASSERT
        assertTrue(result.isSuccess());
        assertNull(result.getError());
        assertEquals("https://hpp-sandbox.worldpay.com/",
                result.getPaymentUrl());
        assertEquals("ORDER3", result.getOrderCode());
        assertEquals("3157597444", result.getReferenceId());
    }

    @Test
    public void canParseErrorResponse() throws Exception {

        // ARRANGE
        WorldpayDocumentParser sut = new WorldpayDocumentParser();
        InputStream in = WorldpayDocumentParserTest.class.getResourceAsStream("/errorResponse.xml");

        // ACT
        PaymentResult result = sut.parseResponse(in);

        // ASSERT
        assertFalse(result.isSuccess());
        assertEquals("Duplicate Order", result.getError());
    }

    @Test(expected = PaymentException.class)
    public void invalidXMLThrowsPaymentException() throws Exception {
        // ARRANGE
        WorldpayDocumentParser sut = new WorldpayDocumentParser();
        InputStream in = WorldpayDocumentParserTest.class.getResourceAsStream("/truncatedResponse.xml");

        // ACT
        sut.parseResponse(in);

        // ASSERT -- see expected exception
    }

}
