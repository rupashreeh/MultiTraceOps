package com.example.observability.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DBMetrics {
  private String requestId; // Request ID
  private String layer; // Layer name (Load Balancer, Application, Database)
  private String event; // Layer event (e.g., Entry, Exit, Metrics, etc.)
  private String currentMethod; // Current Method (controller method in the Application Layer)
  private long timestamp; // Timestamp in milliseconds
  private long queryExecutionTime; // Query execution time in milliseconds
  private int throughput; // Number of queries processed per unit of time
  private long responseTime; // Time taken to respond to a client request
  private int activeConnections; // Number of active connections in the connection pool
  private int idleConnections; // Number of idle connections in the connection pool
  private int lockContention; // Number of times locks are requested but unavailable
  private double indexHitRate; // Hit rate of database indexes
  private double bufferCacheHitRatio; // Buffer cache hit ratio
  private int tableScans; // Number of table scans performed
  private int deadlockOccurrences; // Number of times deadlocks occur
  private long memoryUsage; // Memory consumed by the database process in bytes
  private long storageUsage; // Disk space used by the database in bytes
  private int connectionErrors; // Number of failed connection attempts to the database
  private long replicationLag; // Replication lag for replicated databases in milliseconds
  private int transactionRollbacks; // Number of transaction rollbacks
  private int databaseErrors; // Number of database errors
  private double cacheHitRatio; // Cache hit ratio when retrieving data from caches
  private boolean isSlowQuery; // Number of queries that take longer than a predefined threshold
  private double readToWriteRatio; // Read-to-write ratio
}

