/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

import actionselection.x3dCommand.X3DCommand;
import actionselection.x3dCommand.X3DStringCommand;
import contextawaremodel.agents.X3DAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Administrator
 */
public class BasicX3DABehaviour extends CyclicBehaviour {

    private X3DAgent agent;

    public BasicX3DABehaviour(X3DAgent agent) {
        this.agent = agent;

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
                    X3DCommand command = (X3DCommand) message.getContentObject();
                    command.execute(agent.getMainScene());
                    if (command instanceof X3DStringCommand) {
                        X3DStringCommand x3dCommand = (X3DStringCommand) command;
                        if (x3dCommand.getNodeName().equals("computerState_STRING")) {
                            agent.flipComputerState(x3dCommand.getContent());
                        }
                    }
                    break;

                    case ACLMessage.INFORM_IF:
                        break;
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
