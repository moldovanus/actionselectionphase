/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents;

import actionselection.context.Memory;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import contextawaremodel.GlobalVars;
import contextawaremodel.agents.behaviours.ContextDisturbingBehaviour;
import contextawaremodel.agents.behaviours.ReceiveMessageRLBehaviour;
import contextawaremodel.agents.behaviours.ReinforcementLearningBasicBehaviour;

import contextawaremodel.agents.behaviours.StoreMemoryBehaviour;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import jade.core.Agent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.mindswap.pellet.jena.PelletReasonerFactory;
import policyconversioncore.PoliciesHandler;

/**
 *
 * @author Administrator
 */
public class ReinforcementLearningAgent extends Agent {

    private OWLModel contextAwareModel;
    private com.hp.hpl.jena.ontology.OntModel policyConversionModel;
    private Memory memory;

    @Override
    protected void setup() {
        System.out.println("[RL agent] Hello!");

        //the owl model is passed as an argument by the Administrator Agent
        Object[] args = getArguments();
        if (args != null) {



            this.contextAwareModel = (OWLModel) args[0];
            try {
                JenaOWLModel jenaOwlModel = null;
                File file = new File(GlobalVars.ONTOLOGY_FILE);
                jenaOwlModel = ProtegeOWL.createJenaOWLModelFromURI(file.toURI().toString());



                // politici
                PoliciesHandler policiesHandler = new PoliciesHandler();
                policiesHandler.loadPolicies(GlobalVars.POLICIES_FILE);
                //List<String> swrlCode = policiesHandler.getPoliciesConverter().convertAllPolicies();

                // adaugare reguli in ontologie
                /*SWRLFactory factory = new SWRLFactory(owlModel);
                for (String s : swrlCode) {
                System.out.println("Adding rule: " + s);
                factory.createImp(s);
                }*/

                policyConversionModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
                policyConversionModel.add(jenaOwlModel.getJenaModel());


                File memoryFile = new File(GlobalVars.MEMORY_FILE);
                try {
                    FileInputStream fileInputStream = new FileInputStream(memoryFile);
                    ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
                    memory = (Memory) inputStream.readObject();
                    //memory.restoreOwlModel(policyConversionModel);
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

                addBehaviour(new ReinforcementLearningBasicBehaviour(this, contextAwareModel, policyConversionModel, jenaOwlModel, memory));
                addBehaviour(new ContextDisturbingBehaviour(this, 10000, policyConversionModel));
                addBehaviour(new ReceiveMessageRLBehaviour(this, contextAwareModel, policyConversionModel));
                addBehaviour(new StoreMemoryBehaviour(this, 1000, memory));

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
