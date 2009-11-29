package uk.ac.man.cs.mig.coode.protege.wizard.util;

/**
@author Nick Drummond, Medical Informatics Group, University of Manchester
 *      17-Feb-2005
 */
public interface IdGenerator {

  public String getNextId();

  public String getPrefix();

  public int getPadding();
}
