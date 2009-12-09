package contextawaremodel.gui;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.List;

public class ReceiveChangesGUIBehaviour extends CyclicBehaviour {

    private GUIAgent guiAgent;
    private List<String> receivedMessages;

    public ReceiveChangesGUIBehaviour(GUIAgent guiAgent) {
        this.guiAgent = guiAgent;
        receivedMessages = new ArrayList<String>(10);
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

                case ACLMessage.INFORM_REF:
                    String individualName2 = (String) message.getContentObject();
                    if (! receivedMessages.contains(individualName2)){
                        guiAgent.addIndividual(individualName2);
                        receivedMessages.add(individualName2);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
