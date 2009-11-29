package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.component.options.OptionsComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;

import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         26-Jul-2004
 */
public class OptionsPage extends ArbitraryComponentPage {

  /**
   * Page containing several radio buttons for selecting between options
   * returns the string of the selected option
   * (So make sure you don't use duplicate strings for different options)
   *
   * @param pageName   the name of the page
   * @param propName   the name of the wizard property editable by this page
   */
  public OptionsPage(String pageName, String propName, List options) {
    super(pageName, propName);

    OptionsComponent c = new OptionsComponent(options);

    setComponent(c);
  }
}
