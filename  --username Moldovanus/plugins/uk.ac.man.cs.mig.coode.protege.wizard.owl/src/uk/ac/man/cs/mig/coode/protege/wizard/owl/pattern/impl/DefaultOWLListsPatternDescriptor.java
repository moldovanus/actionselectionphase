package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl;

import edu.stanford.smi.protegex.owl.model.*;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLListsPatternDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Mar-2006
 */
public class DefaultOWLListsPatternDescriptor implements OWLListsPatternDescriptor {

    OWLNamedClass owlList;
    OWLNamedClass emptyList;
    OWLObjectProperty hasListProperty;
    OWLObjectProperty isFollowedBy;
    OWLObjectProperty hasContents;
    OWLObjectProperty hasNext;

    private OWLModel owlModel;

    private static Map<OWLModel, OWLListsPatternDescriptor> descriptors;

    public static OWLListsPatternDescriptor getDescriptor(OWLModel owlModel) {
        OWLListsPatternDescriptor descr = null;
        if (descriptors == null) {
            WizardAPI.getProperty("pattern.owllist.root");
            descriptors = new HashMap();
        }
        else {
            descr = descriptors.get(owlModel);
        }

        if (descr == null) {
            descr = new DefaultOWLListsPatternDescriptor(owlModel);
            descriptors.put(owlModel, descr);
        }
        return descr;
    }

    private DefaultOWLListsPatternDescriptor(OWLModel owlModel) {

        String owlListName = WizardAPI.getProperty("pattern.owllist.root");
        String emptyListName = WizardAPI.getProperty("pattern.owllist.empty");
        String listpropName = WizardAPI.getProperty("pattern.owllist.listprop");
        String restName = WizardAPI.getProperty("pattern.owllist.rest");
        String contentsName = WizardAPI.getProperty("pattern.owllist.contents");
        String nextName = WizardAPI.getProperty("pattern.owllist.next");

        this.owlModel = owlModel;

        if (owlModel.getOWLNamedClass(owlListName) == null) {
            hasListProperty = owlModel.createOWLObjectProperty(listpropName);
            isFollowedBy = (OWLObjectProperty) owlModel.createSubproperty(restName, hasListProperty);
            hasContents = (OWLObjectProperty) owlModel.createSubproperty(contentsName, hasListProperty);
            hasNext = (OWLObjectProperty) owlModel.createSubproperty(nextName, isFollowedBy);

            owlList = owlModel.createOWLNamedClass(owlListName);
            OWLAllValuesFrom restr = owlModel.createOWLAllValuesFrom(hasNext, owlList);
            owlList.addSuperclass(restr);

            emptyList = owlModel.createOWLNamedClass(emptyListName);
            Collection intersection = new ArrayList();
            intersection.add(owlList);
            OWLNamedClass owlThing = owlModel.getOWLThingClass();
            intersection.add(owlModel.createOWLComplementClass(owlModel.createOWLSomeValuesFrom(hasContents, owlThing)));
            emptyList.addEquivalentClass(owlModel.createOWLIntersectionClass(intersection));
            intersection.clear();
            intersection.add(owlList);
            intersection.add(owlModel.createOWLComplementClass(owlModel.createOWLSomeValuesFrom(isFollowedBy, owlThing)));
            emptyList.addEquivalentClass(owlModel.createOWLIntersectionClass(intersection));
            emptyList.removeSuperclass(owlModel.getOWLThingClass());

            // properties setup
            hasListProperty.setDomain(owlList);
            isFollowedBy.setRange(owlList);
            isFollowedBy.setTransitive(true);
            hasNext.setFunctional(true);
            hasContents.setFunctional(true);
        }
        else {
            owlList = owlModel.getOWLNamedClass(owlListName);
            emptyList = owlModel.getOWLNamedClass(emptyListName);
            hasListProperty = owlModel.getOWLObjectProperty(listpropName);
            isFollowedBy = owlModel.getOWLObjectProperty(restName);
            hasContents = owlModel.getOWLObjectProperty(contentsName);
            hasNext = owlModel.getOWLObjectProperty(nextName);
        }
    }

    public OWLNamedClass getEmptyListClass() {
        return emptyList;
    }

    public OWLProperty getContentsProperty() {
        return hasContents;
    }

    public OWLObjectProperty getNextProperty() {
        return hasNext;
    }

    public OWLObjectProperty getRestProperty() {
        return isFollowedBy;
    }

    public OWLModel getModel() {
        return owlModel;
    }

    public OWLNamedClass getListClass() {
        return owlList;
    }
}
