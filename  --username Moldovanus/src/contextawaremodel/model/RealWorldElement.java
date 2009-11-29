package contextawaremodel.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class RealWorldElement implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String contextClassName;
	private String contextInstanceName;
	private String[] contextPropertyName;
	private String[] contextPropertyValue;
	private int cnt;
	private int nrProp;
	
	public RealWorldElement(String contextClassName, String contextInstanceName, int nrProp) {
		this.contextClassName = contextClassName;
		this.contextInstanceName = contextInstanceName;
		this.contextPropertyName = new String[nrProp];
		this.contextPropertyValue = new String[nrProp];
		this.cnt = 0;
		this.nrProp = nrProp;
	}
	
	public void setPropertyValue(String propertyName, String propertyValue) {
		this.contextPropertyName[cnt] = propertyName;
		this.contextPropertyValue[cnt] = propertyValue;
		cnt++;
	}
	
	public String getContextClassName () {
		return this.contextClassName;
	}
	
	public String getContextInstanceName () {
		return this.contextInstanceName;
	}
	
	public String[] getContextPropertyName () {
		return this.contextPropertyName;
	}
	
	public String[] getContextPropertyValue () {
		return this.contextPropertyValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			RealWorldElement e1 = (RealWorldElement)obj;
			return this.contextInstanceName.equals(e1.getContextClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int ret = 0;
		
		for (int i = 0;i < contextInstanceName.length(); i++) {
			ret += contextInstanceName.charAt(i);
		}
		
		return ret;
	}

}
