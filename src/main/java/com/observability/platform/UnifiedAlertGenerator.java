package com.observability.platform;

import com.observability.platform.config.InputProperties;
import com.twilio.Twilio;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import com.twilio.type.PhoneNumber;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;

public class UnifiedAlertGenerator {
    static String data = "";

    private static Properties getProperties(){
             Properties properties = new Properties();
            final ClassLoader loader = UnifiedAlertGenerator.class.getClassLoader();
                try(InputStream config = loader.getResourceAsStream("input.properties")){
                     properties.load(config);
                     System.out.println("Properties             "+properties.toString());
                    } catch(IOException e){
                        throw new IOError(e);
                    }
                return properties;
            }

    public static void sendMail(String recipientEmail, String data, String emailSubject){
        // Recipient's email ID needs to be mentioned.
        String to = recipientEmail;

        // Sender's email ID needs to be mentioned
        String from = "observability39@gmail.com";

        // Assuming you are sending email from through gmails smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", "smtp.gmail.com");                         
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("observabilityplatform@gmail.com", "kcubmadldbfgbosq");
            }
        });

        // Used to debug SMTP issues
        session.setDebug(true);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(emailSubject);

            // Now set the actual message
            message.setText(data);

            System.out.println("Sending Email Message...");
            // Send message
            Transport.send(message);
            System.out.println("Sent email message successfully....");

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

    public static void sendSMS(String recipientNumber, String data, String smsSubject) {
        System.out.println("sending SMS Message...");
        Twilio.init("AC1d44676793d9360f837d31ad7581e747", "79f0068988c54e389767787b7f599ddd");

        com.twilio.rest.api.v2010.account.Message smsmessage = com.twilio.rest.api.v2010.account.Message.creator(new PhoneNumber(recipientNumber),
                new PhoneNumber("+16692718736"),smsSubject + " :" +data).create();
        System.out.println("Sent SMS message successfully....");
        System.out.println(smsmessage.getSid());

    }

    public static void checkAppMetricAlerts(){
        Properties properties = getProperties();
        boolean sendSms = (boolean) properties.get("sendSms");
        boolean sendEmail = (boolean) properties.get("sendEmail");
        boolean sendFileSystem = (boolean) properties.get("sendFileSystem");
        String recipientEmail  = (String) properties.get("recipientEmail");
        String recipientNumber = (String) properties.get("recipientPhoneNumber");
        Integer cpuLoad = (Integer) properties.get("cpuLoad");
        Integer memoryUsage = (Integer) properties.get("memoryUsage");
        List<String> cpuIds = new LinkedList<>();
        List<String> memIds = new LinkedList<>();
        Map<String, ArrayList<String>> traceMap = new HashMap<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
            Statement st = con.createStatement();
            long time = System.currentTimeMillis() - 300000;
            String sql = "select * from appMetricsDB where ts >= " + time;
            PreparedStatement preparedStmt = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                long id = rs.getLong("ts");
                String str1 = rs.getString("appMetricsData");
                JSONObject obj = new JSONObject(str1);
                String rid = obj.get("rid").toString();
                if(traceMap.containsKey(rid)){
                    ArrayList<String> list = traceMap.get(rid);
                    list.add(str1);
                    traceMap.put(rid, list);
                }   else{
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str1);
                    traceMap.put(rid, list);
                }

                if (Double.parseDouble((String) obj.get("cpuLoad")) >= cpuLoad)
                    cpuIds.add(rid);
                if (Long.parseLong((String) obj.get("usedHeapMemory")) >= memoryUsage)
                    memIds.add(rid);
            }
            Path filePath = Paths.get(System.getProperty("user.home")+"/appMetricsCpu.txt");
            for(String cpuId : cpuIds){
               String subject = "CPU Load exceeded threshold for Request Id: "+cpuId;
               List<String> data =   traceMap.get(cpuId);
               StringBuffer message = new StringBuffer();
               for(String d: data){
                   message.append(d).append(System.getProperty("line.separator"));
               }
                if(sendFileSystem)
                    Files.writeString(filePath, subject+ message.toString(), StandardOpenOption.CREATE);
                if(sendEmail)
                    sendMail(recipientEmail, message.toString(), subject);
                if(sendSms)
                    sendSMS(recipientNumber,  message.toString(), subject);
            }
            filePath = Paths.get(System.getProperty("user.home")+"/appMetricsMemory.txt");
            for(String memId : memIds){
                String subject = "Memory Usage exceeded threshold for Request Id: "+memId;
                List<String> data =   traceMap.get(memId);
                StringBuffer message = new StringBuffer();
                for(String d: data){
                    message.append(d).append(System.getProperty("line.separator"));
                }
                if(sendFileSystem)
                    Files.writeString(filePath, subject+ message.toString(), StandardOpenOption.CREATE);
                if(sendEmail)
                    sendMail(recipientEmail, message.toString(), subject);
                if(sendSms)
                    sendSMS(recipientNumber,  message.toString(), subject);
            }
        }catch(Exception e)  {

        }
    }

    public static void checkDBMetricAlerts(){
        Properties properties = getProperties();
        boolean sendSms = (boolean) properties.get("sendSms");
        boolean sendEmail = (boolean) properties.get("sendEmail");
        boolean sendFileSystem = (boolean) properties.get("sendFileSystem");
        String recipientEmail  = (String) properties.get("recipientEmail");
        String recipientNumber = (String) properties.get("recipientPhoneNumber");
        Long queryExecutionTime = (Long) properties.get("queryExecutionTime");
        List<String> slowQuery = new LinkedList<>();
        Map<String, ArrayList<String>> traceMap = new HashMap<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
            Statement st = con.createStatement();
            long time = System.currentTimeMillis() - 300000;
            String sql = "select * from dbMetricsDB where ts >= " + time;
            PreparedStatement preparedStmt = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                long id = rs.getLong("ts");
                String str1 = rs.getString("dbMetricsData");
                JSONObject obj = new JSONObject(str1);
                String rid = obj.get("rid").toString();
                if (traceMap.containsKey(rid)) {
                    ArrayList<String> list = traceMap.get(rid);
                    list.add(str1);
                    traceMap.put(rid, list);
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str1);
                    traceMap.put(rid, list);
                }

                if (Double.parseDouble((String) obj.get("queryExecutionTime")) >= queryExecutionTime)
                    slowQuery.add(rid);
            }
            Path filePath = Paths.get(System.getProperty("user.home")+"/slowQuery.txt");
            for(String slow : slowQuery){
                String subject = "Slow Query In Database for Request Id: "+ slow;
                List<String> data =   traceMap.get(slowQuery);
                StringBuffer message = new StringBuffer();
                for(String d: data){
                    message.append(d).append(System.getProperty("line.separator"));
                }
                if(sendFileSystem)
                    Files.writeString(filePath, subject+ message.toString(), StandardOpenOption.CREATE);
                if(sendEmail)
                    sendMail(recipientEmail, message.toString(), subject);
                if(sendSms)
                    sendSMS(recipientNumber,  message.toString(), subject);
            }

        }catch(Exception e){

        }
    }

   public static void checkLogAlerts(){
       Properties properties = getProperties();
       boolean sendSms = (boolean) properties.get("sendSms");
       boolean sendEmail = (boolean) properties.get("sendEmail");
       boolean sendFileSystem = (boolean) properties.get("sendFileSystem");
       String recipientEmail  = (String) properties.get("recipientEmail");
       String recipientNumber = (String) properties.get("recipientPhoneNumber");
       List<String> traceIds = new LinkedList<>();
       Map<String, ArrayList<String>> traceMap = new HashMap<>();
       try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
            Statement st = con.createStatement();
            long time = System.currentTimeMillis() - 300000;
            String sql = "select * from logDB where ts >= "+time;
            PreparedStatement preparedStmt = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                long id = rs.getLong("ts");
                String str1 = rs.getString("logData");
                JSONObject obj = new JSONObject(str1);
                String rid = obj.get("rid").toString();
                if(traceMap.containsKey(rid)){
                    ArrayList<String> list = traceMap.get(rid);
                    list.add(str1);
                    traceMap.put(rid, list);
                }   else{
                    ArrayList<String> list = new ArrayList<>();
                    list.add(str1);
                    traceMap.put(rid, list);
                }
                //System.out.println(obj.toString());
                if (obj.get("layer").toString().equalsIgnoreCase("loadbalancer") &&
                        obj.get("event").toString().equalsIgnoreCase("error")){
                    System.out.println(obj.toString());
                    traceIds.add(rid);
                }
            }
            boolean serviceSlow=false;
            boolean dbSlow=false;
            for(String rID : traceIds){
               List<String> alertList =  traceMap.get(rID);
               String subject = "Load balancer timeout due to rid: "+ rID + " in layer: ";
               StringBuffer message = new StringBuffer();
               Path filePath = Paths.get(System.getProperty("user.home")+"/"+rID+".txt");
               for(String alert : alertList){
                   JSONObject obj = new JSONObject(alert);
                   message.append(obj.toString()).append(System.getProperty("line.separator"));
                   if(obj.get("layer").toString().equalsIgnoreCase("application") &&
                           obj.get("event").toString().equalsIgnoreCase("continue")){
                       serviceSlow = true;
                    }else{
                       dbSlow = true;
                   }
               }
                if(serviceSlow){
                    subject+=" Service SlowDown ";
                    if(sendFileSystem)
                        Files.writeString(filePath, "Service slowDown due to rid :"+rID+" "+ message.toString(), StandardOpenOption.CREATE);
                    if(sendEmail)
                       sendMail(recipientEmail, message.toString(), subject);
                    if(sendSms)
                       sendSMS(recipientNumber,  message.toString(), subject);
                    serviceSlow = false;

                }   else{
                    subject+=" Database SlowDown ";
                    if(sendFileSystem)
                        Files.writeString(filePath, "Database slowDown rid :"+rID+" "+ message.toString(), StandardOpenOption.CREATE);
                    if(sendEmail)
                        sendMail(recipientEmail, message.toString(), subject);
                    if(sendSms)
                        sendSMS(recipientNumber,  message.toString(), subject);
                    dbSlow = false;
                }
            }


            // fetch all records related to trace id
            con.close();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

   }
}
