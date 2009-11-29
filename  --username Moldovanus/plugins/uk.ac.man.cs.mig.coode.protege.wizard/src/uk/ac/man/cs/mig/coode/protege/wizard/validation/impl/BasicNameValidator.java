package uk.ac.man.cs.mig.coode.protege.wizard.validation.impl;

import edu.stanford.smi.protege.util.CollectionUtilities;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.HandlesUniqueness;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         27-Jul-2004
 */
public class BasicNameValidator implements StringValidator, HandlesUniqueness {

  private PropertiesCollection pc;
  private Collection uniqueProperties;

  public BasicNameValidator(PropertiesCollection pc) {
    this.pc = pc;
  }

  public boolean isValid(String s, Collection currentEntries, String currentProp, Collection errors) {
    boolean result = false;
    if (s.length() < 1) {
      errors.add(WizardAPI.getProperty("error.empty"));
    }
    else if (!isUnique(s, currentEntries, currentProp, errors)) {
      errors.add("You've already entered that name somewhere else!");
    }
    else {
      result = true;
    }
    return result;
  }

  public String fix(String s, Collection currentEntries, String currentProp, Collection log) {
    String stem = new String(s);
    int postfix = 2;
    while (!isUnique(s, currentEntries, currentProp, null)) {
      s = stem + postfix++;
    }
    return s;
  }

  public boolean isUnique(String s, Collection currentEntries, String currentPropertyName, Collection errors) {
    boolean found = false;

    if ((currentEntries == null) || (!currentEntries.contains(s))) {
      if ((pc != null) && (uniqueProperties != null)) {
        for (Iterator i = uniqueProperties.iterator(); i.hasNext();) {
          String propName = (String) i.next();
          if (!propName.equals(currentPropertyName)) {
            Object value = pc.getProperty(propName);
            if (value != null) {
              if ((value instanceof String) && (s.equals(value))) {
                found = true;
                if (errors != null) {
                  errors.add("This property = " + currentPropertyName + ". That value is already in use (property = " + propName + ")");
                }
              }
              else if ((value instanceof Collection) && (((Collection) value).contains(s))) {
                found = true;
                if (errors != null) {
                  errors.add("This property = " + currentPropertyName + ". That value is already in use in list (property = " + propName + ")");
                }
              }
            }
          }
        }
      }
    }
    else {
      found = true;
      if (errors != null) {
        errors.add("Duplicate value found");
      }
    }

    return !found;
  }

  public boolean addUniqueProperty(String propertyName) {
    boolean result = true;

    if (pc != null) {
      if (uniqueProperties == null) {
        uniqueProperties = CollectionUtilities.createList(propertyName);
      }
      else if (!uniqueProperties.contains(propertyName)) {
        uniqueProperties.add(propertyName);
      }
      else {
        result = false;
      }
    }
    else {
      // @@TODO should this be an exception?
      result = false;
      System.err.println("unique properties can only be added if a source was given in the constructor");
    }

    return result;
  }
}
