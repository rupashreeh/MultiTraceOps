package com.example.observability.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LogRecord {
  private String rid;     // Request ID
  private String layer;   // Layer name (Load Balancer, Application, Database)
  private String event;   // Layer event (e.g., Load Balancer Entry, Database Entry, etc.)
  private String cm;      // Current Method (controller method in the Application Layer)
  private long ts;        // Timestamp in milliseconds
}

