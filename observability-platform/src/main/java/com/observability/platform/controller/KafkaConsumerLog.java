package com.observability.platform.controller;

import java.sql.*;
import java.time.Instant;
import java.util.List;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Component

// Class
public class KafkaConsumerLog {

    public KafkaConsumerLog(){
    }
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = {"logs"}, groupId = "group_id",containerFactory = "logKafkaListenerContainerFactory")

    // Method
    public void
    consume(String message)
    {
        System.out.println("The message is :"+ message);
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");

            Statement st = con.createStatement();
            long tsN = System.currentTimeMillis();
            String sql = ("insert into logDB(ts, logData) values(?,?)");
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setLong(1, tsN);
            preparedStatement.setString(2, message);
            preparedStatement.executeUpdate();
            con.close();
        }catch(SQLException | ClassNotFoundException s){
            s.printStackTrace();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @KafkaListener(topics = "appmetrics", groupId = "app_metrics_group_id", containerFactory = "appMetricsKafkaListenerContainerFactory")
    public void consumeAppMetrics(String message) {
        // Process the message from the 'appmetrics' topic
        System.out.println("Received message from 'appmetrics' topic: " + message);

        // Your app metrics processing logic here...
        // For example, you can insert the app metrics data into the 'appmetrics' table.
        try {
            String jdbcUrl = "jdbc:mysql://localhost/test";
            String username = "root";
            String password = "password";

            try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
                long tsN = System.currentTimeMillis();
                String sql = "INSERT INTO appmetrics(ts, appMetricsData) VALUES (?, ?)";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setLong(1, tsN);
                preparedStatement.setString(2, message);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "dbmetrics", groupId = "db_metrics_group_id", containerFactory = "dbMetricsKafkaListenerContainerFactory")
    public void consumeDbMetrics(String message) {
        // Process the message from the 'dbmetrics' topic
        System.out.println("Received message from 'dbmetrics' topic: " + message);

        // Your database metrics processing logic here...
        // For example, you can insert the database metrics data into the 'dbmetrics' table.
        try {
            String jdbcUrl = "jdbc:mysql://localhost/test";
            String username = "root";
            String password = "password";

            try (Connection con = DriverManager.getConnection(jdbcUrl, username, password)) {
                long tsN = System.currentTimeMillis();
                String sql = "INSERT INTO dbmetrics(ts, dbMetricsData) VALUES (?, ?)";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setLong(1, tsN);
                preparedStatement.setString(2, message);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            // Handle any potential exceptions
            e.printStackTrace();
        }
    }
}

