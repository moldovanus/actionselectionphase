/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.command;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class IncrementCommand extends Command {

    private int incrementValue = 1;

    public IncrementCommand(String targetIndividual, String targetProperty, String hasWebService, OntModel policyConversionModel, int incrementValue) {
        super(targetIndividual, targetProperty, hasWebService, policyConversionModel);
        this.incrementValue = incrementValue;
    }

    public int getIncrementValue() {
        return incrementValue;
    }

    public void setIncrementValue(int incrementValue) {
        this.incrementValue = incrementValue;
    }

    @Override
    public void execute() {
        Individual targetIndividual = policyConversionModel.getIndividual(targetIndividualName);
        Property targetProperty = policyConversionModel.getProperty(targetPropertyName);

        RDFNode rdfValue = targetIndividual.getPropertyValue(targetProperty);
        try {
            int value = (NumberFormat.getIntegerInstance()).parse(rdfValue.toString().split("\\^")[0]).intValue();
            value += incrementValue;
            targetIndividual.setPropertyValue(targetProperty, policyConversionModel.createLiteralStatement(
                    targetIndividual, targetProperty, value).getLiteral().as(RDFNode.class));
        } catch (java.text.ParseException ex) {
            Logger.getLogger(IncrementCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rewind() {

        Individual targetIndividual = policyConversionModel.getIndividual(targetIndividualName);
        Property targetProperty = policyConversionModel.getProperty(targetPropertyName);

        RDFNode rdfValue = targetIndividual.getPropertyValue(targetProperty);
        try {
            int value = (NumberFormat.getIntegerInstance()).parse(rdfValue.toString().split("\\^")[0]).intValue();
            value -= incrementValue;
            targetIndividual.setPropertyValue(targetProperty, policyConversionModel.createLiteralStatement(
                    targetIndividual, targetProperty, value).getLiteral().as(RDFNode.class));
        } catch (java.text.ParseException ex) {
            Logger.getLogger(IncrementCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return "Decrement " + targetIndividualName + " by " + incrementValue;
    }

    @Override
    public void setOWLValue() {

        Individual targetIndividual = policyConversionModel.getIndividual(targetIndividualName);
        Property targetProperty = policyConversionModel.getProperty(targetPropertyName);
        Property hasWebServiceProperty = policyConversionModel.getProperty(hasWebServicePropertyName);


        RDFNode rdfValue = targetIndividual.getPropertyValue(targetProperty);

        float value = 0.0f;
        try {
            value = (NumberFormat.getNumberInstance()).parse(rdfValue.toString().split("\\^")[0]).floatValue();
        } catch (ParseException ex) {
            Logger.getLogger(IncrementCommand.class.getName()).log(Level.SEVERE, null, ex);
        }

        String xmldata =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap12:Body>" +
                "<SetSensorValue xmlns=\"http://tempuri.org/\">\n<value>" + Float.toString(value) + "</value>\n</SetSensorValue> \n" +
                "</soap12:Body>\n" +
                "</soap12:Envelope>";

        try {

            RDFNode sensorWSUrl = targetIndividual.getPropertyValue(hasWebServiceProperty);
            if (sensorWSUrl != null) {
                String wsURL = sensorWSUrl.toString().split("\\^")[0];
                wsURL = wsURL.substring(0, wsURL.lastIndexOf("/"));
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
                /*BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                // Response
                String line;
                while ((line = rd.readLine()) != null) {
                System.out.println(line);
                }*/


                sock.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();

        }

    }
}
