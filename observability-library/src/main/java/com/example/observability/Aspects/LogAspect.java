package com.example.observability.Aspects;

import com.example.observability.model.LogRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
public class LogAspect {

  private static final String TOPIC_LOG = "log";

  private KafkaTemplate<String, String> kafkaTemplate=new KafkaTemplate<>(producerFactory());;

  @Autowired
  private HttpServletRequest request;

  private static final RestTemplate restTemplate = new RestTemplate();

  void dbInsert(String log, long ts){
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
      Statement st = con.createStatement();
      String sql = "insert into logDB (ts, logData)  values (?, ?)";
      PreparedStatement preparedStmt = con.prepareStatement(sql);
      preparedStmt.setString (1, String.valueOf(ts));
      preparedStmt.setString (2, log);
      preparedStmt.execute();
      con.close();
    } catch (InstantiationException e) {
      // System.err.println("Got an exception!");
      // throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      // System.err.println("Got an exception1!");

      // throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      //System.err.println("Got an exception2!");

      // throw new RuntimeException(e);
    } catch (SQLException e) {
      //System.err.println("Got an exception3!");
      // throw new RuntimeException(e);
    }
  }
  public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    // Add any other Kafka producer configuration properties as needed

    return new DefaultKafkaProducerFactory<>(configProps);
  }
  @Before("@annotation(com.example.observability.annotation.Log)")
  public void logMethodEntry(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    String traceId = (String) request.getAttribute("rid");
    if (traceId == null || traceId.isEmpty()) {
      traceId = UUID.randomUUID().toString();
      HttpServletRequest request = (HttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
              .getRequest();
      request.setAttribute("rid", traceId);
    }
    long ts = System.currentTimeMillis();
    JSONObject json = new JSONObject();
    json.put("rid",traceId);
    json.put("layer","loadbalancer");
    json.put("event","entry");
    json.put("m", request.getMethod());
    json.put("ts", ts);
    dbInsert(json.toString(),ts );
    kafkaTemplate.send(TOPIC_LOG, json.toString());

    ts =  System.currentTimeMillis();
    LogRecord entryLog = new LogRecord(traceId, "application", "entry", methodName, ts);
    dbInsert(entryLog.toString(),ts );

    kafkaTemplate.send(TOPIC_LOG, entryLog.toString());
  }

  @AfterReturning("@annotation(com.example.observability.annotation.Log)")
  public void logMethodExit(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    String traceId = (String) request.getAttribute("rid");
    Long ts = System.currentTimeMillis();
    String exitLog = String.valueOf(new LogRecord(traceId, "application", "exit", methodName, ts));
    dbInsert(exitLog.toString(), ts);
    kafkaTemplate.send(TOPIC_LOG, exitLog);
  }

  @Before("@annotation(com.example.observability.annotation.Log) && execution(public * *(..))")
  public void logApplicationContinue(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    String traceId = (String) request.getAttribute("rid");
    // Detect HTTP calls made within the method
    // For example, check if the RestTemplate is used for HTTP calls
    boolean isHttpCall = detectHttpCall();

    if (isHttpCall) {
      Long ts = System.currentTimeMillis();
      String continueLog = String.valueOf(new LogRecord(traceId, "application", "continue", methodName, System.currentTimeMillis()));
      dbInsert(continueLog.toString(), ts);
      kafkaTemplate.send(TOPIC_LOG, continueLog);
    }
  }

  private boolean detectHttpCall() {
    // Implement the logic to detect HTTP calls within the method
    // For example, check if the RestTemplate or WebClient is used for HTTP calls
    // You can customize this logic based on your specific use case
    // For demonstration purposes, we check if the RestTemplate is used here
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    for (StackTraceElement stackTraceElement : stackTraceElements) {
      if (stackTraceElement.getClassName().startsWith("org.springframework.web.client.RestTemplate")
          || stackTraceElement.getClassName().startsWith("org.springframework.web.reactive.function.client.WebClient")) {
        return true;
      }
    }
    return false;
  }
}
