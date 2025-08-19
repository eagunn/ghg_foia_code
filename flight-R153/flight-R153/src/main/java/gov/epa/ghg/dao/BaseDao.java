package gov.epa.ghg.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDao<E extends Serializable, K extends Serializable> {
	
	public abstract void save(E instance);
	
	public abstract void delete(E instance);
	
	public abstract E merge(E instance);
	
	// public abstract List<E> find(int start, int max);
	// public abstract List<E> find(int start, int max, E instance);
	public abstract List<E> find(int start, int max, Map<String, Serializable> propertyNameValuePair);
	
	public abstract List<E> findAll();
	// public abstract E findById(K id);
	// public abstract List<E> findByExample(E instance);
	// public abstract List<E> findByProperty(String name, Serializable value);
	// public abstract Integer findCount();
	// public abstract Integer findCountByExample(E instance);
	// public abstract Integer findCountByPropertyMap(Map<String, Serializable> propertyNameValuePair);
	
}
