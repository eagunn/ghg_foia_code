package gov.epa.ghg.util;

import java.util.Comparator;
import java.util.Map;

public class ParentComparator implements Comparator {

	Map<String, Float> parentCompanies;

	public ParentComparator(Map<String, Float> parentCompanies) {
		this.parentCompanies = parentCompanies;
	}

	public int compare(Object a, Object b) {
		String companyOne = (String) a;
		String companyTwo = (String) b;
		if (parentCompanies.get(companyOne) >= parentCompanies.get(companyTwo)) {
			return -1;
		} else {
			return 1;
		}

	}

}