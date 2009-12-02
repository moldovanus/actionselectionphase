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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Administrator
 */
public class ReinforcementLearningBasicBehaviour extends TickerBehaviour {

    private OWLModel contextAwareModel;
    public static String base = "http://www.owl-ontologies.com/Ontology1230214892.owl";
    private com.hp.hpl.jena.ontology.OntModel policyConversionModel;
    private JenaOWLModel owlModel;
    private HashMap<SensorValues, SensorValues> checkCycles;
    private Memory memory;

    public ReinforcementLearningBasicBehaviour(Agent a, OWLModel contextAwareModel, OntModel policyConversionModel, JenaOWLModel owlModel, Memory memory) {
        super(a, 1000);
        this.contextAwareModel = contextAwareModel;
        this.policyConversionModel = policyConversionModel;
        this.owlModel = owlModel;
        this.memory = memory;
    }

    private Pair<Double, Individual> computeEntropy(ContextSnapshot contextSnapshot) {
        Individual brokenPolicy = null;
        double entropy = 0.0;
        Collection<RDFResource> resources = contextSnapshot.getJenaOwlModel().getRDFResources();

        for (RDFResource resource : resources) {

            if (resource.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("policy"))) {
                Individual policy = policyConversionModel.getIndividual(base + "#" + resource.getProtegeType().getName() + "I").asIndividual();
                if (!getEvaluateProp(policy)) {
                    if (brokenPolicy == null) {
                        brokenPolicy = policy;
                    }
                    entropy++;
                }
            }
        }

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
        SensorValues values = new SensorValues(context.getPolicyConversionModel(), context.getJenaOwlModel(), base);
        Queue<Command> actions = memory.getActions(values);


        // exists
        if (actions != null) {
            context.addActions(actions);
            System.out.println("Remembered");
            return context;
        }

        context.executeActions();

        if (computeEntropy(context).getFirst() == 0) {
            return context;
        }
        if (!hasCycles(contexts, new SensorValues(context.getPolicyConversionModel(), context.getJenaOwlModel(), base))) {


            HashMap<SensorValues, SensorValues> myContexts = new HashMap<SensorValues, SensorValues>(contexts);
            SensorValues newContext = new SensorValues(context.getPolicyConversionModel(), context.getJenaOwlModel(), base);
            myContexts.put(newContext, newContext);


            Pair<Double, Individual> contextEvaluationResult = computeEntropy(context);

            if (contextEvaluationResult.getFirst() > 0) {
                OntModel model = context.getPolicyConversionModel();
                Individual brokenPolicy = contextEvaluationResult.getSecond();

                ObjectProperty associatedResource = model.getObjectProperty(base + "#associated-resource");
                Resource res = brokenPolicy.getProperty(associatedResource).getResource();

                Individual sensor = model.getIndividual(res.toString()).asIndividual();
                Property sensorValue = model.getDatatypeProperty(base + "#has-value-of-service");


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
                decrementCommand.rewind();
                context.rewind();
                if (queue.peek() == null) {
                    System.err.println("EMPTY QUEUE");
                    return context;
                } else {
                    return reinforcementLearning(queue, new HashMap<SensorValues, SensorValues>(myContexts));
                }
            }


        }
        if (queue.peek() == null) {
            System.err.println("EMPTY QUEUE");
            return context;
        } else {
            return reinforcementLearning(queue, new HashMap<SensorValues, SensorValues>(contexts));
        }

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
        SensorValues currentValues = new SensorValues(policyConversionModel, owlModel, base);
        queue.add(initialContext);
        ContextSnapshot contextSnapshot = reinforcementLearning(queue, new HashMap<SensorValues, SensorValues>());//, 0/*, new LinkedList<ContextSnapshot>()*/);

        Queue<Command> bestActionsList = contextSnapshot.getActions();
        System.err.println();
        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.err.println(currentValues);
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
        Statement property = policy.getProperty(policyConversionModel.getDatatypeProperty(base + "#EvaluatePolicyP"));

        if (property == null) {
            return false;
        }

        return property.getBoolean();
    }
}
