package uk.ac.man.cs.mig.coode.protege.wizard.component;

import uk.ac.man.cs.mig.coode.protege.wizard.event.ValueChangeListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         13-Aug-2004
 */
public interface UserEditable {

  public boolean addValueChangeListener(ValueChangeListener valueChangeListener);

  public boolean removeValueChangeListener(ValueChangeListener valueChangeListener);
}
