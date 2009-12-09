package contextawaremodel.agents;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import contextawaremodel.GlobalVars;
import java.io.File;


import contextawaremodel.agents.behaviours.BasicCMAABehaviour;
import contextawaremodel.agents.behaviours.InformCIACMAABehaviour;

import contextawaremodel.gui.GUIAgent;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.core.behaviours.Behaviour;
import jade.wrapper.AgentController;
import contextawaremodel.ontology.*;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import policyconversioncore.PoliciesHandler;
//import contextawaremodel.model.SimulatedContext;
//import contextawaremodel.gui.SimulatorMainWindow;

public class CMAAgent extends Agent implements CMAAExternal {

    //the owl model
    private JenaOWLModel owlModel;

    private OntModel policyConversionModel;

    private JenaOWLModel jenaOwlModel;

    //the protege factory
    public MyFactory factory;
    //the main window of the simulator
    //private SimulatorMainWindow smw;
    //the real world implemented model
    //the object used for executing in the Swing thread
    private Runnable addIt;
    //Controller for the CIAgent which is created by the CMMA agent
    private AgentController cia = null;
    //Controller for the RPAgent which is created by the CMMA agent
    private AgentController rpa = null;
    //Controller for the EMAgent which is created by the CMMA agent
    private AgentController ema = null;
    //Controller for the EMAgent which is created by the CMMA agent
    private AgentController gui = null;
    //rl agent
    private AgentController rl = null;

    @Override
    protected void setup() {
        System.out.println("CMA Agent " + getLocalName() + " started.");

        try {
            //create owlModel from Ontology
            this.owlModel = ProtegeOWL.createJenaOWLModelFromURI(new File(GlobalVars.ONTOLOGY_FILE).toURI().toString());
            this.factory = new MyFactory(owlModel);

             
            File file = new File(GlobalVars.ONTOLOGY_FILE);
            jenaOwlModel = ProtegeOWL.createJenaOWLModelFromURI(file.toURI().toString());



            // politici
            PoliciesHandler policiesHandler = new PoliciesHandler();
            policiesHandler.loadPolicies(GlobalVars.POLICIES_FILE);
            //List<String> swrlCode = policiesHandler.getPoliciesConverter().convertAllPolicies();

            // adaugare reguli in ontologie
                /*SWRLFactory factory = new SWRLFactory(owlModel);
            for (String s : swrlCode) {
            System.out.println("Adding rule: " + s);
            factory.createImp(s);
            }*/

            policyConversionModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
            policyConversionModel.add(jenaOwlModel.getJenaModel());



            //runnable object for refreshing the GUI
            //this.addIt = new Runnable() {
            //	public void run() {
            //		smw.updateImage();
            //	}
            //};

            //synchronization factor


            //initialize the simulator GUI
            //this.smw = new SimulatorMainWindow( new SimulatedContext(this.owlModel, this) );
            //this.smw.setLocationRelativeTo(null);
            //this.smw.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            //this.smw.setVisible(true);
            //refreshGui ();


            //star the Context Interpreting Agent
            AgentContainer container = (AgentContainer) getContainerController(); // get a container controller for creating new agents
            //createNewAgent(Name, Class name, arguments to the agent)
            gui = container.createNewAgent(GlobalVars.GUIAGENT_NAME, GUIAgent.class.getName(), new Object[]{this.owlModel});
            gui.start();

            cia = container.createNewAgent(GlobalVars.CIAGENT_NAME, CIAgent.class.getName(), new Object[]{this.owlModel});
            cia.start();

            rl = container.createNewAgent(GlobalVars.RLAGENT_NAME, ReinforcementLearningAgent.class.getName(), new Object[]{this.owlModel,this.policyConversionModel , this.jenaOwlModel});
            rl.start();

            //star the Request Processing Agent

            //createNewAgent(Name, Class name, arguments to the agent)
            //rpa = container.createNewAgent(GlobalVars.RPAGENT_NAME, RPAgent.class.getName(), new Object[] {this.owlModel});
            //rpa.start();


            //start the execution and monitoring agent
            //ema = container.createNewAgent(GlobalVars.EMAGENT_NAME, EMAgent.class.getName(), new Object[] {this.owlModel});
            //ema.start();

            //TODO: add other agents creating code

            addBehaviour(new BasicCMAABehaviour(this, this.owlModel));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void addNewBehaviour(Behaviour newB) {
        this.addBehaviour(newB);
    }

    //send a message to context interpreting agent (context instance has changed)
    public void informCia(String indvName, int aCode) {
        addBehaviour(new InformCIACMAABehaviour(this, indvName, aCode));
    }
    //send message to request processing agent (new request is available)
    //public void informRPA(actor a, String request) {
    //	addBehaviour( new InformRPACMAABehaviour(this, a, request) );
    //}
}
