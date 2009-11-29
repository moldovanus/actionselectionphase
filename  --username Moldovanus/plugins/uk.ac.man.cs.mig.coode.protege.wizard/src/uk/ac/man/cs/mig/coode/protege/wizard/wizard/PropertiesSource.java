package uk.ac.man.cs.mig.coode.protege.wizard.wizard;

import java.beans.PropertyChangeListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Jun-2005
 */
public interface PropertiesSource {

  // need to be aware of cyclic event handling though
  String setProperty(String propName, Object value);

  String createProperty(String propName, Object value);

  Object getProperty(String propName);

  void clearAllProperties();

  /**
   * @param propName
   * @param l
   * @return
   * @@TODO should be a version that you don't have to specify a prop
   */
  boolean addPropertyChangeListener(String propName, PropertyChangeListener l);

  boolean removePropertyChangeListener(String propName, PropertyChangeListener l);

  void removeAllPropertyChangeListeners();
}
