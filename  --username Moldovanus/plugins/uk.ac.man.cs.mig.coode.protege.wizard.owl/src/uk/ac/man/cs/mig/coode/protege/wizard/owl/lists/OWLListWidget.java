package uk.ac.man.cs.mig.coode.protege.wizard.owl.lists;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.components.PropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyValuesWidget;

/**
 * Author: drummond<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jul 12, 2006<br><br>
 * <p/>
 * nick.drummond@cs.manchester.ac.uk<br>
 * www.cs.man.ac.uk/~drummond<br><br>
 */
public class OWLListWidget extends AbstractPropertyValuesWidget {

    protected PropertyValuesComponent createComponent(RDFProperty predicate) {
        return new OWLListComponent(predicate);
    }
}
