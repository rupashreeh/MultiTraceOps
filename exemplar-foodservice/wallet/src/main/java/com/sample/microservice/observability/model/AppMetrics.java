package com.sample.microservice.observability.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppMetrics {
  private String rid;     // Request ID
  private String layer;   // Layer name (Load Balancer, Application, Database)
  private String event;   // Layer event (e.g., Load Balancer Entry, Database Entry, etc.)
  private String cm;      // Current Method (controller method in the Application Layer)
  private long ts;        // Timestamp in milliseconds
  private double cpuLoad;
  private long usedHeapMemory;
  private long committedHeapMemory;
  private long maxHeapMemory;
  private long usedNonHeapMemory;
  private long committedNonHeapMemory;
  private long maxNonHeapMemory;
  private int totalThreadCount;
  private int blockedThreadCount;
  private int waitingThreadCount;
  private int timedWaitingThreadCount;
  private long gcTime;

  // Add constructors, getters, and setters
}

