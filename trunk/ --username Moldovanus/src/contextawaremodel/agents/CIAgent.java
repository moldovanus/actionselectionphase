package contextawaremodel.agents;

import contextawaremodel.agents.behaviours.ReceiveMessagesCIABehaviour;
import contextawaremodel.agents.behaviours.RecordingCIABehaviour;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.mobility.MobilityOntology;

public class CIAgent extends Agent {

    private OWLModel owlModel;

    public OWLModel getOwlModel() {
        return this.owlModel;
    }

    @Override
    protected void setup() {
        System.out.println("[CIA] Hello!");

        //the owl model is passed as an argument by the Administrator Agent
        Object[] args = getArguments();
        if (args != null) {
            this.owlModel = (OWLModel) args[0];

            getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
            getContentManager().registerOntology(MobilityOntology.getInstance());

            addBehaviour(new ReceiveMessagesCIABehaviour(this, owlModel));
            //addBehaviour(new RecordingCIABehaviour(this, 5000));
        } else {
            this.owlModel = null;
            System.out.println("[CIA] CIA Agent failed, owlModel argument is null!");
        }
    }
}
