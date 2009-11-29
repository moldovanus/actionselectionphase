package uk.ac.man.cs.mig.coode.protege.wizard.page;

import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.WizardPropertyEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.event.ValueChangeListener;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class ArbitraryComponentPage extends AbstractWizardPage {

    private static final boolean debug = false;

    private JTextPane promptLabel = new JTextPane();

    private WizardPropertyEditor editor = null;

    private boolean valueHasChanged = false;

    private final ValueChangeListener vcListener = new ValueChangeListener() {
        public void valueChanged() {
            valueHasChanged = true;
            updateButtons();
        }

        public int compareTo(Object o) {
            if (this.equals(o))
                return 0;
            else
                return 1;
        }
    };

    /**
     * Can only be called from subclasses
     * if this call is used, the constructor must finish with a call to setComponent()
     *
     * @param pageName
     * @param propName
     */
    protected ArbitraryComponentPage(String pageName, String propName) {
        super(pageName, propName);
        promptLabel.setBackground(this.getBackground());
        promptLabel.setEditable(false);
        add(promptLabel, BorderLayout.NORTH);
    }

    /**
     * @param pageName
     * @param propName
     * @param editor
     */
    public ArbitraryComponentPage(String pageName, String propName, AbstractWizardEditor editor) {
        this(pageName, propName);
        setComponent(editor);
    }

    protected void setComponent(AbstractWizardEditor editor) {
        if (this.editor == null) {
//      if (editor.isSuitable(getPropValue())) {
            this.editor = editor;
            this.editor.setPropertyName(getPropName());
            add(this.editor.getComponent(), BorderLayout.CENTER);
//      }
//      else {
//        editor = new UglyComponent();
//        add(editor, BorderLayout.CENTER);
//      }
        }
        else {
            // @@TODO could be an Exception
            System.err.println("Cannot change the editor used on this page");
        }
    }

    public void pageSelected(WizardDialog wizard) {
        if (debug) System.out.println("ArbitraryComponentPage.pageSelected");
        super.pageSelected(wizard);

        // if another page is using the same component, make sure we can see it
        if (editor.getComponent().getParent() != this) {
            add(editor.getComponent(), BorderLayout.NORTH);
        }

        Object currentValue = getPropertiesSource().getProperty(getPropName());

        // a component can be used on more than one page, so reset the property name
        // and the value before we start using it
        editor.setPropertyName(getPropName());
        editor.setValue(currentValue);

        // track when edits are made by the user so we can disable the next button
        // when required
        valueHasChanged = false;
        editor.addValueChangeListener(vcListener);

        // make sure the prompt text is set
        promptLabel.setText(getPromptText());

        // the value might already be invalid, so update the buttons
        updateButtons();

        editor.giveFocus();
    }

    protected void pageLosingFocus() {
        if (debug) System.out.println("ArbitraryComponentPage.pageLosingFocus");
        // remove the listener in case we use this component on another page
        editor.removeValueChangeListener(vcListener);

        if (valueHasChanged) {
            if (debug) System.out.println("Value changed, setting prop");
            // save the property value back into the property source
            Object currentValue = editor.getValue();
            String propName = getPropName();
            if (debug) System.out.println("propName = " + propName);
            getPropertiesSource().setProperty(propName, currentValue);
        }
        else {
            if (debug) System.out.println("Value not changed");
        }
    }

    private final void updateButtons() {
        if (editor.currentValueIsValid()) {
            if (debug) System.out.println("VALID - SETTING NEXT TO ENABLED");
            getWizard().setNextButtonEnabled(true);
        }
        else {
            if (debug) System.out.println("INVALID - SETTING NEXT TO DISABLED");
            getWizard().setNextButtonEnabled(false);
        }
    }

    public synchronized void addKeyListener(KeyListener l) {
        editor.addKeyListener(l);
    }

    public synchronized void removeKeyListener(KeyListener l) {
        editor.removeKeyListener(l);
    }

    public void setEnabled(boolean b) {
        editor.setEnabled(b);
    }
}
