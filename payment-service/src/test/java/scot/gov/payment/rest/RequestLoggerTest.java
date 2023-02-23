package scot.gov.payment.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static scot.gov.payment.rest.RequestLogger.*;

public class RequestLoggerTest {

    @Before
    public void clerarMDC() {
        MDC.clear();
    }

    @Test
    public void successfulResponseHasSuccessfulOutcome() {
        assertEquals("success", outcome(Response.Status.Family.SUCCESSFUL));
    }

    @Test
    public void clientErrorHasFailureOutcome() {
        assertEquals("failure", outcome(Response.Status.Family.CLIENT_ERROR));
    }

    @Test
    public void serverErrorHasFailureOutcome() {
        assertEquals("failure", outcome(Response.Status.Family.SERVER_ERROR));
    }

    @Test
    public void otherResponseHasUnknownOutcome() {
        assertEquals("unknown", outcome(Response.Status.Family.INFORMATIONAL));
    }

    @Test
    public void filterWithQuerySetsExpectedMDCProperties() throws Exception {

        // ARRANGE
        ContainerRequestContext context = requestContext("https://www.gov.scot/?query");
        RequestLogger sut = new RequestLogger();

        // ACT
        sut.filter(context);

        // ASSERT
        assertEquals("path", MDC.get("url.path"));
        assertEquals("method", MDC.get("http.request.method"));
        assertEquals("query", MDC.get("url.query"));
    }

    @Test
    public void filterWithNoQuerySetsExpectedMDCProperties() throws Exception {

        // ARRANGE
        ContainerRequestContext context = requestContext("https://www.gov.scot/");
        RequestLogger sut = new RequestLogger();

        // ACT
        sut.filter(context);

        // ASSERT
        assertEquals("path", MDC.get("url.path"));
        assertEquals("method", MDC.get("http.request.method"));
        assertNull(MDC.get("url.query"));
    }


    ContainerRequestContext requestContext(String uri) throws Exception {
        ContainerRequestContext context = mock(ContainerRequestContext.class);
        Request request = mock(Request.class);
        UriInfo uriInfo = mock(UriInfo.class);
        URI requestURI = new URI(uri);
        when(context.getRequest()).thenReturn(request);
        when(context.getUriInfo()).thenReturn(uriInfo);
        when(uriInfo.getPath()).thenReturn("path");
        when(request.getMethod()).thenReturn("method");
        when(uriInfo.getRequestUri()).thenReturn(requestURI);
        return context;
    }

//    @Override
//    public void filter(ContainerRequestContext context) throws IOException {
//        Instant start = Instant.now();
//        context.setProperty(START_PROPERTY, start);
//
//        MDC.put("url.path", context.getUriInfo().getPath());
//        MDC.put("http.request.method", context.getRequest().getMethod());
//
//        String query = context.getUriInfo().getRequestUri().getQuery();
//        if (query != null) {
//            MDC.put("url.query", query);
//        }
//    }


}