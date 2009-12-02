/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

import actionselection.command.Command;
import actionselection.command.DecrementCommand;
import actionselection.command.IncrementCommand;
import actionselection.context.ContextSnapshot;
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
import java.text.NumberFormat;
import java.text.ParseException;
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
    public static String base = "http://www.owl-ontologies.com/Ontology1230214892.owl";
    private com.hp.hpl.jena.ontology.OntModel policyConversionModel;
    private JenaOWLModel owlModel;
    private Queue<SensorValues> a;
    public ReinforcementLearningBasicBehaviour(Agent a, OWLModel contextAwareModel, OntModel policyConversionModel, JenaOWLModel owlModel) {
        super(a, 1000);
        this.contextAwareModel = contextAwareModel;
        this.policyConversionModel = policyConversionModel;
        this.owlModel = owlModel;
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

    /*public ContextSnapshot reinforcementLearning(ContextSnapshot context/*, int max, Queue<ContextSnapshot> queue ) {
    Pair<Double, Individual> contextEvaluationResult = computeEntropy(context);
    if (contextEvaluationResult.getFirst() != 0) {

    OntModel model = context.getPolicyConversionModel();

    Individual brokenPolicy = contextEvaluationResult.getSecond();

    ObjectProperty associatedResource = model.getObjectProperty(base + "#associated-resource");
    Resource res = brokenPolicy.getProperty(associatedResource).getResource();
    Individual sensor = model.getIndividual(res.toString()).asIndividual();
    Property sensorValue = model.getDatatypeProperty(base + "#has-value-of-service");

    String init = sensor.getPropertyValue(sensorValue).toString();

    //max++;
    IncrementCommand incrementCommand = new IncrementCommand(sensor, sensorValue, model);
    Queue<Command> incrementQueue = new LinkedList(context.getActions());
    incrementCommand.execute();
    incrementQueue.add(incrementCommand);

    String a = sensor.getPropertyValue(sensorValue).toString();

    ContextSnapshot afterIncrement = new ContextSnapshot(model, incrementQueue, context.getJenaOwlModel());
    reinforcementLearning(afterIncrement);//, max);
    incrementCommand.rewind();

    Queue<Command> decrementQueue = new LinkedList(context.getActions());
    DecrementCommand decrementCommand = new DecrementCommand(sensor, sensorValue, model);
    decrementCommand.execute();
    decrementQueue.add(decrementCommand);

    String b = sensor.getPropertyValue(sensorValue).toString();

    ContextSnapshot afterDecrement = new ContextSnapshot(model, decrementQueue, context.getJenaOwlModel());
    reinforcementLearning(afterDecrement);//, max);
    decrementCommand.rewind();
    }

    // if (queue.size() != 0) {
    //     reinforcementLearning(queue.remove(), queue);
    // }

    return context;

    }*/
    public ContextSnapshot reinforcementLearning(Queue<ContextSnapshot> queue) {

        ContextSnapshot context = queue.remove();

        if (context.getActions().size() > 10) {

            Boolean empty = false;
            ContextSnapshot c = queue.remove();

            Double min = computeEntropy(c).getFirst();
            ContextSnapshot bestContext = c;
            while (!empty) {
                c.executeActions();
                if (min > computeEntropy(c).getFirst()) {
                    min = computeEntropy(c).getFirst();
                    bestContext = c; 
                }
                c.rewind();
                if (queue.size() > 0) {
                    c = queue.remove();
                } else {
                    empty = true;
                }

            }
            return bestContext;
        } else {
            
            context.executeActions();

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
                Double value1 = computeEntropy(afterIncrement).getFirst();
                incrementCommand.rewind();

                if (value1 > 0) { // k sa stie k i ok cotextu sa nu o mai ia razna

                    Queue<Command> decrementQueue = new LinkedList(context.getActions());
                    DecrementCommand decrementCommand = new DecrementCommand(sensor, sensorValue, model);
                    decrementCommand.execute();
                    decrementQueue.add(decrementCommand);


                    ContextSnapshot afterDecrement = new ContextSnapshot(model, decrementQueue, context.getJenaOwlModel());
                    Double value2 = computeEntropy(afterIncrement).getFirst();
                    queue.add(afterDecrement);
                    decrementCommand.rewind();
                    context.rewind();

                    if (value2 > 0) {
                        context = reinforcementLearning(queue);
                    }
                    
                }
            }
        }
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


        //Individual sensor = policyConversionModel.getIndividual(base + "#TemperatureSensorI").asIndividual();

        //Property temperature = policyConversionModel.getDatatypeProperty(base + "#has-value-of-service");


        /*RDFResource individual = contextAwareModel.getRDFResource("Temperature simulation");

        if (individual.getProtegeType().getNamedSuperclasses(true).contains(contextAwareModel.getRDFSNamedClass("sensor"))) {

        RDFProperty value = contextAwareModel.getRDFProperty("has-value-of-service");
        float sensor_value = Float.parseFloat(individual.getPropertyValue(value).toString().split(" ")[0]);

        System.out.println("Current temperature sensor value is " + sensor_value);


        //Individual sensor = policyConversionModel.getIndividual(base + "#TemperatureSensorI").asIndividual();

        // Property temperature = policyConversionModel.getDatatypeProperty(base + "#has-value-of-service");

        //sensor.setPropertyValue(temperature, policyConversionModel.createLiteralStatement(
        // sensor, temperature, sensor_value).getLiteral().as(RDFNode.class));


        Individual policy1 = policyConversionModel.getIndividual(base + "#TemperaturePolicyI").asIndividual();
        System.out.println("Policy1I.EvaluatePolicyP: " + getEvaluateProp(policy1));
        // setValue();
        }*/
        //policyConversionModel,final Queue<Command> actions,final JenaOWLModel owlModel
        Queue<ContextSnapshot> queue = new LinkedList<ContextSnapshot>();
        ContextSnapshot initialContext = new ContextSnapshot(policyConversionModel, new LinkedList<Command>(), owlModel);
        queue.add(initialContext);
        ContextSnapshot contextSnapshot = reinforcementLearning(queue);//, 0/*, new LinkedList<ContextSnapshot>()*/);
        //JOptionPane.showMessageDialog(null, "Done");
        Queue<Command> bestActionsList = contextSnapshot.getActions();
        contextSnapshot.executeActions();
        OntModel model = contextSnapshot.getPolicyConversionModel();
        Collection<RDFResource> resources = contextSnapshot.getJenaOwlModel().getRDFResources();
        for (RDFResource resource : resources) {
            if (resource.getProtegeType().getNamedSuperclasses(true).contains(owlModel.getRDFSNamedClass("sensor"))) {
                System.err.println("===============================================================");
                String name = resource.getProtegeType().getName();
                String name1[] = name.split("-");
                String nameF = "";
                for (int i = 0; i < name1.length; i++) {
                    nameF += name1[i].substring(0, 1).toUpperCase() + name1[i].substring(1, name1[i].length());
                }
                Individual res = model.getIndividual(base + "#" + nameF + "I").asIndividual();
                System.out.println("Name to get " + nameF + "I");
                Property resValue = model.getDatatypeProperty(base + "#has-value-of-service");
                String init = res.getPropertyValue(resValue).toString();
                float value = 0;
                try {
                    value = (NumberFormat.getNumberInstance()).parse(init.split("\\^")[0]).floatValue();
                } catch (ParseException ex) {
                    Logger.getLogger(ReinforcementLearningBasicBehaviour.class.getName()).log(Level.SEVERE, null, ex);
                }
               // setValue(value);
                // Resource res = resource.getProperty(associatedResource).getResource();
                System.out.println("Value being set " + value);
                System.err.println("===============================================================");
            }

        }



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
