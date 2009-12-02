/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.context;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class SensorValues implements Serializable{

    private Map<String, Double> myMap;

    public SensorValues() {
        myMap = new HashMap<String, Double>();
    }

    public SensorValues(OntModel model,JenaOWLModel owlModel, String base) {
        myMap = new HashMap<String,Double> ();
        Collection<RDFResource> resources = owlModel.getRDFResources();
        for (RDFResource resource : resources) {
            if (resource.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("sensor"))) {
                String name = resource.getProtegeType().getName();
                String name1[] = name.split("-");
                String nameF = "";
                for (int i = 0; i < name1.length; i++) {
                    nameF += name1[i].substring(0, 1).toUpperCase() + name1[i].substring(1, name1[i].length());
                }
                Individual res = model.getIndividual(base + "#" + nameF + "I").asIndividual();
                Property resValue = model.getDatatypeProperty(base + "#has-value-of-service");
                String init = res.getPropertyValue(resValue).toString();
                try {
                    myMap.put(name, new Double((NumberFormat.getNumberInstance()).parse(init.split("\\^")[0]).doubleValue()));
                } catch (ParseException ex) {
                    Logger.getLogger(SensorValues.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SensorValues other = (SensorValues) obj;
        return this.equals(other);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.myMap != null ? this.myMap.hashCode() : 0);
        return hash;
    }
    public

     Double getValue(String sensorName) {
        return myMap.get(sensorName);
    }

    public void setValue(String sensorName, Double value) {
        myMap.put(sensorName, value);
    }

    public Map<String, Double> getMyMap() {
        return myMap;
    }

    public void setMyMap(Map<String, Double> myMap) {
        this.myMap = myMap;
    }

    public boolean equals(SensorValues values) {
        return myMap.equals(values.getMyMap());
    }

    @Override
    public String toString() {
        String stringValue = "";
        for (String key : myMap.keySet()){
            stringValue += "[ " + key + ": " + myMap.get(key) + "]  ";
        }
        return stringValue;
    }


}
