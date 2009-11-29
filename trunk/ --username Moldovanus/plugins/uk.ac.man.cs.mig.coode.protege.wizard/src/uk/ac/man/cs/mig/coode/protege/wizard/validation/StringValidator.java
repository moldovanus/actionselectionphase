package uk.ac.man.cs.mig.coode.protege.wizard.validation;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public interface StringValidator {

  public boolean isValid(String s, Collection currentEntries, String currentProp, Collection errors);

  public String fix(String s, Collection currentEntries, String currentProp, Collection log);
}
