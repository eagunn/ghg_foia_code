package gov.epa.ghg.rest;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.epa.ghg.service.HealthCheckService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class HealthCheckController {
	
	@Inject
	HealthCheckService healthCheckService;
	
	@GetMapping(value = "/health-check")
	public String checkHealth() throws Exception {
		try {
			if (healthCheckService.isHealthy()) {
				return "UP";
			}
		} catch (Exception e) {
			log.info("Health check failed [{}]", e.getMessage());
		}
		return "DOWN";
	}
}
