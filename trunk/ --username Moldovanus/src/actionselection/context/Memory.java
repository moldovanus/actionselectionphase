/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.context;

import actionselection.command.Command;
import com.hp.hpl.jena.ontology.OntModel;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author Me
 */
public class Memory implements Serializable {

    private Map<SensorValues, Queue<Command>> map;

    public Memory() {
        map = new HashMap<SensorValues, Queue<Command>>(10);
    }

    public void memorize(SensorValues values, Queue<Command> actions) {
        map.put(values, actions);
    }

    public Queue<Command> getActions(SensorValues values) {
        return map.get(values);
    }

    public void forgetAll() {
        map.clear();
    }

    public void restoreOwlModel(OntModel model){
        for(Queue<Command> queue : map.values()){
            for(Command command : queue){
                command.setPolicyConversionModel(model);
            }
        }
    }


}
