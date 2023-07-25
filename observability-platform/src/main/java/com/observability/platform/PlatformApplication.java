package com.observability.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class PlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlatformApplication.class, args);
	}

	@Scheduled(fixedRate = 300000) // 300000 milliseconds = 5 minutes
	public void callUnifiedAlertsGenerator() {
		// Call the UnifiedAlertsGenerator here
		// For example, if the UnifiedAlertsGenerator is a static method:
		UnifiedAlertGenerator.main(new String[] { "recipient@example.com", "+1234567890" });
	}

}
