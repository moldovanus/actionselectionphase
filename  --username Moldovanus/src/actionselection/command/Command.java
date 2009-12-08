/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.command;

import com.hp.hpl.jena.ontology.OntModel;
import java.io.Serializable;
import java.text.NumberFormat;

/**
 *
 * @author Administrator
 */
public abstract class Command implements Serializable {

    protected String targetIndividualName;
    protected String targetPropertyName;
    protected String hasWebServicePropertyName;
    protected transient com.hp.hpl.jena.ontology.OntModel policyConversionModel;
    protected NumberFormat integerNumberFormat = NumberFormat.getIntegerInstance();


    
    public Command(String targetIndividual, String targetProperty, String hasWebService, OntModel policyConversionModel) {
        this.targetIndividualName = targetIndividual;
        this.targetPropertyName = targetProperty;
        this.hasWebServicePropertyName = hasWebService;
        this.policyConversionModel = policyConversionModel;
    }

    public final void setPolicyConversionModel(OntModel policyConversionModel) {
        this.policyConversionModel = policyConversionModel;
    }

    public abstract void execute();

    public abstract void rewind();

    @Override
    public abstract String toString();

    public abstract void setOWLValue();
}
