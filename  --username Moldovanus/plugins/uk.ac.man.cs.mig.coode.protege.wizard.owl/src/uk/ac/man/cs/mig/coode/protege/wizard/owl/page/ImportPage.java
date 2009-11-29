package uk.ac.man.cs.mig.coode.protege.wizard.owl.page;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import uk.ac.man.cs.mig.coode.protege.wizard.page.AbstractWizardPage;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         23-May-2005
 */
public class ImportPage extends AbstractWizardPage {

  public static final boolean debug = false;

//  private MyImportPanel panel;
  private JenaOWLModel model;

  public ImportPage(String pageName, String propName, JenaOWLModel model) {
    super(pageName, propName);
    this.model = model;
  }

//  public void pageSelected(WizardDialog wizard) {
//    super.pageSelected(wizard);
//
//    panel = new MyImportPanel();
//    add(panel);
//
//  }
//
//  public void nextButtonPressed(WizardDialog wizard) {
//    super.nextButtonPressed(wizard);
//    if (panel != null) {
//      panel.ensureActionPerformed();
//    }
//  }
//
//  class MyImportPanel extends AddImportPanel {
//
//    private boolean done = false;
//
//    public MyImportPanel() {
//      super(model);
//    }
//
//    public void ensureActionPerformed() {
//      if (!done) {
//        performAction();
//        Iterator tstores = model.getTripleStoreModel().getTripleStores().iterator();
//        while (tstores.hasNext()) {
//          TripleStore current = (TripleStore) tstores.next();
//          if (debug) System.out.println("current = " + current);
//        }
//        done = true;
//      }
//    }
//  };
}
