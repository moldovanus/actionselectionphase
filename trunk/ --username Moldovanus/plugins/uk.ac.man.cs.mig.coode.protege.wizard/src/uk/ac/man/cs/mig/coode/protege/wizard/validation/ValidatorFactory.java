package uk.ac.man.cs.mig.coode.protege.wizard.validation;

import edu.stanford.smi.protege.model.KnowledgeBase;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.BasicNameValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Mar-2005
 */
public interface ValidatorFactory {

  public BasicNameValidator create(KnowledgeBase kb, PropertiesCollection pc);
}
