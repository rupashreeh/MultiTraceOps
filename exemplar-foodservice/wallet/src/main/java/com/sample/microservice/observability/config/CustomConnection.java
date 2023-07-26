package com.sample.microservice.observability.config;


import com.sample.microservice.observability.JMXClient;
import com.sample.microservice.observability.model.DBMetrics;
import com.sample.microservice.observability.model.LogRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomConnection implements InvocationHandler {

  private Connection originalConnection;

  private String rid;

    private final KafkaTemplate<String, String> kafkaTemplate=new KafkaTemplate<>(producerFactory());;

    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Add any other Kafka producer configuration properties as needed

        return new DefaultKafkaProducerFactory<>(configProps);
    }

  private static JMXClient jmxClient = new JMXClient();


  public CustomConnection(Connection originalConnection, String rid) {
    this.originalConnection = originalConnection;
       this.rid = rid;
  }

    void dbInsertLog(String log, long ts){
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


    void dbInsertMetric(String log, long ts){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
            Statement st = con.createStatement();
            String sql = "insert into dbMetricsDB (ts, dbMetricsData)  values (?, ?)";
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

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      long startTimestamp = System.currentTimeMillis();

      // Intercept execute methods of Statement and PreparedStatement
    if (method.getName().equals("execute") ||
        method.getName().equals("executeQuery") ||
            method.getName().equals("prepareStatement") ||
        method.getName().equals("executeUpdate")) {
      // Check if the method has the DBLog annotation
        if (rid.isEmpty()) {
          rid = UUID.randomUUID().toString();
        }
        String cm = method.getName();
        String layer = "database";
        String event = "entry";
        long ts = System.currentTimeMillis();
        LogRecord dbLogEntry = new LogRecord(rid, layer, event, cm, ts);
        dbInsertLog(dbLogEntry.toString(), ts);
        kafkaTemplate.send("logs", dbLogEntry.toString());

        // Execute the method call on the original statement/prepared statement
        Object result = method.invoke(originalConnection, args);


        layer = "database";
        event = "exit";
        cm = method.getName();
        ts = System.currentTimeMillis();

        // Log the database exit event to Kafka logs topic or wherever you need to log it
        // You can use a KafkaTemplate or any other method for logging

        LogRecord dbLogExit = new LogRecord(rid, layer, event, cm, ts);
        dbInsertLog(dbLogEntry.toString(), ts);

        kafkaTemplate.send("logs", dbLogExit.toString());

        ObjectName poolObjectName = new ObjectName("com.zaxxer.hikari:type=Pool (exemplar-pool)");
        //int activeConnections = (int) jmxClient.getAttribute(poolObjectName, "ActiveConnections");
       // int totalConnections = (int) jmxClient.getAttribute(poolObjectName, "TotalConnections");
      //  int idleConnections = (int) jmxClient.getAttribute(poolObjectName, "IdleConnections");

     //   Integer throughput = (Integer) jmxClient.getAttribute(poolObjectName, "Throughput");

        // ... Retrieve other relevant metrics as needed

        // Get query execution time using JDBC API
        // Execute your JDBC query here

        // Log or store the JDBC metrics to Kafka or any other destination
        DBMetrics dbMetricsLog = new DBMetrics();
        dbMetricsLog.setRequestId(rid);
        dbMetricsLog.setLayer("database");
        dbMetricsLog.setEvent("metrics");
        dbMetricsLog.setCurrentMethod(method.getName());
        dbMetricsLog.setTimestamp(System.currentTimeMillis());
       // dbMetricsLog.setThroughput(throughput);
        //dbMetricsLog.setActiveConnections(activeConnections);
       // dbMetricsLog.setIdleConnections(idleConnections);

        // ... Set other JDBC metrics as needed
        kafkaTemplate.send("metrics", dbMetricsLog.toString());
        long endTimestamp = System.currentTimeMillis();
        long queryExecutionTime = endTimestamp - startTimestamp;
        if (queryExecutionTime > 10000) {
            dbMetricsLog.setSlowQuery(true);
        }
        dbMetricsLog.setQueryExecutionTime(queryExecutionTime);
        dbInsertMetric(dbMetricsLog.toString(), ts);


        return result;
      }

    // If the method is not intercepted, delegate the method call to the original connection
    return method.invoke(originalConnection, args);
  }

  // Utility method to create a proxy connection
  public static Connection createProxy(Connection connection) {
      // Get the current request from the RequestContextHolder
      String traceId="";
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
      // Extract the traceId from the "X-Amzn-TraceId" header
      if (request != null) {
           traceId = (String) request.getAttribute("rid");
      }
      // Create the proxy connection with the traceId
      return (Connection) Proxy.newProxyInstance(
              CustomConnection.class.getClassLoader(),
              new Class[]{Connection.class},
              new CustomConnection(connection, traceId)
      );
  }

}
