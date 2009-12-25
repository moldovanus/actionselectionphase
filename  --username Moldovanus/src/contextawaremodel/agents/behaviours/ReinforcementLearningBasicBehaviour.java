/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

import actionselection.command.Command;
import actionselection.command.DecrementCommand;
import actionselection.command.IncrementCommand;
import actionselection.command.SetCommand;
import actionselection.context.ContextSnapshot;
import actionselection.context.Memory;
import actionselection.context.SensorValues;
import actionselection.gui.ActionsOutputFrame;
import actionselection.utils.Pair;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import contextawaremodel.GlobalVars;
import contextawaremodel.agents.ReinforcementLearningAgent;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class ReinforcementLearningBasicBehaviour extends TickerBehaviour {

    private OWLModel contextAwareModel;
    private com.hp.hpl.jena.ontology.OntModel policyConversionModel;
    private JenaOWLModel owlModel;
    private Memory memory;
    private Property evaluatePolicyProperty;
    private Property hasWebServiceProperty;
    private Property hasAcceptedValueProperty;
    private NumberFormat integerNumberFormat = NumberFormat.getIntegerInstance();
    private double smallestEntropy = 10000;
    private ObjectProperty associatedResource;
    private ObjectProperty associatedActuatorProperty;
    private ObjectProperty associatedActionProperty;
    private Property actionEffect;
    private Property sensorValueProperty;
    private ArrayList<Resource> associatedResources;
    private ArrayList<Resource> actuatorsList;
    private ArrayList<Resource> actionsList;
    private ActionsOutputFrame resultsFrame;
    private ReinforcementLearningAgent agent;

    public ReinforcementLearningBasicBehaviour(Agent a, int interval, OWLModel contextAwareModel, OntModel policyConversionModel, JenaOWLModel owlModel, Memory memory) {
        super(a, interval);
        agent = (ReinforcementLearningAgent) a;
        this.contextAwareModel = contextAwareModel;
        this.policyConversionModel = policyConversionModel;
        this.owlModel = owlModel;
        this.memory = memory;
        evaluatePolicyProperty = policyConversionModel.getDatatypeProperty(GlobalVars.base + "#EvaluatePolicyP");
        hasWebServiceProperty = policyConversionModel.getDatatypeProperty(GlobalVars.base + "#has-web-service-URI");
        hasAcceptedValueProperty = policyConversionModel.getDatatypeProperty(GlobalVars.base + "#AcceptableSensorValue");
        associatedResource = policyConversionModel.getObjectProperty(GlobalVars.base + "#associated-resource");
        associatedActuatorProperty = policyConversionModel.getObjectProperty(GlobalVars.base + "#has-actuator");
        associatedActionProperty = policyConversionModel.getObjectProperty(GlobalVars.base + "#associated-action");
        actionEffect = policyConversionModel.getProperty(GlobalVars.base + "#effect");
        sensorValueProperty = policyConversionModel.getDatatypeProperty(GlobalVars.base + "#has-value-of-service");
        associatedResources = new ArrayList<Resource>();
        actuatorsList = new ArrayList<Resource>();
        actionsList = new ArrayList<Resource>();
        resultsFrame = new ActionsOutputFrame();


        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                resultsFrame.setVisible(true);
            }
        });
    }

    private Pair<Double, Individual> computeEntropy(ContextSnapshot contextSnapshot) {
        Individual brokenPolicy = null;
        double entropy = 0.0;
        Collection<RDFResource> resources = contextSnapshot.getJenaOwlModel().getRDFResources();

        for (RDFResource resource : resources) {

            if (resource.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("policy"))) {
                Individual policy = policyConversionModel.getIndividual(GlobalVars.base + "#" + resource.getProtegeType().getName() + "I").asIndividual();
                if (!getEvaluateProp(policy)) {
                    if (brokenPolicy == null) {
                        brokenPolicy = policy;
                    }
                    //System.err.println("Broken " + policy);
                    entropy++;
                }
            }
        }
        //SensorValues currentValues = new SensorValues(policyConversionModel, owlModel, GlobalVars.base);
        // System.err.println("Entropy : " + entropy + " Broken " + brokenPolicy);
        //System.err.println("for " + currentValues);
        return new Pair<Double, Individual>(entropy, brokenPolicy);
    }

    public Boolean hasCycles(HashMap<SensorValues, SensorValues> contexts, SensorValues myContext) {
        if (contexts.get(myContext) != null) {
            return true;
        } else {
            return false;
        }
    }

    public ContextSnapshot reinforcementLearning(Queue<ContextSnapshot> queue, HashMap<SensorValues, SensorValues> contexts) throws Exception {

        ContextSnapshot context = queue.remove();

        //if values have been changed stop and repair what you can so far 
        //if (agent.isContextDirty()) {
         ///   return context;
       // }

        SensorValues values = new SensorValues(policyConversionModel, owlModel, GlobalVars.base);
        Queue<Command> actions = memory.getActions(values);

        //exists
        if (actions != null) {
            context.addActions(actions);
            System.out.println("Remembered");
            return context;
        }

        context.executeActions();
        //System.err.println("  Current state : " + new SensorValues(context.getPolicyConversionModel(), context.getJenaOwlModel(), GlobalVars.base));
        Pair<Double, Individual> contextEvaluationResult = computeEntropy(context);

        if (contextEvaluationResult.getFirst() == 0) {
            context.rewind();
            return context;
        }
        SensorValues newContext = new SensorValues(policyConversionModel, owlModel, GlobalVars.base);

        if (!hasCycles(contexts, newContext)) {

            HashMap<SensorValues, SensorValues> myContexts = new HashMap<SensorValues, SensorValues>(contexts);
            myContexts.put(newContext, newContext);

            if (contextEvaluationResult.getFirst() > 0) {

                Individual brokenPolicy = contextEvaluationResult.getSecond();
                StmtIterator iterator = brokenPolicy.listProperties(associatedResource);

                associatedResources.clear();
                while (iterator.hasNext()) {
                    associatedResources.add(iterator.nextStatement().getResource());
                }

                for (Resource attachedResource : associatedResources) {

                    //get the resource as individual from the global model such that getPropertyValue can be called on it
                    Individual sensor = policyConversionModel.getIndividual(attachedResource.toString());

                    //skip sensor if its value respects the policy
                    if (sensorHasAcceptableValue(sensor)) {
                        //      System.err.println(sensor + " respects policy");
                        continue;
                    }

                    // System.err.println("Sensor : " + sensor + " value :" + sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]);

                    //get all actuators associated to the current sensor
                    StmtIterator associatedActuators = sensor.listProperties(associatedActuatorProperty);

                    //transfer to array list because if i iterate over and execute actions ConcurrentmodificationException is thrown by iterator

                    actuatorsList.clear();
                    while (associatedActuators.hasNext()) {
                        actuatorsList.add(associatedActuators.nextStatement().getResource());
                    }

                    int actuatorsListLength = actuatorsList.size();
                    for ( int i = 0; i < actuatorsListLength; i++) {
                        
                        Resource attachedActuatorResource = actuatorsList.get(i);
                        //Resource attachedActuatorResource =
                        Individual actuator = policyConversionModel.getIndividual(attachedActuatorResource.toString());

                        //System.err.println("Actuator:" + actuator);

                        StmtIterator actuatorActions = actuator.listProperties(associatedActionProperty);

                        //get all actions possible on this 

                        actionsList.clear();
                        while (actuatorActions.hasNext()) {
                            actionsList.add(actuatorActions.nextStatement().getResource());
                        }
                        for (Resource attachedActionResource : actionsList) {
                            //Resource attachedActionResource = actuatorActions.nextStatement().getResource();
                            Individual action = policyConversionModel.getIndividual(attachedActionResource.toString());
                            String effect = action.getPropertyValue(actionEffect).toString().split("\\^")[0];
                            //System.err.println("Action:" + action + " Effect: " + effect);

                            char firstChar = effect.trim().charAt(0);
                            if (firstChar == '+') {
                                try {
                                    String numberString = effect.substring(1, effect.length());
                                    int value = integerNumberFormat.parse(numberString).intValue();
                                    IncrementCommand incrementCommand = new IncrementCommand(sensor.toString(), sensorValueProperty.toString(), hasWebServiceProperty.toString(), policyConversionModel, value);
                                    Queue<Command> incrementQueue = new LinkedList(context.getActions());

                                    incrementCommand.execute();
                                    incrementQueue.add(incrementCommand);

                                    ContextSnapshot afterIncrement = new ContextSnapshot(policyConversionModel, incrementQueue, context.getJenaOwlModel());
                                    queue.add(afterIncrement);
                                    incrementCommand.rewind();

                                } catch (ParseException ex) {
                                    Logger.getLogger(ReinforcementLearningBasicBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            } else if (firstChar == '-') {
                                try {
                                    String numberString = effect.substring(1, effect.length());
                                    int value = integerNumberFormat.parse(numberString).intValue();
                                    Queue<Command> decrementQueue = new LinkedList(context.getActions());
                                    DecrementCommand decrementCommand = new DecrementCommand(sensor.toString(), sensorValueProperty.toString(), hasWebServiceProperty.toString(), policyConversionModel, value);
                                    decrementCommand.execute();
                                    decrementQueue.add(decrementCommand);
                                    ContextSnapshot afterDecrement = new ContextSnapshot(policyConversionModel, decrementQueue, context.getJenaOwlModel());
                                    queue.add(afterDecrement);
                                    decrementCommand.rewind();

                                } catch (ParseException ex) {
                                    Logger.getLogger(ReinforcementLearningBasicBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else if (firstChar >= '0' && firstChar <= '9') {
                                try {
                                    String numberString = effect.substring(0, effect.length());
                                    int value = integerNumberFormat.parse(numberString).intValue();

                                    SetCommand setCommand = new SetCommand(sensor.toString(), sensorValueProperty.toString(), hasWebServiceProperty.toString(), policyConversionModel, value);

                                    int before = integerNumberFormat.parse(sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]).intValue();
                                    setCommand.execute();
                                    int after = integerNumberFormat.parse(sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]).intValue();

                                    if (after != before) {
                                        Queue<Command> setCommandQueue = new LinkedList(context.getActions());
                                        setCommandQueue.add(setCommand);
                                        ContextSnapshot afterSet = new ContextSnapshot(policyConversionModel, setCommandQueue, context.getJenaOwlModel());
                                        queue.add(afterSet);
                                    }
                                    setCommand.rewind();

                                } catch (ParseException ex) {
                                    Logger.getLogger(ReinforcementLearningBasicBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                throw new Exception("Unsupported effect exception:" + firstChar);
                            }
                        }
                    }
                }

                if (queue.peek() == null) {
                    System.err.println("EMPTY QUEUE");
                    context.rewind();
                    return context;
                } else {
                    context.rewind();
                    return reinforcementLearning(queue, new HashMap<SensorValues, SensorValues>(myContexts));
                }
            }
        } else {
            if (queue.peek() == null) {
                System.err.println("EMPTY QUEUE");
                context.rewind();
                return context;
            } else {
                context.rewind();
                return reinforcementLearning(queue, new HashMap<SensorValues, SensorValues>(contexts));
            }
        }
        context.rewind();
        return context;
    }

    public double evaluateResourceValue(double currentValue, double wantedValue) {
        if (currentValue - wantedValue < 0) {
            return (wantedValue - currentValue);
        }
        return (currentValue - wantedValue);
    }

    private void setBrokenResources(ContextSnapshot contextSnapshot) {

        Map<String, String> brokenResources = GlobalVars.getBrokenResources();
        Map<String, String> validResources = GlobalVars.getValidResources();
        brokenResources.clear();
        validResources.clear();

        //set for printing
        ArrayList<String> brokenPoliciesNames = new ArrayList<String>();

        Collection<RDFResource> resources = contextSnapshot.getJenaOwlModel().getRDFResources();

        for (RDFResource resource : resources) {

            if (resource.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("policy"))) {
                Individual policy = policyConversionModel.getIndividual(GlobalVars.base + "#" + resource.getProtegeType().getName() + "I").asIndividual();
                StmtIterator sensorsForBrokenPolicy = policy.listProperties(associatedResource);
                if (!getEvaluateProp(policy)) {

                    brokenPoliciesNames.add(resource.getProtegeType().getName());

                    while (sensorsForBrokenPolicy.hasNext()) {
                        Resource res = sensorsForBrokenPolicy.nextStatement().getResource();
                        Individual sensor = policyConversionModel.getIndividual(res.toString());
                        //skip sensor if its value respects the policy
                        if (!sensorHasAcceptableValue(sensor)) {
                            brokenResources.put(sensor.getLocalName(), sensor.getLocalName());
                        } else {
                            validResources.put(sensor.getLocalName(), sensor.getLocalName());
                        }
                    }
                } else {
                    while (sensorsForBrokenPolicy.hasNext()) {
                        Resource res = sensorsForBrokenPolicy.nextStatement().getResource();
                        Individual sensor = policyConversionModel.getIndividual(res.toString());
                        //skip sensor if its value respects the policy
                        validResources.put(sensor.getLocalName(), sensor.getLocalName());
                    }
                }
            }
        }

        resultsFrame.setBrokenPoliciesList(brokenPoliciesNames);

    }

    @Override
    protected void onTick() {

        Queue<ContextSnapshot> queue = new LinkedList<ContextSnapshot>();
        ContextSnapshot initialContext = new ContextSnapshot(policyConversionModel, new LinkedList<Command>(), owlModel);
        SensorValues currentValues = new SensorValues(policyConversionModel, owlModel, GlobalVars.base);
        queue.add(initialContext);

        resultsFrame.setActionsList(null);
        resultsFrame.setBrokenStatesList(currentValues.toArrayList());

        setBrokenResources(initialContext);

        smallestEntropy = 10000;
        ContextSnapshot contextSnapshot;
        if (computeEntropy(initialContext).getFirst() != 0) {
            try {
                long startMinutes = new java.util.Date().getTime();

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setLanguage("JavaSerialization");

                msg.setContent("BROKEN");
                msg.addReceiver(new AID(GlobalVars.RLAGENT_NAME + "@" + agent.getContainerController().getPlatformName()));
                agent.send(msg);
                agent.setContextDirty(false);
                contextSnapshot = reinforcementLearning(queue, new HashMap<SensorValues, SensorValues>());

                msg.setContent("OK");
                msg.addReceiver(new AID(GlobalVars.RLAGENT_NAME + "@" + agent.getContainerController().getPlatformName()));
                agent.send(msg);

                long endMinutes = new java.util.Date().getTime();

                int value = (int) ((endMinutes - startMinutes) / 1000);

                agent.setRlTime(value);

                System.err.println("Reinforcement alg running time: " + value + " seconds");

            } catch (Exception ex) {
                Logger.getLogger(ReinforcementLearningBasicBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }

            //do nothing if 
            if ( agent.isContextDirty()){
                return;
            }
            setBrokenResources(contextSnapshot);

            Queue<Command> bestActionsList = contextSnapshot.getActions();

            ArrayList<String[]> actions = new ArrayList<String[]>(bestActionsList.size());


            System.err.println();
            System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.err.println("for " + currentValues);
            System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.err.println("===============================================================");
            for (Command command : bestActionsList) {
                actions.add(command.toStringArray());
                System.err.println(command);
            }
            System.err.println("===============================================================");
            System.err.println();

            resultsFrame.setActionsList(actions);

            memory.memorize(currentValues, bestActionsList);
            contextSnapshot.executeActions();
            contextSnapshot.executeActionsOnOWL();

            setBrokenResources(initialContext);

            System.out.println("");
        }
    }

    public boolean getEvaluateProp(Individual policy) {
        //System.out.println(base + "#EvaluatePolicyP");
        Statement property = policy.getProperty(evaluatePolicyProperty);

        if (property == null) {
            return false;
        }

        return property.getBoolean();
    }

    public boolean sensorHasAcceptableValue(Individual sensor) {
        Statement property = sensor.getProperty(hasAcceptedValueProperty);

        if (property == null) {
            return false;
        }

        return property.getBoolean();
    }
}
