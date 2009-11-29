package uk.ac.man.cs.mig.coode.protege.wizard.component;

import javax.swing.*;
import java.awt.event.KeyListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         27-Jul-2004
 */
public interface WizardPropertyEditor extends UserEditable {

    /**
     * Placeholder to check if the component can handle this type of value
     * This almost certainly could check an internally maintained list of acceptable
     * value types
     * Future expansion, could hava factory to get a suitable container for a certain
     * value type
     *
     * @param value
     * @return true if the component can handle this type of value
     */
    public boolean isSuitable(Object value);

    public boolean setValue(Object newValue);

    public Object getValue();

    public void setPropertyName(String propertyName);

    public boolean currentValueIsValid();

    public void giveFocus();

    public void setEnabled(boolean enabled);

    public JComponent getComponent();

    void addKeyListener(KeyListener l);

    void removeKeyListener(KeyListener l);
}
