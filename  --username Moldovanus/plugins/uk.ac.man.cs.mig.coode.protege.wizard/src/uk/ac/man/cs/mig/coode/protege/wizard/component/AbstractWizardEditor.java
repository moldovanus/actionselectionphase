package uk.ac.man.cs.mig.coode.protege.wizard.component;

import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.event.ValueChangeListener;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Trace;

import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         27-Jul-2004
 */
public abstract class AbstractWizardEditor
        implements WizardPropertyEditor {

    private static final boolean debug = Trace.Component.general;

    protected String propertyName;

    private Set valueChangeListeners = new TreeSet();

//  private AbstractList keyListeners = new ArrayList();

    public abstract boolean isSuitable(Object value);

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean currentValueIsValid() {
        return true;
    }

    public synchronized void addKeyListener(KeyListener l) {
        getComponent().addKeyListener(l);
    }

    public synchronized void removeKeyListener(KeyListener l) {
        getComponent().removeKeyListener(l);
    }

    public boolean addValueChangeListener(ValueChangeListener valueChangeListener) {
        return valueChangeListeners.add(valueChangeListener);
    }

    public boolean removeValueChangeListener(ValueChangeListener valueChangeListener) {
        return valueChangeListeners.remove(valueChangeListener);
    }

    protected final void notifyValueChanged() {
        if (debug) System.out.println(getClass() + " notifyValueChanged");
        Iterator i = valueChangeListeners.iterator();
        while (i.hasNext()) {
            ((ValueChangeListener) i.next()).valueChanged();
        }
    }

    public void giveFocus() {
    }

    public void setEnabled(boolean enabled) {
    }

    protected KbHelper getHelper() {
        return KbHelper.getHelper(WizardAPI.getWizardManager().getKnowledgeBase());
    }
}
