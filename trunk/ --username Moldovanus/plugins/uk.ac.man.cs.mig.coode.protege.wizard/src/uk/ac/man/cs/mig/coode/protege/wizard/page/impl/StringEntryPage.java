package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.component.text.PlainTextField;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class StringEntryPage extends ArbitraryComponentPage {

  public StringEntryPage(String pageName, String propName, int numRows) {
    super(pageName, propName);
    setComponent(new PlainTextField(numRows));
  }
}
