package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         19-May-2005
 */
public interface OWLPattern {

//  public void setParent(OWLNamedClass parent);

//  public void setName(String name);

  public void setBaseClass(OWLNamedClass base);

  public OWLNamedClass getBaseClass();

  public void removeFromModel();

  public OWLProperty getIsPartOfPatternAnnotProp();
  public OWLProperty getPatternPartsAnnotProp();

  /**
   * Useful for navigating or deleting a pattern
   * @return a collection of RDFResources
   */
  public Collection getPatternParts();
}
