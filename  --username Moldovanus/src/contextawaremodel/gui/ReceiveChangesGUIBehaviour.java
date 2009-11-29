package contextawaremodel.gui;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiveChangesGUIBehaviour extends CyclicBehaviour {

    private GUIAgent guiAgent;

    public ReceiveChangesGUIBehaviour(GUIAgent guiAgent) {
        this.guiAgent = guiAgent;
    }

    @Override
    public void action() {
        ACLMessage message = guiAgent.receive();
        if ( message == null ) return;

        try {
            switch (message.getPerformative()) {
                case ACLMessage.INFORM:
                    String individualName = (String) message.getContentObject();
                    guiAgent.addIndividual(individualName);
                    break;
            }
        } catch (Exception e) {
        }
    }

}
