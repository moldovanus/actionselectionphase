/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package actionselection.command;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class IncrementCommand extends Command{

    private double incrementValue = 0.1;
    public IncrementCommand(Individual targetIndividual, Property targetProperty, OntModel policyConversionModel) {
        super(targetIndividual, targetProperty, policyConversionModel);
    }

    public double getIncrementValue() {
        return incrementValue;
    }

    public void setIncrementValue(double incrementValue) {
        this.incrementValue = incrementValue;
    }


    
    @Override
    public void execute() {
        RDFNode rdfValue = targetIndividual.getPropertyValue(targetProperty);
        try {
            float value = (NumberFormat.getNumberInstance()).parse(rdfValue.toString().split("\\^")[0]).floatValue();
            value += incrementValue;
            targetIndividual.setPropertyValue(targetProperty, policyConversionModel.createLiteralStatement(
                    targetIndividual, targetProperty, value).getLiteral().as(RDFNode.class));
        } catch (java.text.ParseException ex) {
            Logger.getLogger(IncrementCommand.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    @Override
    public void rewind() {
        RDFNode rdfValue = targetIndividual.getPropertyValue(targetProperty);
        try {
            float value = (NumberFormat.getNumberInstance()).parse(rdfValue.toString().split("\\^")[0]).floatValue();
            value -= incrementValue;
            targetIndividual.setPropertyValue(targetProperty, policyConversionModel.createLiteralStatement(
                    targetIndividual, targetProperty, value).getLiteral().as(RDFNode.class));
        } catch (java.text.ParseException ex) {
            Logger.getLogger(IncrementCommand.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    @Override
    public String toString() {
        return "Increment " + targetIndividual + " by " + incrementValue ;
    }



}
