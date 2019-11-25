package scot.gov.payment.rest;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.entries;

public class RequestLogger implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogger.class);

    private static final String START_PROPERTY = "start";

    @Inject
    public RequestLogger() {
        // Default constructor
    }

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        Instant start = Instant.now();
        context.setProperty(START_PROPERTY, start);

        MDC.put("url.path", context.getUriInfo().getPath());
        MDC.put("http.request.method", context.getRequest().getMethod());

        String query = context.getUriInfo().getRequestUri().getQuery();
        if (query != null) {
            MDC.put("url.query", query);
        }
    }

    @Override
    public void filter(ContainerRequestContext context, ContainerResponseContext response)
            throws IOException {

        Instant start = (Instant) context.getProperty(START_PROPERTY);
        Instant end = Instant.now();

        Map<String, String> event = new HashMap<>();
        event.put("event.start", start.toString());
        event.put("event.end", end.toString());
        event.put("event.duration", Long.toString(Duration.between(start, end).toMillis()));
        event.put("event.outcome", outcome(response.getStatusInfo().getFamily()));

        event.put("http.response.status_code", Integer.toString(response.getStatus()));

        String method = context.getRequest().getMethod();
        String path = context.getUriInfo().getPath();
        int status = response.getStatus();
        LOGGER.info("{} {} {} {}",
                status,
                method,
                path,
                entries(event));
    }

    static String outcome(Response.Status.Family family) {
        switch (family) {
            case SUCCESSFUL:
                return "success";
            case SERVER_ERROR:
                return "failure";
            case CLIENT_ERROR:
                return "failure";
            default:
                return "unknown";
        }
    }

}