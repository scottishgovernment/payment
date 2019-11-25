package scot.gov.payment.rest.listeners;

import com.codahale.metrics.MetricRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.payment.service.PaymentRequest;
import scot.gov.payment.service.PaymentResultBuilder;

import static org.junit.Assert.assertEquals;

public class MetricCollectingResourceListenerTest {

    @Test
    public void countsErrorsCorrectly() {
        // ARRANGE
        MetricRegistry metricRegistry = new MetricRegistry();
        MetricCollectingResourceListener sut = new MetricCollectingResourceListener(metricRegistry);

        // ACT
        sut.onPaymentResult(new PaymentRequest(), new PaymentResultBuilder().error("error").build());
        sut.onPaymentResult(new PaymentRequest(), new PaymentResultBuilder().success().build());

        // ASSERT
        assertEquals(1, sut.errorCounter.getCount());
        assertEquals(1, sut.errorMeter.getCount());
    }
}
