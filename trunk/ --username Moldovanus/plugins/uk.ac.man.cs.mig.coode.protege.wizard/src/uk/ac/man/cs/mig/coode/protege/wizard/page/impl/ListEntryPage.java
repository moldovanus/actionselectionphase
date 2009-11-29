package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.component.listentry.TreeListEntryComponent;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ArbitraryComponentPage;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         12-Aug-2004
 */
public class ListEntryPage extends ArbitraryComponentPage {

  protected TreeListEntryComponent theComponent;

  public ListEntryPage(String pageName, String propName, StringValidator v,
                       boolean allowTreeEntry, Collection prefixes) {
    super(pageName, propName);

    int features = TreeListEntryComponent.FEATURE_ALL;
    if (!allowTreeEntry) {
      features &= ~TreeListEntryComponent.FEATURE_ALLOW_TREE;
    }

    theComponent = new TreeListEntryComponent(v, features);

    if (prefixes != null){
      theComponent.setPrefixes(prefixes);
    }

    setComponent(theComponent);
  }

  public ListEntryPage(String pageName, String propName, StringValidator v,
                       boolean allowTreeEntry){
    this(pageName, propName, v, allowTreeEntry, null);
  }

  public void setPrefixText(String newText) {
    theComponent.setPrefix(newText);
  }

  public void setPrefixes(Collection prefixes){
    theComponent.setPrefixes(prefixes);
  }
}
