package com.sample.microservice.observability;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class JMXClient {

  private final MBeanServerConnection mBeanServer;

  public JMXClient() {
    mBeanServer = ManagementFactory.getPlatformMBeanServer();
  }

  public Object getAttribute(ObjectName objectName, String attributeName) throws Exception {
    return mBeanServer.getAttribute(objectName, attributeName);
  }
}

