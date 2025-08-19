package gov.epa.ghg.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import gov.epa.ghg.dao.DimGhgDao;

@Service
public class HealthCheckService {
	
	@Inject
	private DimGhgDao dimGhgDao;
	
	public boolean isHealthy() {
		return dimGhgDao.getDbHealth() == 1;
	}
}
