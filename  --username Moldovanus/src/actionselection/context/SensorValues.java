/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.context;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class SensorValues {

    private Map<String, Double> myMap;

    public SensorValues() {
        myMap = new HashMap<String, Double>();
    }

    public SensorValues(OntModel model,JenaOWLModel owlModel) {
        Collection<RDFResource> resources = owlModel.getRDFResources();
        for (RDFResource resource : resources) {
            if (resource.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("sensor"))) {
             
                String name = resource.getProtegeType().getName();
           

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
        return true;
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
}
