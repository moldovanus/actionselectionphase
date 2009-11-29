package contextawaremodel.agents.behaviours;

import contextawaremodel.agents.CMAAgent;
import contextawaremodel.GlobalVars;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import contextawaremodel.worldInterface.WorldElement;
import contextawaremodel.worldInterface.WorldFileParser;
import jade.core.behaviours.TickerBehaviour;
import java.util.HashMap;

/**
 * Behaviour of the context model administrating agent (CMAA)
 * Behaviour includes synchronizing the real world and the model
 *
 */
public class BasicCMAABehaviour extends TickerBehaviour {

    private JenaOWLModel owlModel;
    private WorldFileParser wfp;
    private CMAAgent agent;
    private HashMap<String, Boolean> existsIndv;

    public BasicCMAABehaviour(CMAAgent agent, JenaOWLModel owlModel) {
        super(agent, 1000);
        this.agent = agent;
        this.owlModel = owlModel;
        this.wfp = new WorldFileParser();
        this.existsIndv = new HashMap<String, Boolean>();
    }

    @Override
    protected void onTick() {

        if (this.wfp.wasChanged()) {
            WorldElement[] wElements = wfp.getWorld();
            existsIndv.clear();
            //first iterate to add new or update old OWL Individuals
            for (int i = 0; i < wElements.length; i++) {
                //mark that the individual does exist
                existsIndv.put(wElements[i].getIndvName(), true);

                //get the OWL Class instance
                OWLNamedClass ccls = owlModel.getOWLNamedClass(wElements[i].getClassName());

                //if the class instance is valid
                if (ccls != null) {
                    //get the OWL Individual Resource instance
                    RDFResource rdfr = owlModel.getRDFResource(wElements[i].getIndvName());

                    if (rdfr != null) {

                        //if the instance already exists => update
                        Object[] propName = wElements[i].getAttributes();
                        //iterate through all data properties
                        for (int j = 0; j < propName.length; j++) {
                            RDFProperty prop = owlModel.getRDFProperty(propName[j].toString());
                            if (prop != null) {
                                //if property is not set => we set it
                                if (rdfr.getPropertyValue(prop) == null) {
                                    rdfr.setPropertyValue(prop, "");
                                }

                                String oldP = rdfr.getPropertyValue(prop).toString().trim();
                                String newP = wElements[i].getAttributeValue(propName[j].toString().trim()).trim();

                                //if property has new value => we set the new value
                                if (!oldP.equals(newP)) {
                                    rdfr.setPropertyValue(prop, newP);
                                    agent.informCia(wElements[i].getIndvName(), GlobalVars.INDIVIDUAL_MODIFIED);
                                    System.out.print("[CMAA] " + propName[j] + " was changed for " + wElements[i].getIndvName());
                                    System.out.println("from " + oldP + " to " + newP);
                                }
                            } else {
                                //property does not exist => error message
                                System.out.println("[CMAA] " + propName[j] + " invalid Ontology property name!");
                            }
                        }

                    } else {

                        //if the instance does not exist exists => create new
                        //the individual instance does no exist => add it to the ontology
                        OWLIndividual owlInd = ccls.createOWLIndividual(wElements[i].getIndvName());
                        agent.informCia(wElements[i].getIndvName(), GlobalVars.INDIVIDUAL_CREATED);
                        System.out.println("[CMAA] Individual " + wElements[i].getIndvName() + "(" + wElements[i].getClassName() + ") was added to the context");
                        Object[] propName = wElements[i].getAttributes();

                        for (int j = 0; j < propName.length; j++) {
                            RDFProperty prop = owlModel.getRDFProperty(propName[j].toString());
                            if (prop != null) {
                                //if property exists
                                owlInd.setPropertyValue(prop, wElements[i].getAttributeValue(propName[j].toString()));
                            } else {
                                System.out.println("[CMAA] " + propName[j] + " invalid Ontology property name!");
                            }
                        }

                    }
                }

            }

            //get all OWL Individuals from ontology
            Object[] owlElements = owlModel.getOWLIndividuals().toArray();

            //iterate to check if one was removed
            for (int i = 0; i < owlElements.length; i++) {
                OWLIndividual owli = (OWLIndividual) owlElements[i];
                if (!existsIndv.containsKey(owli.getLocalName())) {
                    agent.informCia(owli.getLocalName(), GlobalVars.INDIVIDUAL_DELETED);
                    System.out.println("[CMAA] Individual " + owli.getLocalName() + " was removed!");
                    //remove instance from owlmodel
                    owli.delete();
                }
            }
        }



    }
}
