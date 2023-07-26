 MultiTraceOps
Enhanced Multi Layer Observability For Fault Localization


MiTraceOps is a powerful observability library and platform designed to enable precise fault localization in cloud-native microservices-based systems. It achieves this by collecting and correlating logs and metrics across multiple layers of the system, including the application layer and the database layer. This documentation provides comprehensive guidance on how to effectively use MiTraceOps to monitor microservices, detect incidents, and pinpoint faults with high accuracy.

**Steps to include observability library in your platform**

 clone the main branch of this repo.
 cd observability-library
 mvn clean install -U

 copy the observability-library-1.0.0-SNAPSHOT.jar to your project 

 Make it a class library and add it to your classpath.

 Annotate your controllers with @Log, @MetricsLog.

 Include the AppConfig , JPAConfig and RIDHandler inside a config folder in your service. For example - refer to the exemplar service and how it is done.

 Run your service.

 Create a sample workload for each endpoint you want to invoke. A sample has been provided in exemplar service.

Ensure Kafka and Mysql are running. Run the observability-platform next. Change the properties in observability platform to provide your recipient phone number , recipient email, sender email and password, sender phone number and twilio account credentials. 

You can see alerts sent to your email, sms and persisted on the local filesystem.

 

 
