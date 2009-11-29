package contextawaremodel.agents.behaviours;

import java.io.IOException;
import java.util.LinkedList;

import contextawaremodel.GlobalVars;
import contextawaremodel.agents.RPAgent;

import contextawaremodel.model.ActionPlan;
import contextawaremodel.model.AgentRequest;
import contextawaremodel.model.RealWorldElement;
import contextawaremodel.model.SemanticSpace;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BasicRPABehaviour extends CyclicBehaviour {

    //private
    private JenaOWLModel owlModel;
    private RPAgent agent;
    private AgentRequest request;

    public BasicRPABehaviour(JenaOWLModel owlModel, RPAgent agent) {
        this.owlModel = owlModel;
        this.agent = agent;
    }

    @Override
    public void action() {
        MessageTemplate m1 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

        ACLMessage msgContextInstanceModified = agent.receive(m1);

        if (msgContextInstanceModified != null) {
            //message has been received that context instance was modified
            System.out.println("[RPA] recieved message to process new request");
            try {
                request = (AgentRequest) (msgContextInstanceModified.getContentObject());

                System.out.println(request.getRequest());

                //send message to CIA
                ACLMessage msg2 = new ACLMessage(ACLMessage.REQUEST);
                msg2.addReceiver(new AID(GlobalVars.CIAGENT_NAME + "@" + agent.getContainerController().getPlatformName()));
                msg2.setContent("Start moving");
                agent.send(msg2);

                //wait for reply from CIA with Semantic space

                MessageTemplate m2 = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msgCIA = agent.blockingReceive(m2);

                SemanticSpace sm1 = (SemanticSpace) (msgCIA.getContentObject());
                System.out.println("[RPA] recieved Semantic space from CIA " + sm1.toString());

                //TODO: get or build the plan , send message to EMA
                ActionPlan pl1 = new ActionPlan("test");
                ACLMessage msgtoEMA = new ACLMessage(ACLMessage.REQUEST);
                msgtoEMA.addReceiver(new AID(GlobalVars.EMAGENT_NAME + "@" + agent.getContainerController().getPlatformName()));

                //sending message with plan to execute to Execution and Monitoring agent
                try {
                    msgtoEMA.setContentObject(pl1);
                    msgtoEMA.setLanguage("JavaSerialization");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                agent.send(msgtoEMA);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
