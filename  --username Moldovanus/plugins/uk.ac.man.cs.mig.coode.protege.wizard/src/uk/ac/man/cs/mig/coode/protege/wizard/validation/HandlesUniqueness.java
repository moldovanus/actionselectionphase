package uk.ac.man.cs.mig.coode.protege.wizard.validation;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         28-Jul-2004
 */
public interface HandlesUniqueness {

  public boolean isUnique(String s, Collection currentEntries, String currentProp, Collection errors);

  public boolean addUniqueProperty(String propertyName);
}
