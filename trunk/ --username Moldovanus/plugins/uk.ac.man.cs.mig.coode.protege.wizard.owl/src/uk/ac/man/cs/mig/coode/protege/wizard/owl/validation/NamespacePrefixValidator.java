package uk.ac.man.cs.mig.coode.protege.wizard.owl.validation;

import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         14-Feb-2005
 */
public class NamespacePrefixValidator implements StringValidator {

    public boolean isValid(String s, Collection currentValues, String currentProp, Collection errors) {

        return !s.contains(" ");
    }

    public String fix(String s, Collection currentValues, String currentProp, Collection log) {
        return "Fix NOT implemented for Namespace validator";
    }
}
