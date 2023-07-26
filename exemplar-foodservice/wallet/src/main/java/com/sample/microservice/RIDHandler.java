package com.sample.microservice;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class RIDHandler extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Amzn-TraceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID_HEADER);

        if (traceId == null || traceId.isEmpty()) {
            // Generate a UUID as the trace ID
            traceId = UUID.randomUUID().toString();
            // Set the trace ID in the response header
            response.setHeader("rid", traceId);
            System.out.println("Request Id is" + traceId);
        }

        // Pass the request along the filter chain
        filterChain.doFilter(request, response);
    }
}
