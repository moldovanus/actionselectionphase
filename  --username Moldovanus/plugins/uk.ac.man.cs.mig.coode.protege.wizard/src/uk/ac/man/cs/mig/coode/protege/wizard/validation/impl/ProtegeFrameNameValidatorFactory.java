package uk.ac.man.cs.mig.coode.protege.wizard.validation.impl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.ValidatorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Mar-2005
 */
public class ProtegeFrameNameValidatorFactory implements ValidatorFactory{

  public BasicNameValidator create(KnowledgeBase kb, PropertiesCollection pc) {
    return new ProtegeFrameNameValidator(kb, pc);
  }
}
