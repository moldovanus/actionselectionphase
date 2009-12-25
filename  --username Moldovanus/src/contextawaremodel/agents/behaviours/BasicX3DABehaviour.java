/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package contextawaremodel.agents.behaviours;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import x3dtest.LoadTest;

/**
 *
 * @author Administrator
 */
public class BasicX3DABehaviour extends CyclicBehaviour {
    private LoadTest loadTest;
    private Agent agent ;
      public BasicX3DABehaviour(LoadTest lt, Agent agent) {
        this.agent = agent;
        this.loadTest = lt;
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
                    /*final RDFResource individual = owlModel.getRDFResource(individualName);
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
                    break;*/

                case ACLMessage.INFORM:
                    String content = (String) message.getContent();
                    
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

}
