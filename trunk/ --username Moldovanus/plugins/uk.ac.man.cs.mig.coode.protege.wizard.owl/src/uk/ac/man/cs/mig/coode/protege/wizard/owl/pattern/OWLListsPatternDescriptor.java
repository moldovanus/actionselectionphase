package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         02-Jun-2005
 */
public interface OWLListsPatternDescriptor extends OWLPatternDescriptor {

    public OWLNamedClass getEmptyListClass();

    public OWLProperty getContentsProperty();

    public OWLObjectProperty getNextProperty();

    public OWLObjectProperty getRestProperty();

    public OWLNamedClass getListClass();
}
