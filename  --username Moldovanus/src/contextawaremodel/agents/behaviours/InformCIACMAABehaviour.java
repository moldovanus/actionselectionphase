package contextawaremodel.agents.behaviours;

import java.io.IOException;

import contextawaremodel.agents.CMAAgent;
import contextawaremodel.GlobalVars;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

//behaviour to inform the CIA about the changes in the semantic space
public class InformCIACMAABehaviour extends OneShotBehaviour {

    private CMAAgent agent;
    private String indvName;
    private int aCode;

    public InformCIACMAABehaviour(CMAAgent agent, String indvName, int aCode) {
        this.indvName = indvName;
        this.agent = agent;
        this.aCode = aCode;
    }

    @Override
    public void action() {

        ACLMessage msg;

        switch (aCode) {
            case GlobalVars.INDIVIDUAL_CREATED:
                msg = new ACLMessage(ACLMessage.INFORM);
                break;
            case GlobalVars.INDIVIDUAL_DELETED:
                msg = new ACLMessage(ACLMessage.INFORM_IF);
                break;
            case GlobalVars.INDIVIDUAL_MODIFIED:
                msg = new ACLMessage(ACLMessage.INFORM_REF);
                break;
            default:
                msg = null;
                break;
        }

        if (msg != null) {
            msg.addReceiver(new AID(GlobalVars.CIAGENT_NAME + "@" + agent.getContainerController().getPlatformName()));
           // msg.addReceiver(new AID(GlobalVars.GUIAGENT_NAME + "@" + agent.getContainerController().getPlatformName()));
            msg.addReceiver(new AID(GlobalVars.RLAGENT_NAME + "@" + agent.getContainerController().getPlatformName()));
            try {
                msg.setContentObject(indvName);
                msg.setLanguage("JavaSerialization");
            } catch (IOException e) {
                e.printStackTrace();
            }
            agent.send(msg);
        }

    }
}
