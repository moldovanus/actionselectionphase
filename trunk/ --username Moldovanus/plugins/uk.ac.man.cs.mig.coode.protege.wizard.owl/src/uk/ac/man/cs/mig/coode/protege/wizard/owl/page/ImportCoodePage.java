package uk.ac.man.cs.mig.coode.protege.wizard.owl.page;

import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.FileField;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.page.AbstractWizardPage;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;

/**
 * This wizard page breaks the paradigm of not mucking around with the Protege
 * world until Finish is pressed. This is because it will become impossible to
 * perform the other steps without adding the appropriate classes and properties
 * to the knowledgebase.
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         23-May-2005
 */
public class ImportCoodePage extends AbstractWizardPage {

  public static final boolean debug = false;

  public String metaOntologyUrl;
  public String metaOntologyPrefix;
  private JenaOWLModel model;

  private JTextPane promptPane;
  private FileField importFileField;
  private Checkbox localCheckbox;
  private Checkbox doImportCheckbox;
  private JComponent mainPanel;

  public ImportCoodePage(String name, String propName, JenaOWLModel model) {
    super(name, propName);

    this.model = model;

    metaOntologyUrl = WizardAPI.getProperty("meta.url");
    metaOntologyPrefix = WizardAPI.getProperty("meta.prefix");
  }

  private JComponent createGUI() {

    promptPane = new JTextPane();
    promptPane.setBackground(this.getBackground());

    // if the user wishes to use a local version of the meta ontology
    localCheckbox = new Checkbox(WizardAPI.getProperty("ui.localmeta"));
    localCheckbox.setEnabled(true);
    localCheckbox.setState(true);
    localCheckbox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean state = localCheckbox.getState();
        if (debug) System.out.println("state = " + state);
        setFileFieldEnabled(state);
      }
    });

    // optional field for selecting a local file
    String dir = PluginUtilities.getPluginsDirectory().toString() + File.separator +
                 WizardAPI.getProperty("plugin.owlwizards.dir") + File.separator +
                 WizardAPI.getProperty("meta.filename");
    importFileField = new FileField(null, dir, ".owl", "");
    setFileFieldEnabled(localCheckbox.getState());

    // checkbox must be selected to allow the next button to be pressed
    doImportCheckbox = new Checkbox(WizardAPI.getProperty("ui.import"));
    doImportCheckbox.setEnabled(true);
    doImportCheckbox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean state = doImportCheckbox.getState();
        if (state) {
          try {
            OwlKbHelper helper = OwlKbHelper.getHelper(ImportCoodePage.this.model);
            File localPath = null;
            if (localCheckbox.getState()) {
              localPath = importFileField.getFilePath();
            }
            if (!helper.ensureOntologyImported(new URI(metaOntologyUrl),
                                               metaOntologyPrefix,
                                               localPath)) {
              state = false;
              doImportCheckbox.setState(false);
            }
            else {
              Collection metaResources =
                      helper.getResourcesFromNamespace(WizardAPI.getProperty("meta.url"),
                                                       RDFResource.class);
              helper.hideResources(metaResources);
              doImportCheckbox.setEnabled(false);
              localCheckbox.setEnabled(false);
              setFileFieldEnabled(false);
              getPropertiesSource().setProperty(getPropName(), Boolean.TRUE);
            }
          }
          catch (URISyntaxException e1) {
            WizardAPI.getWizardManager().handleErrors(e1);
          }
        }
        getPropertiesSource().setProperty(getPropName(), new Boolean(state));
        getWizard().setNextButtonEnabled(state);
      }
    });

    JPanel localPanel = new JPanel(new BorderLayout(6, 6));
    localPanel.add(localCheckbox, BorderLayout.NORTH);
    localPanel.add(importFileField, BorderLayout.CENTER);

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(promptPane);
    panel.add(localPanel);
    panel.add(doImportCheckbox);

    return panel;
  }

  public void pageSelected(WizardDialog wizard) {
    super.pageSelected(wizard);

    if (mainPanel == null) {
      mainPanel = createGUI();
      add(mainPanel, BorderLayout.NORTH);
    }

    promptPane.setText(getPromptText());

    getWizard().setNextButtonEnabled(doImportCheckbox.getState());
  }

  /**
   * Needed because setEnabled does not work on the FileField
   *
   * @param state
   */
  private void setFileFieldEnabled(boolean state) {
    LabeledComponent c = (LabeledComponent) importFileField.getComponent(0);
    c.getCenterComponent().setEnabled(state);
    for (Iterator i = c.getHeaderButtons().iterator(); i.hasNext();) {
      ((JButton) i.next()).setEnabled(state);
    }
  }

}
