package contextawaremodel.model;

import java.io.Serializable;

//class that contains all data necessary to represent an agent request
//for now it only contains the request String
public class AgentRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String request;
	
	public AgentRequest(String request) {
		this.request = request;
	}
	
	public String getRequest () {
		return this.request;
	}
}
