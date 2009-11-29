package uk.ac.man.cs.mig.coode.protege.wizard.util;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         14-Jun-2005
 */
public class DefaultConsole implements Console{

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
