package contextawaremodel.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

import contextawaremodel.GlobalVars;

public class SemanticSpace implements Serializable {
	//contains the real world elements that make up the semantic space
	private HashSet<RealWorldElement> rweSet;
	
	public SemanticSpace () {
		this.rweSet = new HashSet<RealWorldElement>();
	}
	
	public void updateSpace(RealWorldElement e) {
		if (this.rweSet.contains(e)) {
			this.rweSet.remove(e);
		} 
		this.rweSet.add(e);
	}
	
	@Override
	public String toString() {
		String ret = "[";
		
		Iterator<RealWorldElement> it = rweSet.iterator();
		
		while (it.hasNext()) {
			RealWorldElement e = it.next();
			String[] propName = e.getContextPropertyName();
			String[] propValue = e.getContextPropertyValue();

			for (int j = 0;j < propName.length; j++) {
				if (propName[j].equals(GlobalVars.PHYSICAL_RESOURCE_NAME_NAME)) {
					ret += propValue[j] + "(" + e.getContextInstanceName() + "), ";
				}
			}
		}
		ret = ret.substring(0, ret.length() - 2) + "] \n= (";
		
		Iterator<RealWorldElement> it2 = rweSet.iterator();
		
		while (it2.hasNext()) {
			RealWorldElement e = it2.next();
			String[] propName = e.getContextPropertyName();
			String[] propValue = e.getContextPropertyValue();

			for (int j = 0;j < propName.length; j++) {
				if (propName[j].equals(GlobalVars.PHYSICAL_RESOURCE_VALUE_NAME)) {
					ret += propValue[j] + ", ";
				}
			}
		}
		
		ret = ret.substring(0, ret.length() - 2) + ")";
		
		return ret;
	}
}
