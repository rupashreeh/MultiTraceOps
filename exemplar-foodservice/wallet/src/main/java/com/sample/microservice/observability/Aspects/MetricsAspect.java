package com.sample.microservice.observability.Aspects;


import com.sample.microservice.observability.collectors.RequestMetricsCollector;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.UUID;

@Aspect
@Component
public class MetricsAspect {

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private RequestMetricsCollector metricsCollector;

  @Before("@annotation(com.sample.microservice.observability.annotation.MetricLog)")
  public void logMethodEntry(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    String traceId = (String) request.getAttribute("rid");
    if (traceId == null || traceId.isEmpty()) {
      System.out.println("Empty traceId");
    }

    // Capture metrics using RequestMetricsCollector and associate them with the trace ID
    metricsCollector.captureMetrics(traceId, "application","entry");

  }

  @AfterReturning("@annotation(com.sample.microservice.observability.annotation.MetricLog)")
  public void logMethodExit(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    System.out.println("Method Exit: " + methodName);

    String traceId = (String) request.getAttribute("rid");
    if (traceId == null || traceId.isEmpty()) {
      System.out.println("Empty traceId");
    }

    // Capture metrics using RequestMetricsCollector and associate them with the trace ID
    metricsCollector.captureMetrics(traceId,"application","exit");

    // Perform any other actions after method execution
  }
}

