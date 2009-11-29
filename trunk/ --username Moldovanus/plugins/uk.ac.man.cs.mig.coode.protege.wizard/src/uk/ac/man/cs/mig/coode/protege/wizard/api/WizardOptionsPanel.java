package uk.ac.man.cs.mig.coode.protege.wizard.api;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.AbstractValidatableComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.util.GuiUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         19-Oct-2005
 */
public class WizardOptionsPanel extends AbstractValidatableComponent {

    private WizardOptions options;

    private JComponent[] components;


    public WizardOptionsPanel(Project project) {
        super();

        this.options = WizardAPI.getWizardManager().getUserOptions();

        createPanelUsingSpringLayout();

        refreshOptions();
    }

    private void refreshOptions() {
        for (int i = 0; i < WizardOptions.NUM_PROPS; i++) {
            Class type = options.getType(i);
            if (type == boolean.class) {
                JCheckBox c = (JCheckBox) components[i];
                c.setSelected(options.getBoolean(i));
            } else if (type == String.class) {
                JTextField t = (JTextField) components[i];
                t.setText(options.getProperty(i));
            } else if (type == int.class) {
                JSpinner s = (JSpinner) components[i];
                s.setValue(options.getInteger(i));
            }
        }
    }

    private void createPanelUsingSpringLayout() {

        JComponent panel = new JPanel(new SpringLayout());

//        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        components = new JComponent[WizardOptions.NUM_PROPS];

        for (int i = 0; i < WizardOptions.NUM_PROPS; i++) {
            Class type = options.getType(i);
            if (type == boolean.class) {
                components[i] = new JCheckBox();
            } else if (type == String.class) {
                components[i] = new JTextField();
            } else if (type == int.class) {
                components[i] = new JSpinner();
                components[i].setPreferredSize(new Dimension(50, components[i].getHeight()));
            }

            JLabel label = new JLabel(options.getPropertyName(i));
            label.setLabelFor(components[i]);

            panel.add(components[i]);
            panel.add(label);
        }

        GuiUtils.makeCompactGrid(panel, WizardOptions.NUM_PROPS, 2, 0, 0, 6, 6);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(panel);
    }

    public void saveContents() {
        for (int i = 0; i < WizardOptions.NUM_PROPS; i++) {
            Class type = options.getType(i);
            if (type == boolean.class) {
                JCheckBox c = (JCheckBox) components[i];
                options.setBoolean(i, c.isSelected());
            } else if (type == int.class) {
                JSpinner s = (JSpinner) components[i];
                options.setInteger(i, (Integer) s.getValue());
            } else if (type == String.class) {
                JTextField t = (JTextField) components[i];
                options.setProperty(i, t.getText());
            } else {
                System.err.println("WIZARD OPTIONS: Cannot find type of property");
            }
        }
    }

    public boolean validateContents() {
        return true;
    }
}
