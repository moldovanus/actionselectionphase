package uk.ac.man.cs.mig.coode.protege.wizard.validation.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         14-Feb-2005
 */
public class AnyStringValidator implements StringValidator {

  public boolean isValid(String s, Collection currentEntries, String currentProp, Collection errors) {
    return true;
  }

  public String fix(String s, Collection currentEntries, String currentProp, Collection log) {
    return s;
  }
}
