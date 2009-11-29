package uk.ac.man.cs.mig.coode.protege.wizard.util;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Feb-2005
 */
public interface Console {

  public void out(String str);

  public void err(String str);

  public void warn(String str);

  public void clear();
}
