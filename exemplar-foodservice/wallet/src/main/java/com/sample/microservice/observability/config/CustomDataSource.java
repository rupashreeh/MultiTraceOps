package com.sample.microservice.observability.config;



import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class CustomDataSource implements DataSource {

  private final DataSource dataSource;

  public CustomDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    }


  @Override
   public Connection getConnection() throws SQLException {
    Connection originalConnection = dataSource.getConnection();
    return CustomConnection.createProxy(originalConnection);
  }

  @Override // Implement other DataSource methods by delegating them to the original DataSource
  public Connection getConnection(String username, String password) throws SQLException {
    Connection originalConnection = dataSource.getConnection(username, password);
    return CustomConnection.createProxy(originalConnection);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return dataSource.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return dataSource.isWrapperFor(iface);
  }

  @Override
  public java.io.PrintWriter getLogWriter() throws SQLException {
    return dataSource.getLogWriter();
  }

  @Override
  public void setLogWriter(java.io.PrintWriter out) throws SQLException {
    dataSource.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    dataSource.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return dataSource.getLoginTimeout();
  }

  @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }

  // Implement other DataSource methods by delegating them to the original DataSource
  // ...
}
