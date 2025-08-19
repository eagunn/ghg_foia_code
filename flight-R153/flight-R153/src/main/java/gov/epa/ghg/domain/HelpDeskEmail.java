package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * HelpDeskEmail entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "PUB_HELP_DESK_EMAIL")
public class HelpDeskEmail implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// Fields
	
	private Long emailId;
	private String emailFrom;
	private String ticketNumber;
	private String subject;
	private String body;
	private String dateReported;
	
	// Constructors
	
	/**
	 * default constructor
	 */
	public HelpDeskEmail() {
	}
	
	/**
	 * full constructor
	 */
	public HelpDeskEmail(String emailFrom, String ticketNumber,
			String subject, String body, String dateReported) {
		this.emailFrom = emailFrom;
		this.ticketNumber = ticketNumber;
		this.subject = subject;
		this.body = body;
		this.dateReported = dateReported;
	}
	
	// Property accessors
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "EMAIL_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getEmailId() {
		return this.emailId;
	}
	
	public void setEmailId(Long emailId) {
		this.emailId = emailId;
	}
	
	@Column(name = "EMAIL_FROM", length = 1024)
	public String getEmailFrom() {
		return this.emailFrom;
	}
	
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	
	@Column(name = "TICKET_NUMBER", length = 20)
	public String getTicketNumber() {
		return this.ticketNumber;
	}
	
	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}
	
	@Column(name = "SUBJECT", length = 1024)
	public String getSubject() {
		return this.subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Column(name = "BODY")
	public String getBody() {
		return this.body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	@Column(name = "DATE_REPORTED")
	public String getDateReported() {
		return this.dateReported;
	}
	
	public void setDateReported(String dateReported) {
		this.dateReported = dateReported;
	}
	
}
