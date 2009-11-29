package uk.ac.man.cs.mig.coode.protege.wizard.page.impl;


/**
 * A demonstration implementation so that a developer can check their wizard is working
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         09-Nov-2004
 */
public class TestPage extends TextPage {

  private static String[] pageTypes = {"TextPage", "StringEntryPage",
                                       "ValidatedStringEntryPage",
                                       "OptionPage", "ListEntryPage", "ClassTreePage",
                                       "MultiSelectPage", "MatrixPage"};

  public TestPage() {
    super("test page", "Congratulations, your wizard is working correctly" +
                       "You can now try using one of the following pages: " +
                       printPageTypes());
  }

  private static String printPageTypes() {
    String result = "";
    for (int i = 0; i < pageTypes.length; i++) {
      result += "\n" + pageTypes[i];
    }
    return result;
  }
}
