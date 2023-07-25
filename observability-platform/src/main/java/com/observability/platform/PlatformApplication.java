package com.observability.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class PlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlatformApplication.class, args);
	}

	@Scheduled(fixedRate = 60000) // 300000 milliseconds = 5 minutes
	public void callUnifiedAlertsGenerator() {
		UnifiedAlertGenerator.checkLogAlerts();
		UnifiedAlertGenerator.checkAppMetricAlerts();
		UnifiedAlertGenerator.checkDBMetricAlerts();

	}

}
