/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author Me
 */
public class ContextDisturbingBehaviour extends TickerBehaviour {

    private com.hp.hpl.jena.ontology.OntModel policyConversionModel;

    public ContextDisturbingBehaviour(Agent a, long period, OntModel policyConversionModel) {
        super(a, period);
        this.policyConversionModel = policyConversionModel;
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
        float f = (new Random()).nextFloat() * 2;
        setValue(3.0f);
    }
}
