/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

import actionselection.command.Command;
import actionselection.command.SetCommand;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import contextawaremodel.agents.ReinforcementLearningAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Me
 */
public class ContextDisturbingBehaviour extends TickerBehaviour {

    private com.hp.hpl.jena.ontology.OntModel policyConversionModel;
    private ArrayList<Command> commands;
    private ArrayList<ArrayList<Command>> myList = new ArrayList<ArrayList<Command>>(4);
    private int currentindex = 0;
    private Random random = new Random();
    private ReinforcementLearningAgent agent;

    public ContextDisturbingBehaviour(Agent a, long period, OntModel policyConversionModel) {
        super(a, period);
        agent = (ReinforcementLearningAgent) a;
        this.policyConversionModel = policyConversionModel;

        myList = new ArrayList<ArrayList<Command>>();
        commands = new ArrayList<Command>(5);
        ///StudentSensorRule
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#AlarmStateSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 0));
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#FaceRecognitionSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 2));
        myList.add(commands);
        commands = new ArrayList<Command>(5);
        ///Allarm Professor
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#AlarmStateSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 0));

        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#FaceRecognitionSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 0));
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#ComputerStateSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 0));
        ///LightPolicy
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#RoomStateSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 1));
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#LightSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 0));
        myList.add(commands);

        commands = new ArrayList<Command>(5);
        ///StudentSensorRule
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#AlarmStateSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 1));
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#FaceRecognitionSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 1));
        myList.add(commands);

        commands = new ArrayList<Command>(5);

        //Temperature = 9 and Humidity = 18 -> TemperatureAndHumidityProlicyI broken
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#TemperatureSensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 9));
        commands.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#HumiditySensorI",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 18));
        myList.add(commands);




    }

    public synchronized void setValue(int value) {
        String xmldata =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap12:Body>" +
                "<SetSensorValue xmlns=\"http://tempuri.org/\">\n<value>" + Integer.toString(value) + "</value>\n</SetSensorValue> \n" +
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

            Individual targetIndividual = policyConversionModel.getIndividual("http://www.owl-ontologies.com/Ontology1230214892.owl#TemperatureSensorI").asIndividual();
            Property targetProperty = policyConversionModel.getDatatypeProperty("http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service");

            targetIndividual.setPropertyValue(targetProperty, policyConversionModel.createLiteralStatement(
                    targetIndividual, targetProperty, value).getLiteral().as(RDFNode.class));
            System.err.println("[[[[[[[ Temperature set to :" + value + "]]]]]]]]");
            sock.close();



        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }

    @Override
    protected void onTick() {
        /* int f = (new Random()).nextInt() * 2;
        setValue(5);
        SetCommand command = new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#AlarmStateSensorI",
        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 0);
        command.execute();

        SetCommand command2 = new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#HumiditySensorI",
        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
        "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, 13);
        command2.execute();
         */

        /*ArrayList<Command> commandList = myList.get(currentindex);
        System.err.println("Context broken intentionally " + currentindex);
        for (Command c : commandList) {
        c.execute();
        c.setOWLValue();
        }

        currentindex++;
        if (currentindex > 3) {
        currentindex = 0;
        }

         */

        if (agent.isContextIsOK()) {
            ArrayList<Command> commandList = new ArrayList<Command>(7);


            commandList.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#AlarmStateSensorI",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, random.nextInt(2)));
            commandList.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#FaceRecognitionSensorI",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, random.nextInt(3)));
            
            commandList.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#ComputerStateSensorI",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, random.nextInt(1)));

            commandList.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#HumiditySensorI",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, random.nextInt(20) + 15));

            commandList.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#TemperatureSensorI",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, random.nextInt(10) + 15));

            commandList.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#LightSensorI",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, random.nextInt(2)));

            commandList.add(new SetCommand("http://www.owl-ontologies.com/Ontology1230214892.owl#RoomStateSensorI",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-value-of-service",
                    "http://www.owl-ontologies.com/Ontology1230214892.owl#has-web-service-URI", policyConversionModel, random.nextInt(2)));

            for (Command c : commandList) {
                c.execute();
                c.setOWLValue();
            }

            System.err.println("Context disturbed");
        }

    }
}
