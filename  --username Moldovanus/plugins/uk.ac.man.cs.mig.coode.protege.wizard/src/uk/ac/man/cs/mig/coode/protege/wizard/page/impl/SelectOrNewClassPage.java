package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import edu.stanford.smi.protege.model.Cls;
import uk.ac.man.cs.mig.coode.protege.wizard.component.text.ValidatedTextField;
import uk.ac.man.cs.mig.coode.protege.wizard.component.tree.ClassTreeComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.page.MultiComponentPage;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.WizardDialog;

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Jun-2005
 */
public class SelectOrNewClassPage extends MultiComponentPage {

  protected ValidatedTextField field;
  protected ClassTreeComponent tree;
  private String selectedProp;
  private String rootProp;
  private String newProp;
  private StringValidator v;

  private List listeners = new ArrayList();

  private boolean clearAllowed = false;

  public SelectOrNewClassPage(String pageName, String selectedProp, String rootProp, String newProp, StringValidator v) {
    super(pageName);
    this.selectedProp = selectedProp;
    this.rootProp = rootProp;
    this.newProp = newProp;
    this.v = v;
  }

  public void pageSelected(WizardDialog wizard) {

    if (tree == null) {
      tree = new ClassTreeComponent((Cls) getPropertiesSource().getProperty(rootProp));
      tree.setTitle(getProtegeWizard().getPageProperty(getName(), "tree.title"));
      addComponent(tree, selectedProp);
    }

    tree.setClearAllowed(clearAllowed);

    if (field == null) {
      field = new ValidatedTextField(v);
      field.setEmptyAllowed(true);
      field.setTitle(getProtegeWizard().getPageProperty(getName(), "text.title"));
      addComponent(field, newProp);
    }

    // add any listeners that have been "accumulated"
    if (listeners.size() > 0) {
      for (Iterator i = listeners.iterator(); i.hasNext();) {
        KeyListener l = (KeyListener) i.next();
        tree.addKeyListener(l);
        field.addKeyListener(l);
        i.remove();
      }
    }

    super.pageSelected(wizard);
  }

  protected void giveFocus() {
    tree.giveFocus();
  }

  public synchronized void addKeyListener(KeyListener l) {
    if (tree == null) {
      listeners.add(l);
    }
    else {
      tree.addKeyListener(l);
      field.addKeyListener(l);
    }
  }

  public synchronized void removeKeyListener(KeyListener l) {
    if (tree != null) {
      tree.removeKeyListener(l);
      field.removeKeyListener(l);
    }
    else {
      listeners.remove(l);
    }
  }

  public void setClearAllowed(boolean allowed) {
    clearAllowed = allowed;
  }
}
