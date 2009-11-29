package contextawaremodel.agents.behaviours;


import contextawaremodel.agents.CIAgent;

import contextawaremodel.model.SemanticSpace;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import jade.core.behaviours.CyclicBehaviour;

public class BasicCIABehaviour extends CyclicBehaviour {

	//private 
	private JenaOWLModel owlModel;
	private CIAgent agent;
	private SemanticSpace semS;
	
	public BasicCIABehaviour(JenaOWLModel owlModel, CIAgent agent) {
		this.owlModel = owlModel;
		this.agent = agent;
		this.semS = new SemanticSpace();
	}
	
	@Override
	public void action() {
            System.out.println("CIA called");
        return; /*
		MessageTemplate m1 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		
		ACLMessage msgContextInstanceModified = agent.receive(m1);
		
		//waiting for message about context instance
		if (msgContextInstanceModified != null) {
			//message has been received that context instance was modified
			System.out.println("[CIA] recieved message to build semantic space");
			try {
				Object[] semanticElements = ((LinkedList<RealWorldElement>)msgContextInstanceModified.getContentObject()).toArray();
				
				for (int i = 0;i < semanticElements.length; i++) {			
					this.semS.updateSpace((RealWorldElement)semanticElements[i]);
				}
				
				System.out.println(this.semS.toString());
				
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}

		//waiting for message from RPA to get semanticSpace
		MessageTemplate m2 = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		ACLMessage msgRPA = agent.receive(m2);
		
		if (msgRPA != null) {
			//send message to rpa with the request
			ACLMessage msgtoRPA = new ACLMessage(ACLMessage.REQUEST);
			msgtoRPA.addReceiver( new AID(GlobalVars.RPAGENT_NAME + "@" + agent.getContainerController().getPlatformName())  );

			try {
				msgtoRPA.setContentObject(this.semS);
				msgtoRPA.setLanguage("JavaSerialization");
			} catch (IOException e) {
				e.printStackTrace();
			}
			agent.send(msgtoRPA);
		}*/
	}

}
