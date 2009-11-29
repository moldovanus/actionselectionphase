package uk.ac.man.cs.mig.coode.protege.wizard.component.listentry;

import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.text.ValidatedTextFieldCombo;
import uk.ac.man.cs.mig.coode.protege.wizard.event.ValueChangeListener;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         14-Feb-2005
 */
public class TreeListEntryComponent extends BasicListEntryComponent {

  private static final int NUM_FEATURES = 4; // temp - got rid of remove dupls

  public static final int FEATURE_NONE = 0;
  public static final int FEATURE_PREFIX = 1;
  public static final int FEATURE_POSTFIX = 2;
  public static final int FEATURE_ALLOW_TREE = 4;
  public static final int FEATURE_TIDY = 8;
  public static final int FEATURE_ALL = FEATURE_PREFIX | FEATURE_POSTFIX | FEATURE_ALLOW_TREE | FEATURE_TIDY;

  private int featuresActivated = FEATURE_NONE;

  private JCheckBox allowTreeCheckbox;
  private JButton tidyButton;
  private ValidatedTextFieldCombo prefixComponent;

  private JTextField postfixField;

  private boolean allowTree = false;
//  private DefaultComboBoxModel prefixModel;

  public TreeListEntryComponent(StringValidator validator, int features) {
    super(validator);
    if (features != FEATURE_NONE) {
      featuresActivated = features;
      JComponent featurePanel = createOptionsPanel();
      mainPanel.add(featurePanel, BorderLayout.NORTH);
    }
  }

  protected UniqueEntryListEditor createEditor(StringValidator validator, Console errorsConsole) {
    return new UniqueTreeEntryEditor(validator, errorsConsole);
  }

  public Object getValue() {
    if (allowTree) {
      return theEditor.getValue(); // return the tree root
    }
    else {
      return theEditor.getValidEntries(); // return a plain list
    }
  }

  protected JComponent createOptionsPanel() {
    JPanel optionsPanel = new JPanel(new GridLayout(NUM_FEATURES, 1, 6, 6));

    optionsPanel.setBorder(BorderFactory.createEtchedBorder());

    if ((featuresActivated & FEATURE_PREFIX) != 0) {
      optionsPanel.add(createPrefixPanel());
    }

    if ((featuresActivated & FEATURE_POSTFIX) != 0) {
      optionsPanel.add(createPostfixPanel());
    }

    if ((featuresActivated & FEATURE_ALLOW_TREE) != 0) {
      allowTree = true;
      optionsPanel.add(createIndentOKPanel());
    }

    if ((featuresActivated & FEATURE_TIDY) != 0) {
      optionsPanel.add(createTidyPanel());
    }

    return optionsPanel;
  }

  private JComponent createTidyPanel() {
    JPanel fixPanel = new JPanel(new GridLayout(1, 2));
    String tidyText = WizardAPI.getProperty("ui.tidy");
    tidyButton = new JButton(new AbstractAction(tidyText) {
      public void actionPerformed(ActionEvent e) {
        if (prefixComponent != null){
          prefixComponent.fix();
          theEditor.setPrefix((String)prefixComponent.getValue());
        }
        theEditor.fix();
      }
    });

    fixPanel.add(new JLabel(tidyText));
    fixPanel.add(tidyButton);
    fixPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    return fixPanel;
  }

  private JComponent createIndentOKPanel() {
    allowTreeCheckbox = new JCheckBox(WizardAPI.getProperty("ui.tabindent"), allowTree);
    allowTreeCheckbox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        allowTree = allowTreeCheckbox.isSelected();
        notifyValueChanged();
      }
    });
    allowTreeCheckbox.setMargin(new Insets(6, 6, 6, 6));
    return allowTreeCheckbox;
  }

  protected JComponent createPrefixPanel() {
    JPanel prependPanel = new JPanel(new GridLayout(1, 2));
    prefixComponent = new ValidatedTextFieldCombo(getValidator());
    prefixComponent.addValueChangeListener(new ValueChangeListener(){
      public void valueChanged() {
        theEditor.setPrefix((String)prefixComponent.getValue());
        notifyValueChanged();
      }
      public int compareTo(Object o) {
        return 0;
      }
    });
    prependPanel.add(new JLabel(WizardAPI.getProperty("ui.setprefix")));
    prependPanel.add(prefixComponent.getComponent());
    prependPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    return prependPanel;
  }

  protected JComponent createPostfixPanel() {
    JPanel appendPanel = new JPanel(new GridLayout(1, 2));
    postfixField = new JTextField();
    postfixField.addCaretListener(new CaretListener() {
      public void caretUpdate(CaretEvent e) {
        theEditor.setPostfix(postfixField.getText());
        notifyValueChanged();
      }
    });
    appendPanel.add(new JLabel(WizardAPI.getProperty("ui.setsuffix")));
    appendPanel.add(postfixField);
    appendPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    return appendPanel;
  }

  public void setPrefixes(Collection prefixes) {
    if (prefixComponent != null){
      prefixComponent.setValues(prefixes);
    }
  }

  public void setPrefix(String newText) {
    if (prefixComponent != null){
      prefixComponent.setValue(newText);
      theEditor.setPrefix(newText);
      notifyValueChanged();
    }
  }
}
