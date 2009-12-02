/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.command;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public abstract class Command implements Serializable {

    protected Individual targetIndividual;
    protected Property targetProperty;
    protected com.hp.hpl.jena.ontology.OntModel policyConversionModel;

    public Property getTargetProperty() {
        return targetProperty;
    }

    public void setTargetProperty(Property targetProperty) {
        this.targetProperty = targetProperty;
    }

    public Command(Individual targetIndividual, Property targetProperty, OntModel policyConversionModel) {
        this.targetIndividual = targetIndividual;
        this.targetProperty = targetProperty;
        this.policyConversionModel = policyConversionModel;
    }

    public abstract void execute();

    public abstract void rewind();

    @Override
    public abstract String toString();

    public abstract void setOWLValue();
}
