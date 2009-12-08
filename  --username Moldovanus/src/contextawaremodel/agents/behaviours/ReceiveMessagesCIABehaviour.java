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
                case ACLMessage.INFORM_REF:
                    String individualName_2 = (String) message.getContentObject();
                    final RDFResource individual_2 = owlModel.getRDFResource(individualName_2);
                    if (!individual_2.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("sensor"))) {
                        return;
                    }

                    RDFProperty urlProperty_2 = owlModel.getRDFProperty("has-web-service-URI");
                    final RDFProperty valueProperty_2 = owlModel.getRDFProperty("has-value-of-service");
                    String url_2 = individual_2.getPropertyValue(urlProperty_2).toString();

                    System.out.println("Modified " + individualName_2);

                    SensorAPI.addSensorListener(url_2,
                            new SensorListener() {

                                public void valueChanged(double newValue) {
                                    individual_2.setPropertyValue(valueProperty_2, String.format("%1$2.2f", newValue));
                                }
                            });

                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
