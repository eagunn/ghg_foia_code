package gov.epa.ghg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "PUB_JCT_STATE_MSA")
public class JctStateMsa {
	
	private static final long serialVersionUID = 1L;
	
	private int state_msa_id;
	private String cbsafp;
	private String state;
	
	public JctStateMsa() {
	
	}
	
	public JctStateMsa(int state_msa_id, String cbsafp, String state) {
		this.state_msa_id = state_msa_id;
		this.cbsafp = cbsafp;
		this.state = state;
	}
	
	@GenericGenerator(name = "generator", strategy = "increment")
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "STATE_MSA_ID", unique = true, nullable = false)
	public int getState_msa_id() {
		return state_msa_id;
	}
	
	public void setState_msa_id(int state_msa_id) {
		this.state_msa_id = state_msa_id;
	}
	
	@Column(name = "CBSAFP", length = 5)
	public String getCbsafp() {
		return cbsafp;
	}
	
	public void setCbsafp(String cbsafp) {
		this.cbsafp = cbsafp;
	}
	
	@Column(name = "STATE", length = 2)
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
}
