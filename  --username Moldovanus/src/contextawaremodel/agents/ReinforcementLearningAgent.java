/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import contextawaremodel.GlobalVars;
import contextawaremodel.agents.behaviours.ReinforcementLearningBasicBehaviour;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import jade.core.Agent;
import java.io.File;
import java.util.List;

import org.mindswap.pellet.jena.PelletReasonerFactory;
import policyconversioncore.PoliciesHandler;

/**
 *
 * @author Administrator
 */
public class ReinforcementLearningAgent extends Agent {

    private OWLModel contextAwareModel;
    private com.hp.hpl.jena.ontology.OntModel policyConversionModel;
    

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
                List<String> swrlCode = policiesHandler.getPoliciesConverter().convertAllPolicies();

                // adaugare reguli in ontologie
                /*SWRLFactory factory = new SWRLFactory(owlModel);
                 for (String s : swrlCode) {
                      System.out.println("Adding rule: " + s);
                    factory.createImp(s);
                }*/

                policyConversionModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
                policyConversionModel.add(jenaOwlModel.getJenaModel());
                addBehaviour(new ReinforcementLearningBasicBehaviour(this, contextAwareModel, policyConversionModel, jenaOwlModel));

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
