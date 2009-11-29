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
public class DecrementCommand extends Command {

    private double decrementValue = 0.1;

    public double getDecrementValue() {
        return decrementValue;
    }

    public void setDecrementValue(float decrementValue) {
        this.decrementValue = decrementValue;
    }

    public DecrementCommand(Individual targetIndividual, Property targetProperty, OntModel policyConversionModel) {
        super(targetIndividual, targetProperty, policyConversionModel);
    }

    @Override
    public void execute() {
        RDFNode rdfValue = targetIndividual.getPropertyValue(targetProperty);
        try {
            float value = (NumberFormat.getNumberInstance()).parse(rdfValue.toString().split("\\^")[0]).floatValue();
            value -= decrementValue;
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
            value += decrementValue;
            targetIndividual.setPropertyValue(targetProperty, policyConversionModel.createLiteralStatement(
                    targetIndividual, targetProperty, value).getLiteral().as(RDFNode.class));
        } catch (java.text.ParseException ex) {
            Logger.getLogger(IncrementCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return "Decrement " + targetIndividual + " by " + decrementValue ;
    }
}
