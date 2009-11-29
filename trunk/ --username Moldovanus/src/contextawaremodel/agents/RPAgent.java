package contextawaremodel.agents;

import contextawaremodel.agents.behaviours.BasicRPABehaviour;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.mobility.MobilityOntology;

public class RPAgent extends Agent {
private JenaOWLModel owlModel;
	
	protected void setup() {
		System.out.println("RP Agent " + getLocalName() + " started.");
		
		//the owl model is passed as an argument by the Administrator Agent
		Object[] args = getArguments();
		if (args != null) {
			this.owlModel = (JenaOWLModel)args[0];
			
		    //getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
			//getContentManager().registerOntology(MobilityOntology.getInstance());
					
			addBehaviour(new BasicRPABehaviour(this.owlModel, this));
			//addBehaviour(new BasicCIABehaviour(this.owlModel, this));
		} else {
			this.owlModel = null;
			System.out.println("RP Agent failed, owlModel argument is null!");
		}
		
		
	}
}
