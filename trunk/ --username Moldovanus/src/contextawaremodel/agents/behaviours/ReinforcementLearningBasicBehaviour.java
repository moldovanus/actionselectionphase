/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

import actionselection.command.Command;
import actionselection.command.DecrementCommand;
import actionselection.command.IncrementCommand;
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
    private HashMap<SensorValues, SensorValues> checkCycles;
    private Memory memory;
    private Property evaluatePolicyProperty;
    private Property hasWebServiceProperty;

    public ReinforcementLearningBasicBehaviour(Agent a, OWLModel contextAwareModel, OntModel policyConversionModel, JenaOWLModel owlModel, Memory memory) {
        super(a, 1000);
        this.contextAwareModel = contextAwareModel;
        this.policyConversionModel = policyConversionModel;
        this.owlModel = owlModel;
        this.memory = memory;
        evaluatePolicyProperty = policyConversionModel.getDatatypeProperty(GlobalVars.base + "#EvaluatePolicyP");
        hasWebServiceProperty = policyConversionModel.getDatatypeProperty(GlobalVars.base + "#has-web-service-URI");
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
                    entropy++;
                }
            }
        }
        System.err.println("!!!!!! Entropy : " + entropy + " Broken " + brokenPolicy);
        return new Pair<Double, Individual>(entropy, brokenPolicy);
    }

    public Boolean hasCycles(HashMap<SensorValues, SensorValues> contexts, SensorValues myContext) {
        if (contexts.get(myContext) != null) {
            return true;
        } else {
            return false;
        }
    }

    public ContextSnapshot reinforcementLearning(Queue<ContextSnapshot> queue, HashMap<SensorValues, SensorValues> contexts) {

        ContextSnapshot context = queue.remove();
        SensorValues values = new SensorValues(context.getPolicyConversionModel(), context.getJenaOwlModel(), GlobalVars.base);
        Queue<Command> actions = memory.getActions(values);

        //exists
        if (actions != null) {
            context.addActions(actions);
            System.out.println("Remembered");
            return context;
        }

        context.executeActions();

        if (computeEntropy(context).getFirst() == 0) {
            context.rewind();
            return context;
        }

        if (!hasCycles(contexts, new SensorValues(context.getPolicyConversionModel(), context.getJenaOwlModel(), GlobalVars.base))) {


            HashMap<SensorValues, SensorValues> myContexts = new HashMap<SensorValues, SensorValues>(contexts);
            SensorValues newContext = new SensorValues(context.getPolicyConversionModel(), context.getJenaOwlModel(), GlobalVars.base);
            myContexts.put(newContext, newContext);


            Pair<Double, Individual> contextEvaluationResult = computeEntropy(context);

            if (contextEvaluationResult.getFirst() > 0) {
                OntModel model = context.getPolicyConversionModel();
                Individual brokenPolicy = contextEvaluationResult.getSecond();

                ObjectProperty associatedResource = model.getObjectProperty(GlobalVars.base + "#associated-resource");
                ObjectProperty associatedActuatorProperty = model.getObjectProperty(GlobalVars.base + "#has-actuator");
                ObjectProperty associatedActionProperty = model.getObjectProperty(GlobalVars.base + "#associated-action");
                Property actionEffect = model.getProperty(GlobalVars.base + "#effect");
                Property sensorValueProperty = model.getDatatypeProperty(GlobalVars.base + "#has-value-of-service");
                //System.out.println(model);

                //Resource res = brokenPolicy.getProperty(associatedResource).getResource();

                //list associated resources
                StmtIterator iterator = brokenPolicy.listProperties(associatedResource);
                //System.err.println("##########################");
                //System.err.println(brokenPolicy);
                while (iterator.hasNext()) {
                    //get associated resource name
                    Resource attachedResource = iterator.nextStatement().getResource();

                    //get the resource as individual from the global model such that getPropertyValue can be called on it
                    Individual sensor = model.getIndividual(attachedResource.toString());

                    System.err.println("Sensor : " + sensor + " value :" + sensor.getPropertyValue(sensorValueProperty).toString().split("\\^")[0]);

                    //get all actuators associated to the current sensor
                    StmtIterator associatedActuators = sensor.listProperties(associatedActuatorProperty);

                    //transfer to array list because if i iterate over and execute actions ConcurrentmodificationException is thrown by iterator
                    ArrayList<Resource> actuatorsList = new ArrayList<Resource>(10);
                    while (associatedActuators.hasNext()) {
                        actuatorsList.add(associatedActuators.nextStatement().getResource());
                    }
                    for (Resource attachedActuatorResource : actuatorsList) {
                        //Resource attachedActuatorResource =
                        Individual actuator = model.getIndividual(attachedActuatorResource.toString());

                        //System.err.println("Actuator:" + actuator);

                        StmtIterator actuatorActions = actuator.listProperties(associatedActionProperty);

                        //get all actions possible on this 
                        ArrayList<Resource> list = new ArrayList<Resource>(10);
                        while (actuatorActions.hasNext()) {
                            list.add(actuatorActions.nextStatement().getResource());
                        }
                        for (Resource attachedActionResource : list) {
                            //Resource attachedActionResource = actuatorActions.nextStatement().getResource();
                            Individual action = model.getIndividual(attachedActionResource.toString());
                            String effect = action.getPropertyValue(actionEffect).toString().split("\\^")[0];
                            //System.err.println("Action:" + action + " Effect: " + effect);
                            if (effect.trim().charAt(0) == '+') {
                                try {
                                    String numberString = effect.substring(1, effect.length());
                                    float value = NumberFormat.getNumberInstance().parse(numberString).floatValue();
                                    IncrementCommand incrementCommand = new IncrementCommand(sensor.toString(), sensorValueProperty.toString(), hasWebServiceProperty.toString(), model, value);
                                    Queue<Command> incrementQueue = new LinkedList(context.getActions());
                                    incrementCommand.execute();
                                    incrementQueue.add(incrementCommand);

                                    ContextSnapshot afterIncrement = new ContextSnapshot(model, incrementQueue, context.getJenaOwlModel());
                                    queue.add(afterIncrement);
                                    incrementCommand.rewind();
                                } catch (ParseException ex) {
                                    Logger.getLogger(ReinforcementLearningBasicBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            } else if (effect.trim().charAt(0) == '-') {
                                try {
                                    String numberString = effect.substring(1, effect.length());
                                    float value = NumberFormat.getNumberInstance().parse(numberString).floatValue();
                                    Queue<Command> decrementQueue = new LinkedList(context.getActions());
                                    DecrementCommand decrementCommand = new DecrementCommand(sensor.toString(), sensorValueProperty.toString(), hasWebServiceProperty.toString(), model, value);
                                    decrementCommand.execute();
                                    decrementQueue.add(decrementCommand);

                                    ContextSnapshot afterDecrement = new ContextSnapshot(model, decrementQueue, context.getJenaOwlModel());
                                    queue.add(afterDecrement);
                                    decrementCommand.rewind();

                                } catch (ParseException ex) {
                                    Logger.getLogger(ReinforcementLearningBasicBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            //System.err.println("\n \n \n ");
                        }
                        //System.err.println("\n \n \n ");
                    }

                    //Individual sensor = model.getIndividual(associatedResource).asIndividual();
                    /*Property sensorValue = model.getDatatypeProperty(base + "#has-value-of-service");


                    IncrementCommand incrementCommand = new IncrementCommand(sensor, sensorValue, model);
                    Queue<Command> incrementQueue = new LinkedList(context.getActions());
                    incrementCommand.execute();
                    incrementQueue.add(incrementCommand);

                    ContextSnapshot afterIncrement = new ContextSnapshot(model, incrementQueue, context.getJenaOwlModel());
                    queue.add(afterIncrement);
                    incrementCommand.rewind();

                    Queue<Command> decrementQueue = new LinkedList(context.getActions());
                    DecrementCommand decrementCommand = new DecrementCommand(sensor, sensorValue, model);
                    decrementCommand.execute();
                    decrementQueue.add(decrementCommand);

                    ContextSnapshot afterDecrement = new ContextSnapshot(model, decrementQueue, context.getJenaOwlModel());
                    queue.add(afterDecrement);
                    decrementCommand.rewind();*/
                    // System.err.println("\n \n \n ");
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

    @Override
    protected void onTick() {

        Queue<ContextSnapshot> queue = new LinkedList<ContextSnapshot>();
        ContextSnapshot initialContext = new ContextSnapshot(policyConversionModel, new LinkedList<Command>(), owlModel);
        SensorValues currentValues = new SensorValues(policyConversionModel, owlModel, GlobalVars.base);
        queue.add(initialContext);
        ContextSnapshot contextSnapshot = reinforcementLearning(queue, new HashMap<SensorValues, SensorValues>());//, 0/*, new LinkedList<ContextSnapshot>()*/);

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
}
