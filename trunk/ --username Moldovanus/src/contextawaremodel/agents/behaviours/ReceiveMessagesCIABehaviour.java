package contextawaremodel.agents.behaviours;

import contextawaremodel.sensorapi.SensorAPI;
import contextawaremodel.sensorapi.SensorListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiveMessagesCIABehaviour extends CyclicBehaviour {

    private Agent agent;
    private OWLModel owlModel;

    public ReceiveMessagesCIABehaviour(Agent agent, OWLModel owlModel) {
        this.agent = agent;
        this.owlModel = owlModel;
    }

    @Override
    public void action() {
        ACLMessage message = agent.receive();
        if (message == null) {
            return;
        }

        try {
            switch (message.getPerformative()) {
                case ACLMessage.INFORM:
                    String individualName = (String) message.getContentObject();
                    final RDFResource individual = owlModel.getRDFResource(individualName);
                    if (!individual.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("sensor"))) {
                        return;
                    }

                    RDFProperty urlProperty = owlModel.getRDFProperty("has-web-service-URI");
                    final RDFProperty valueProperty = owlModel.getRDFProperty("has-value-of-service");
                    String url = individual.getPropertyValue(urlProperty).toString();
                    SensorAPI.addSensorListener(url,
                            new SensorListener() {

                                public void valueChanged(double newValue) {
                                    individual.setPropertyValue(valueProperty, String.format("%1$2.2f", newValue));
                                }
                            });

                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
