package uk.ac.man.cs.mig.coode.protege.wizard.component.console;

import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         08-Dec-2004
 */
public class StandardOut implements Console {

  public void out(String str) {
    System.out.println(str);
  }

  public void err(String str) {
    System.err.println("ERROR -> " + str);
  }

  public void warn(String str) {
    System.err.println("WARNING -> " + str);
  }

  public void clear() {
    System.out.println();
    System.out.println();
    System.out.println("-----------------------------------------------------------");
    System.out.println();
    System.out.println();
  }
}
