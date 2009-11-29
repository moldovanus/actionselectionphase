package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.MetaOntologyNotImportedException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLListsPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         07-Jun-2005
 */
public class MetaListsPatternDescriptor implements OWLListsPatternDescriptor {
    private OWLModel model;
    private String prefix;

    public MetaListsPatternDescriptor(OWLModel model) throws MetaOntologyNotImportedException {
        OwlKbHelper helper = OwlKbHelper.getHelper(model);
        URI importURI = null;
        try {
            importURI = new URI(WizardAPI.getProperty("meta.url"));
        }
        catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (!helper.isImported(importURI)) {
            throw new MetaOntologyNotImportedException();
        } else {
            this.model = model;
            this.prefix = WizardAPI.getProperty("meta.prefix");
        }
    }

    public OWLModel getModel() {
        return model;
    }

//    public OWLNamedClass getParentClass() {
//        String owlList = WizardAPI.getProperty("pattern.owllist.root");
//        return model.getOWLNamedClass(prefix + ":" + owlList);
//    }

    public OWLNamedClass getEmptyListClass() {
        String emptyList = WizardAPI.getProperty("pattern.owllist.empty");
        return model.getOWLNamedClass(prefix + ":" + emptyList);
    }

    public OWLProperty getContentsProperty() {
        String contents = WizardAPI.getProperty("pattern.owllist.contents");
        return model.getOWLProperty(prefix + ":" + contents);
    }

    public OWLObjectProperty getNextProperty() {
        String next = WizardAPI.getProperty("pattern.owllist.next");
        return model.getOWLObjectProperty(prefix + ":" + next);
    }

    public OWLObjectProperty getRestProperty() {
        String rest = WizardAPI.getProperty("pattern.owllist.rest");
        return model.getOWLObjectProperty(prefix + ":" + rest);
    }

    public OWLNamedClass getListClass() {
        String list = WizardAPI.getProperty("pattern.owllist.root");
        return model.getOWLNamedClass(prefix + ":" + list);
    }
};