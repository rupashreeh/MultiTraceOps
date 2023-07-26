package com.example.observability.collectors;

import com.example.observability.model.AppMetrics;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.lang.management.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestMetricsCollector {

  private static final String TOPIC_METRICS = "metrics";

  private final KafkaTemplate<String, String> kafkaTemplate=new KafkaTemplate<>(producerFactory());;

  public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    // Add any other Kafka producer configuration properties as needed

    return new DefaultKafkaProducerFactory<>(configProps);
  }
  void dbInsert(String log, long ts){
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
      Statement st = con.createStatement();
      String sql = "insert into appMetricsDB (ts, appMetricsData)  values (?, ?)";
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


  public void captureMetrics(String traceId, String layer, String event) {
    // Capture CPU Usage
    OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    double cpuLoad = osBean.getSystemLoadAverage(); // CPU load average

    // Capture Memory Usage
    MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
    long usedHeapMemory = heapUsage.getUsed();
    long committedHeapMemory = heapUsage.getCommitted();
    long maxHeapMemory = heapUsage.getMax();

    MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
    long usedNonHeapMemory = nonHeapUsage.getUsed();
    long committedNonHeapMemory = nonHeapUsage.getCommitted();
    long maxNonHeapMemory = nonHeapUsage.getMax();

    // Capture Thread Count
    ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadBean.getAllThreadIds(), true, true);
    int totalThreadCount = threadInfos.length;
    int blockedThreadCount = 0;
    int waitingThreadCount = 0;
    int timedWaitingThreadCount = 0;

    for (ThreadInfo threadInfo : threadInfos) {
      Thread.State threadState = threadInfo.getThreadState();
      if (threadState == Thread.State.BLOCKED) {
        blockedThreadCount++;
      } else if (threadState == Thread.State.WAITING) {
        waitingThreadCount++;
      } else if (threadState == Thread.State.TIMED_WAITING) {
        timedWaitingThreadCount++;
      }
    }

    // Capture Garbage Collection Metrics
    long gcTime = 0L;
    for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
      gcTime += gcBean.getCollectionTime();
    }

    long ts = System.currentTimeMillis();
    // Create MetricsData object
    AppMetrics metricsData = new AppMetrics();
    metricsData.setRid(traceId);
    metricsData.setCpuLoad(cpuLoad);
    metricsData.setUsedHeapMemory(usedHeapMemory);
    metricsData.setCommittedHeapMemory(committedHeapMemory);
    metricsData.setMaxHeapMemory(maxHeapMemory);
    metricsData.setUsedNonHeapMemory(usedNonHeapMemory);
    metricsData.setCommittedNonHeapMemory(committedNonHeapMemory);
    metricsData.setMaxNonHeapMemory(maxNonHeapMemory);
    metricsData.setTotalThreadCount(totalThreadCount);
    metricsData.setBlockedThreadCount(blockedThreadCount);
    metricsData.setWaitingThreadCount(waitingThreadCount);
    metricsData.setTimedWaitingThreadCount(timedWaitingThreadCount);
    metricsData.setGcTime(gcTime);
    metricsData.setLayer(layer);
    metricsData.setEvent(event);

    // Log or process the metrics as per your application's requirements
    System.out.println("Metrics captured for Trace ID: " + traceId);
    System.out.println(metricsData);
    metricsData.setTs(ts);
    dbInsert(metricsData.toString(),ts);
    // Send metrics to Kafka topic
    kafkaTemplate.send(TOPIC_METRICS, metricsData.toString());
  }
}

