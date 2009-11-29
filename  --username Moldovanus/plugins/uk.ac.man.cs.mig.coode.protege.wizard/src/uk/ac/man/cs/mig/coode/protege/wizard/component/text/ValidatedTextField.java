package uk.ac.man.cs.mig.coode.protege.wizard.component.text;

import edu.stanford.smi.protege.util.CollectionUtilities;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.AbstractWizardEditor;
import uk.ac.man.cs.mig.coode.protege.wizard.component.Fixable;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class ValidatedTextField extends AbstractWizardEditor
        implements ComboBoxEditor, Fixable {

    private JTextField textBox;
    private JLabel errorBox;
    private JPanel thePanel;
    private StringValidator validator;
    private Color normalColour;
    private JPanel mainPanel;
    private java.util.List errors;
    private boolean valid;
    private boolean emptyAllowed = false;
    private JLabel titleText;
    private String title = null;

    public ValidatedTextField(StringValidator v) {
        super();

        thePanel = new JPanel(new GridLayout(0, 1));

        validator = v;

        // create the components
        titleText = new JLabel(title);
        textBox = createTextBox();
        errorBox = createErrorBox();

        // get the standard colour now for removing the highlight
        normalColour = textBox.getForeground();

        // add the components to the panel
        if (title != null) {
            thePanel.add(titleText);
        }
        thePanel.add(textBox);
        thePanel.add(errorBox);

        mainPanel = new JPanel(new BorderLayout(6, 6));
        mainPanel.add(thePanel, BorderLayout.NORTH);
    }

    public void setEmptyAllowed(boolean emptyAllowed) {
        this.emptyAllowed = emptyAllowed;
    }

    public void setTitle(String title) {
        this.title = title;
        if (titleText != null) {
            titleText.setText(title);
            if (thePanel.getComponents()[0] != titleText) {
                thePanel.add(titleText, 0);
            }
        }
    }

    public void giveFocus() {
        textBox.grabFocus();
    }

    public void setEnabled(boolean enabled) {
        textBox.setEnabled(enabled);
    }

    private JLabel createErrorBox() {
        JLabel err = new JLabel();
        err.setBorder(BorderFactory.createEtchedBorder());
        return err;
    }

    private JTextField createTextBox() {
        JTextField field = new JTextField();

        field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }

            public void insertUpdate(DocumentEvent e) {
                validateContents();
                notifyValueChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                validateContents();
                notifyValueChanged();
            }
        });
        return field;
    }

    public synchronized void addKeyListener(KeyListener l) {
        textBox.addKeyListener(l);
    }

    public synchronized void removeKeyListener(KeyListener l) {
        textBox.removeKeyListener(l);
    }

    private void validateContents() {
        if (validator != null) {
            errors = new ArrayList();
            valid = validator.isValid(textBox.getText(), null, propertyName, errors);
            if (!valid) {
                errorBox.setText((String) CollectionUtilities.getFirstItem(errors));
                textBox.setForeground(Color.RED);
            }
            else {
                errorBox.setText("");
                textBox.setForeground(normalColour);
            }
        }
    }

    public void setValidator(StringValidator v) {
        validator = v;
    }

    public boolean setValue(Object newValue) {
        if (newValue != null) {
            textBox.setText(newValue.toString());
        }
        else {
            textBox.setText(null);
        }
        validateContents();
        return true;
    }

    public Object getValue() {
        return textBox.getText();
    }

    public boolean isSuitable(Object value) {
        return true; // as we can toString() anything
    }

    public boolean currentValueIsValid() {
        return valid || (emptyAllowed && ((errors == null) || errors.get(0).equals(WizardAPI.getProperty("error.empty"))));
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    //////////////////////////// below are all for implementing ComboBoxEditor

    public void selectAll() {
        textBox.selectAll();
    }

    public Component getEditorComponent() {
        return textBox;
    }

    public void addActionListener(ActionListener l) {
        textBox.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        textBox.addActionListener(l);
    }

    public Object getItem() {
        return textBox.getText();
    }

    public void setItem(Object anObject) {
        textBox.setText((String) anObject);
    }

    public void fix() {
        if (validator != null) {
            textBox.setText(validator.fix(textBox.getText(), null, propertyName, null));
        }
    }
}
