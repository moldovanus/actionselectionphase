/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.context;

import actionselection.command.Command;
import com.hp.hpl.jena.ontology.OntModel;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import java.util.Queue;

/**
 *
 * @author Administrator
 */
public class ContextSnapshot {

    private com.hp.hpl.jena.ontology.OntModel policyConversionModel;
    private Queue<Command> actions;
    private JenaOWLModel jenaOwlModel;
    //private SensorValues currentValues ;
    private double contextEntropy = 0;

    public ContextSnapshot(final OntModel policyConversionModel, final Queue<Command> actions, final JenaOWLModel owlModel) {
        this.policyConversionModel = policyConversionModel;
        this.actions = actions;
        this.jenaOwlModel = owlModel;
    }

    public double getContextEntropy() {
        return contextEntropy;
    }

    public void setContextEntropy(double contextEntropy) {
        this.contextEntropy = contextEntropy;
    }


    public void addActions(Queue<Command> commands){
        for(Command command:commands){
            actions.add(command);
        }
    }
 
    public void executeActions() {
        for (Command command : actions) {
            command.execute();
        }
    }

     public void executeActionsOnOWL() {
        for (Command command : actions) {
            command.setOWLValue();
        }
    }

    public void rewind() {
        for (Command command : actions) {
            command.rewind();
        }
    }

    public JenaOWLModel getJenaOwlModel() {
        return jenaOwlModel;
    }

    public void setJenaOwlModel(JenaOWLModel jenaOwlModel) {
        this.jenaOwlModel = jenaOwlModel;
    }

    public Queue<Command> getActions() {
        return actions;
    }

    public void setActions(final Queue<Command> actions) {
        this.actions = actions;
    }

    public OntModel getPolicyConversionModel() {
        return policyConversionModel;
    }

    public void setPolicyConversionModel(OntModel policyConversionModel) {
        this.policyConversionModel = policyConversionModel;
    }

    //public SensorValues getCurrentValues() {
   //     return currentValues;
    //}

   // public void setCurrentValues(SensorValues currentValues) {
       // this.currentValues = currentValues;
   // }
   
}
