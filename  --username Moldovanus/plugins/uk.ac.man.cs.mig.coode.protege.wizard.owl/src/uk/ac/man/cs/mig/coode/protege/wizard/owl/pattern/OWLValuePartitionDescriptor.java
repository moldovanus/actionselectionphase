package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         02-Jun-2005
 */
public interface OWLValuePartitionDescriptor extends OWLPatternDescriptor{

  public boolean isPropFunctional();
  public String getPropName();
  public OWLNamedClass getPropDomain();

  public Object getValueNames();
}
