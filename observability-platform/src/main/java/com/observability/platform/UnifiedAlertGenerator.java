package com.observability.platform;

import com.twilio.Twilio;
import org.json.JSONObject;

import java.io.IOException;
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
   public static void main(String[] args) {
       // Add recipient email and phone number as input parameters
       if (args.length != 2) {
           System.out.println("Usage: UnifiedAlertGenerator <recipient_email> <recipient_phone_number>");
           return;
       }
       String recipientEmail = args[0];
       String recipientNumber = args[1];
        List<String> traceIds = new LinkedList<>();
        Map<String, ArrayList<String>> traceMap = new HashMap<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
            Statement st = con.createStatement();
            String sql = "select * from logDB";
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
                    Files.writeString(filePath, "Service slowDown due to rid :"+rID+" "+ message.toString(), StandardOpenOption.CREATE);
                    sendMail(recipientEmail, message.toString(), subject);
                    sendSMS(recipientNumber,  message.toString(), subject);
                    serviceSlow = false;

                }   else{
                    subject+=" Database SlowDown ";
                    Files.writeString(filePath, "Database slowDown rid :"+rID+" "+ message.toString(), StandardOpenOption.CREATE);
                    sendMail(recipientEmail, message.toString(), subject);
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
