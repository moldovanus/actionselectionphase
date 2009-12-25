/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import contextawaremodel.GlobalVars;
import contextawaremodel.agents.ReinforcementLearningAgent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Moldovanus
 */
public class ReceiveMessageRLBehaviour extends CyclicBehaviour {

    private ReinforcementLearningAgent agent;
    private OWLModel owlModel;
    private OntModel jenaModel;

    public ReceiveMessageRLBehaviour(Agent agent, OWLModel owlModel, OntModel jenaModel) {
        super(agent);
        this.agent = (ReinforcementLearningAgent) agent;
        this.owlModel = owlModel;
        this.jenaModel = jenaModel;
    }

    @Override
    public void action() {
        ACLMessage message = agent.receive();
        if (message == null) {
            return;
        }

        try {
            switch (message.getPerformative()) {
                case ACLMessage.INFORM_REF:
                    String individualName = (String) message.getContentObject();
                    final RDFResource individual = owlModel.getRDFResource(individualName);
                    if (!individual.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("sensor"))) {
                        return;
                    }

                    RDFProperty urlProperty = owlModel.getRDFProperty("has-web-service-URI");
                    String url = individual.getPropertyValue(urlProperty).toString();

                    //register the web service URL read from the external file into the jena ont model
                    Individual sensor = jenaModel.getIndividual(GlobalVars.base + "#" + individualName);
                    Property urlJenaProperty = jenaModel.getDatatypeProperty(GlobalVars.base + "#has-web-service-URI");
                    sensor.setPropertyValue(urlJenaProperty, jenaModel.createLiteralStatement(
                            sensor, urlJenaProperty, url).getLiteral().as(RDFNode.class));
                    break;

                case ACLMessage.INFORM:
                    String content = (String) message.getContent();
                    if (content.equals("BROKEN")) {
                        agent.setContextIsOK(false);
                    } else if (content.equals("OK")) {
                        agent.setContextIsOK(true);
                    }
                    break;

                case ACLMessage.SUBSCRIBE:
                    Boolean value = (Boolean) message.getContentObject();
                    agent.setContextDirty(value);
                    System.err.println("Context dirty received");
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
