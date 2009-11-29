package uk.ac.man.cs.mig.coode.protege.wizard.wizard;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

/**
 * Implementation of the properties store that is used by Wizards
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class PropertiesCollection implements PropertiesSource{

  private HashMap properties = new HashMap();

  private HashMap propertyListeners = new HashMap();

  // need to be aware of cyclic event handling though
  public final String setProperty(String propName, Object value) {
    if (Trace.propertyTrace) System.out.println("setting [" + propName + " to " + value + "]");

    String error = null;
    if (properties.containsKey(propName)) {
      // @@TODO typecheck
      // @@TODO check whether valid change (uniqueness etc)
      Object oldValue = properties.get(propName);
      properties.put(propName, value);
      if (propertyListeners.containsKey(propName)) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propName, oldValue, value);
        ((PropertyChangeListener) propertyListeners.get(propName)).propertyChange(evt);
      }
    }
    else {
      error = "Could not find a property called " + propName;
    }
    return error;
  }

  public final String createProperty(String propName, Object value) {
    String error = null;
    if (!properties.containsKey(propName)) {
      properties.put(propName, value);
    }
    else {
      error = "Property already exists " + propName;
    }
    return error;
  }

  public final Object getProperty(String propName) {
    Object value = properties.get(propName);
    if (Trace.propertyTrace) System.out.println("getting [" + propName + " = " + value + "]");
    return value;
  }

  public final void clearAllProperties() {
    if (Trace.propertyTrace) System.out.println("Clear all properties");
    properties.clear();
    propertyListeners.clear();
  }

  public final void printProperties() {
    System.out.println("properties = " + properties);
  }

  public final boolean addPropertyChangeListener(String propName, PropertyChangeListener l) {
    boolean result = false;
    if (properties.containsKey(propName)) {
      if (propertyListeners.containsKey(propName)) {
        System.err.println("YOU ALREADY HAVE A LISTENER ON PROPERTY " + propName);
      }
      else {
        propertyListeners.put(propName, l);
        result = true;
      }
    }
    return result;
  }

  public final boolean removePropertyChangeListener(String propName, PropertyChangeListener l) {
    boolean result = false;
    if (properties.containsKey(propName)) {
      if (propertyListeners.containsKey(propName)) {
        propertyListeners.remove(propName);
        result = true;
      }
    }
    return result;
  }

  public final void removeAllPropertyChangeListeners() {
    propertyListeners.clear();
  }
}
