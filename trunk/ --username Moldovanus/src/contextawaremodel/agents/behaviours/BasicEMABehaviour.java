package contextawaremodel.agents.behaviours;

import contextawaremodel.agents.EMAgent;
import contextawaremodel.model.ActionPlan;
import contextawaremodel.model.SemanticSpace;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class BasicEMABehaviour extends CyclicBehaviour {
	//private 
	private JenaOWLModel owlModel;
	private EMAgent agent;
	private ActionPlan plan;
	
	public BasicEMABehaviour(JenaOWLModel owlModel, EMAgent agent) {
		this.owlModel = owlModel;
		this.agent = agent;
	}
	
	@Override
	public void action() {
		MessageTemplate m1 = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
		
		ACLMessage msgCarryOutPlan = agent.receive(m1);
		
		if (msgCarryOutPlan != null) {
			try {
				plan = (ActionPlan)(msgCarryOutPlan.getContentObject());
				System.out.println("[EMA] Received plan " + plan.getStr());
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		}
	}

}
