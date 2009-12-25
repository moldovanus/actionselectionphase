/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents;

import actionselection.context.Memory;
import com.hp.hpl.jena.ontology.OntModel;
import contextawaremodel.GlobalVars;
import contextawaremodel.agents.behaviours.ContextDisturbingBehaviour;
import contextawaremodel.agents.behaviours.GarbadgeCollectForcerAgent;
import contextawaremodel.agents.behaviours.RLPlotterBehaviour;
import contextawaremodel.agents.behaviours.ReceiveMessageRLBehaviour;
import contextawaremodel.agents.behaviours.ReinforcementLearningBasicBehaviour;

import contextawaremodel.agents.behaviours.StoreMemoryBehaviour;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import jade.core.Agent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ReinforcementLearningAgent extends Agent {

    private OWLModel contextAwareModel;
    private OntModel policyConversionModel;
    private JenaOWLModel jenaOwlModel;
    private Memory memory;
    private int rlTime;
    private int totalRunningTime;
    private int runCount = 0;
    private boolean contextIsOK = true;

    public boolean isContextIsOK() {
        return contextIsOK;
    }

    public void setContextIsOK(boolean contextIsOK) {
        this.contextIsOK = contextIsOK;
    }

    

    public int getTotalRunningTime() {
        return totalRunningTime;
    }

    public int getRlAverageTime() {
        if (runCount == 0) {
            return 0;
        } else {
            return totalRunningTime / runCount;
        }
    }

    public int getRlTime() {
        return rlTime;
    }

    public void setRlTime(int rlTime) {
        this.rlTime = rlTime;
        this.totalRunningTime += rlTime;
        runCount++;
    }

    @Override
    protected void setup() {
        System.out.println("[RL agent] Hello!");

        //the owl model is passed as an argument by the Administrator Agent
        Object[] args = getArguments();
        if (args != null) {
            this.contextAwareModel = (OWLModel) args[0];
            this.policyConversionModel = (OntModel) args[1];
            jenaOwlModel = (JenaOWLModel) args[2];
            try {

                File memoryFile = new File(GlobalVars.MEMORY_FILE);
                try {
                    FileInputStream fileInputStream = new FileInputStream(memoryFile);
                    ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
                    memory = (Memory) inputStream.readObject();
                    memory.restoreOwlModel(policyConversionModel);
                    memory = new Memory();
                } catch (FileNotFoundException ex) {
                    System.err.println(ex.getMessage());
                    memory = new Memory();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                    memory = new Memory();
                } catch (ClassNotFoundException ex) {
                    System.err.println(ex.getMessage());
                    memory = new Memory();
                }

                
                Map<String, Map<String, String>> valueMapping = GlobalVars.getValueMapping();

                Map<String, String> mapping = new HashMap<String, String>();
                mapping.put("0.00", "OFF");
                mapping.put("1.00", "ON");

                valueMapping.put("AlarmStateSensorI", mapping);
                valueMapping.put("ComputerStateSensorI", mapping);
                valueMapping.put("LightSensorI", mapping);


                Map<String, String> roomEmpty = new HashMap<String, String>();
                roomEmpty.put("0.00", "EMPTY");
                roomEmpty.put("1.00", "NOT EMPTY");
                valueMapping.put("RoomStateSensorI", roomEmpty);

                //Map<String, String> movementEmpty = new HashMap<String, String>();
                //movementEmpty.put("0.00", "NO MOVEMENT");
                //movementEmpty.put("1.00", "MOVEMENT DETECTED");
                //valueMapping.put("RoomEmptySensorI", movementEmpty);


                Map<String, String> faceRecognition = new HashMap<String, String>();
                faceRecognition.put("0.00", "PROFESSOR");
                faceRecognition.put("1.00", "STUDENT");
                faceRecognition.put("2.00", "UNKNOWN");
                valueMapping.put("FaceRecognitionSensorI", faceRecognition);


                addBehaviour(new ReinforcementLearningBasicBehaviour(this, 1000, contextAwareModel, policyConversionModel, jenaOwlModel, memory));
                //addBehaviour(new ContextDisturbingBehaviour(this,5000, policyConversionModel));
                addBehaviour(new ReceiveMessageRLBehaviour(this, contextAwareModel, policyConversionModel));
                addBehaviour(new StoreMemoryBehaviour(this, 5000, memory));
                addBehaviour(new RLPlotterBehaviour(this, 1000));
                //addBehaviour(new GarbadgeCollectForcerAgent(this,60000));

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            this.contextAwareModel = null;
            this.policyConversionModel = null;
            System.out.println("[RL] RL Agent failed, owlModel arguments are null!");
        }

    }
}
