package gov.epa.ghg.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import gov.epa.ghg.enums.ReportingStatus;

@Converter
public class ReportingStatusConverter implements AttributeConverter<ReportingStatus, String> {
	
	@Override
	public String convertToDatabaseColumn(ReportingStatus attribute) {
		return attribute == null ? null : attribute.name();
	}
	
	@Override
	public ReportingStatus convertToEntityAttribute(String dbData) {
		return dbData == null ? null : ReportingStatus.valueOf(dbData);
	}
}
