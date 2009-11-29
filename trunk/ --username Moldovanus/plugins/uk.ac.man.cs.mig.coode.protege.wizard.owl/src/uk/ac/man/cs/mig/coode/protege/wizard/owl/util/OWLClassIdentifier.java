package uk.ac.man.cs.mig.coode.protege.wizard.owl.util;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import uk.ac.man.cs.mig.coode.protege.wizard.util.ClassIdentifier;
import uk.ac.man.cs.mig.coode.protege.wizard.util.ClassIdentifierTester;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Nov-2004
 */
public class OWLClassIdentifier implements ClassIdentifier {

  private static final boolean debug = false;

  public boolean isUserClass(Object cls) {
    if (debug) System.out.println("OWLClassIdentifier.isUserClass");
    boolean result = cls instanceof OWLNamedClass &&
                      (((OWLNamedClass)cls).isEditable());
    if (debug && result){
      ClassIdentifierTester.printClassAttributes(cls);
    }
    return result;
  }
}
