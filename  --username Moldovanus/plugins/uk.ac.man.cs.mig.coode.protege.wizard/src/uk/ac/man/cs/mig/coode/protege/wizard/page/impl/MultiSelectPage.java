package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import edu.stanford.smi.protege.model.Frame;
import uk.ac.man.cs.mig.coode.protege.wizard.component.multiselect.MultiSelectComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Oct-2004
 */
public class MultiSelectPage extends ArbitraryComponentPage {

  //@@TODO need to get the shortcut text back in
  private static String shortcutText = "\n(shortcut = CTRL+ENTER to finish)";

  private MultiSelectComponent theComponent;

  public MultiSelectPage(String pageName, String propName, Collection roots) {
    super(pageName, propName);
    theComponent = new MultiSelectComponent(roots, false);
    setComponent(theComponent);
  }

  public MultiSelectPage(String pageName, String propName, Frame root) {
    super(pageName, propName);
    theComponent = new MultiSelectComponent(root, false);
    setComponent(theComponent);
  }

  public void setDuplicateValuesAllowed(boolean value) {
    theComponent.setDuplicateValuesAllowed(value);
  }
}
