package scot.gov.payment.service.worldpay;

import org.jboss.resteasy.plugins.providers.ReaderProvider;
import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.payment.PaymentConfiguration;
import scot.gov.payment.service.PaymentRequest;

/**
 * Created by z418868 on 10/05/2022.
 */
public class WorldpayDocumentBuilderTest {

    @Test
    public void blah() throws Exception {
        WorldpayDocumentBuilder sut = new WorldpayDocumentBuilder();
        sut.configuration = Mockito.mock(PaymentConfiguration.class);
        PaymentConfiguration.Worldpay worldpay = Mockito.mock(PaymentConfiguration.Worldpay.class);
        Mockito.when(worldpay.getMerchantCode()).thenReturn("merchant");
        Mockito.when(worldpay.getInstallationId()).thenReturn("installId");
        Mockito.when(sut.configuration.getWorldpay()).thenReturn(worldpay);

        PaymentRequest request = new PaymentRequest();
        request.setAmount("10.0");
        request.setDescription("");
        request.setOrderCode("order");
        request.setEmailAddress("blah@google.com");
        String str = sut.buildPaymentDocument(request);
    }
}
