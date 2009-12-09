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
import actionselection.utils.Pair;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import contextawaremodel.GlobalVars;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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

    public ReinforcementLearningBasicBehaviour(Agent a, OWLModel contextAwareModel, OntModel policyConversionModel, JenaOWLModel owlModel, Memory memory) {
        super(a, 1000);
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
        associatedResources = new ArrayList<Resource>(10);
        actuatorsList = new ArrayList<Resource>(10);
        actionsList = new ArrayList<Resource>(10);
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
        // do {
        //    context = queue.remove();
        // } while ( context.getContextEntropy() > smallestEntropy);


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
                    for (Resource attachedActuatorResource : actuatorsList) {
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

                                    //System.err.println("Before: Sensor  " + sensor + " value :" + sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]);

                                    incrementCommand.execute();
                                    incrementQueue.add(incrementCommand);
                                    //System.err.println(incrementCommand);
                                    //System.err.println("After : Sensor  " + sensor + " value :" + sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]);
                                    //System.err.println(incrementCommand + "---> Level: " + context.getCurrentLevel());
                                    ContextSnapshot afterIncrement = new ContextSnapshot(policyConversionModel, incrementQueue, context.getJenaOwlModel());
                                    queue.add(afterIncrement);
                                    double entropy = computeEntropy(context).getFirst();
                                    afterIncrement.setContextEntropy(entropy);
                                    if (smallestEntropy > entropy) {
                                        smallestEntropy = entropy;
                                    }

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

                                    // System.err.println("Before: Sensor  " + sensor + " value :" + sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]);

                                    decrementCommand.execute();
                                    decrementQueue.add(decrementCommand);

                                    //System.err.println(decrementCommand);
                                    //System.err.println("After : Sensor  " + sensor + " value :" + sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]);
                                    //System.err.println(decrementCommand + "---> Level: " + context.getCurrentLevel());

                                    ContextSnapshot afterDecrement = new ContextSnapshot(policyConversionModel, decrementQueue, context.getJenaOwlModel());
                                    queue.add(afterDecrement);

                                    double entropy = computeEntropy(context).getFirst();
                                    afterDecrement.setContextEntropy(entropy);
                                    if (smallestEntropy > entropy) {
                                        smallestEntropy = entropy;
                                    }
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
                                    // System.err.println("Before: Sensor  " + sensor + " value :" + before);

                                    setCommand.execute();


                                    int after = integerNumberFormat.parse(sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]).intValue();
                                    // System.err.println(setCommand + "---> Level: " + context.getCurrentLevel());
                                    // System.err.println("After : Sensor  " + sensor + " value :" + sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]);

                                    if (after != before) {
                                        // System.err.println("Action added");
                                        Queue<Command> setCommandQueue = new LinkedList(context.getActions());
                                        setCommandQueue.add(setCommand);
                                        ContextSnapshot afterSet = new ContextSnapshot(policyConversionModel, setCommandQueue, context.getJenaOwlModel());
                                        double entropy = computeEntropy(context).getFirst();
                                        afterSet.setContextEntropy(entropy);
                                        if (smallestEntropy > entropy) {
                                            smallestEntropy = entropy;
                                        }
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
                //System.err.println("\n");

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

    @Override
    protected void onTick() {

        Queue<ContextSnapshot> queue = new LinkedList<ContextSnapshot>();
        ContextSnapshot initialContext = new ContextSnapshot(policyConversionModel, new LinkedList<Command>(), owlModel);
        SensorValues currentValues = new SensorValues(policyConversionModel, owlModel, GlobalVars.base);
        queue.add(initialContext);

        smallestEntropy = 10000;
        ContextSnapshot contextSnapshot;//, 0/*, new LinkedList<ContextSnapshot>()*/);
        try {
            int startMinutes = new java.util.Date().getMinutes();
            contextSnapshot = reinforcementLearning(queue, new HashMap<SensorValues, SensorValues>());
            int endMinutes = new java.util.Date().getMinutes();


            System.err.println("Reinforcement alg running time: " + (endMinutes - startMinutes) + "minutes");

        } catch (Exception ex) {
            Logger.getLogger(ReinforcementLearningBasicBehaviour.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        Queue<Command> bestActionsList = contextSnapshot.getActions();
        System.err.println();
        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.err.println("for " + currentValues);
        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.err.println("===============================================================");
        for (Command command : bestActionsList) {
            System.err.println(command);
        }
        System.err.println("===============================================================");
        System.err.println();


        memory.memorize(currentValues, bestActionsList);
        contextSnapshot.executeActions();
        contextSnapshot.executeActionsOnOWL();

    }

    public synchronized void setValue(float value) {
        String xmldata =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap12:Body>" +
                "<SetSensorValue xmlns=\"http://tempuri.org/\">\n<value>" + Float.toString(value) + "</value>\n</SetSensorValue> \n" +
                "</soap12:Body>\n" +
                "</soap12:Envelope>";

        try {

            String wsURL = "http://localhost:2591/RandomTemperatureSensorWS.asmx";
            //Parse URL and create socket
            String[] uriDetails = wsURL.split("[:/]+");

            String hostname = uriDetails[1];
            int port = Integer.valueOf(uriDetails[2]);

            InetAddress addr = InetAddress.getByName(hostname);
            Socket sock = new Socket(addr, port);
            String path = uriDetails[3];
            for (int i = 4; i < uriDetails.length; i++) {
                path += "/" + uriDetails[i];
            }


            //Send header
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "utf-8"));
            wr.write("POST /" + path + " HTTP/1.1\r\n");
            wr.write("Host: " + hostname + "\r\n");
            wr.write("Content-Type: text/xml; charset=\"utf-8 \" \r\n");
            wr.write("Content-Length: " + xmldata.length() + "\r\n");
            wr.write("SOAPAction: http://tempuri.org/SetSensorValue \r\n");
            wr.write("\r\n");

            //Send data
            wr.write(xmldata);// System.out.println(xmldata);
            wr.flush();
            BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            // Response
            //String line;
            /*while ((line = rd.readLine()) != null) {
            // System.out.println(line);
            }*/


            sock.close();

        } catch (IOException ex) {
            ex.printStackTrace();

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
        //System.out.println(base + "#EvaluatePolicyP");
        Statement property = sensor.getProperty(hasAcceptedValueProperty);

        if (property == null) {
            return false;
        }

        return property.getBoolean();
    }
}
