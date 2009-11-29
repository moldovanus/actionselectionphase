package contextawaremodel.worldInterface;

import java.util.HashMap;

public class WorldElement {

    private String indvName, className;
    private HashMap<String, String> attributes;
    //HashMaps<Attribute Name, Attribute Value>

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getIndvName() {
        return indvName;
    }

    public void setIndvName(String indvName) {
        this.indvName = indvName;
    }
    
    public WorldElement (String indvName, String className) {
        this.indvName = indvName;
        this.className = className;
        this.attributes = new HashMap<String, String>();
    }

    public boolean addAttribute (String aName,String aValue) {
        if (attributes.containsKey(aName)) {
            //attribute already defined
            return false;
        } else {
            //define new attribute
            attributes.put(aName, aValue);
            return true;
        }
    }

    public Object[] getAttributes ()  {
        return attributes.keySet().toArray();
    }

    public String getAttributeValue (String aName) {
        return attributes.get(aName);
    }

}
