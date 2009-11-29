package uk.ac.man.cs.mig.coode.protege.wizard.util;

import edu.stanford.smi.protege.model.Cls;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Nov-2004
 */
public class StandardProtegeClassIdentifier implements ClassIdentifier{

  public boolean isUserClass(Object cls) {
    return (cls instanceof Cls) &&
           (((Cls)cls).isEditable()) &&
          !(((Cls)cls).isAbstract());
  }
}
