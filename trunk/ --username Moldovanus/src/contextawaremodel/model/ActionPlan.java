package contextawaremodel.model;

import java.io.Serializable;

public class ActionPlan implements Serializable{

	private String str;
	
	public ActionPlan ( String str ) {
		this.str = str;
	}
	
	public String getStr() {
		return str;
	}
}
