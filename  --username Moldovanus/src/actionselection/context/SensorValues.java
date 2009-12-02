/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package actionselection.context;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class SensorValues {
    private Map<String,Double> myMap;
    public SensorValues(){
        myMap = new HashMap<String,Double>();
    }
    public Double getValue(String sensorName){
        return myMap.get(sensorName);
    }
    public void setValue(String sensorName, Double value){
        myMap.put(sensorName, value);
    }

    public Map<String, Double> getMyMap() {
        return myMap;
    }

    public void setMyMap(Map<String, Double> myMap) {
        this.myMap = myMap;
    }

}
