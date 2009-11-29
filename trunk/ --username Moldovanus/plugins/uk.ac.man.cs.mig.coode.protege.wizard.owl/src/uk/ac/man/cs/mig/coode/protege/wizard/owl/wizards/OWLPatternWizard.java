package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.page.ImportCoodePage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.SelectOrNewClassPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ValidatedStringEntryPage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Jun-2005
 */
public abstract class OWLPatternWizard extends AbstractOWLWizard {

    private static final String importProp = "importProp";
    private static final String nameProp = "nameProp";
    private static final String superclassProp = "superPropertyProp";
    private static final String superSuperProp = "superSuperProp";
    protected static final String owlThingProp = "owlThingProp";

    private URI metaURI;

    private OWLNamedClass defaultParent;

    public String getMenuString() {
        return "Tools->Patterns";
    }

    protected void initialise() throws NullPointerException, WizardInitException {

        if (isMetaActive()) {
            try {
                this.metaURI = new URI(WizardAPI.getProperty("meta.url"));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }

            addProperty(importProp, Boolean.FALSE);

            if (!getOwlHelper().isImported(metaURI)) {
                addPage(new ImportCoodePage("import",
                        importProp,
                        (JenaOWLModel) getModel()));
            }
        }

        addUniqueProperty(nameProp, getDefaultName());
        addUniqueProperty(superclassProp, null);
        addProperty(superSuperProp, getDefaultParent());
        addProperty(owlThingProp, getModel().getOWLThingClass());

        addPage(new ValidatedStringEntryPage("patternname", nameProp, getDefaultValidator()));

        addPropertyChangeListener(nameProp, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                patternNameChanged((String) evt.getNewValue());
            }
        });

        SelectOrNewClassPage parentPage = new SelectOrNewClassPage("patternsuper",
                superSuperProp,
                owlThingProp,
                superclassProp,
                getDefaultValidator());
        parentPage.setClearAllowed(false);
        addPage(parentPage);
    }

    protected abstract void patternNameChanged(String newName);

    protected abstract String getDefaultName();

    protected final String getPatternName() {
        return (String) getProperty(nameProp);
    }

    protected final OWLNamedClass getPatternParent() {
        String parentClassName = (String) getProperty(superclassProp);

        OWLNamedClass indirectParentClass = (OWLNamedClass) getProperty(superSuperProp);
        OWLNamedClass directParentClass = null;

        if ((parentClassName == null) || (parentClassName.equals(""))) {
            directParentClass = indirectParentClass;
        } else {
            //TODO below will not work when an alternative browser slot is used.
            directParentClass = getModel().getOWLNamedClass(parentClassName);
            if (directParentClass == null) {
                directParentClass = (OWLNamedClass) getOwlHelper().createClass(parentClassName, indirectParentClass);
            }
        }

        setDefaultParent(directParentClass);

        return directParentClass;
    }

    protected final OWLNamedClass getDefaultParent() {
        if (defaultParent == null) {
            defaultParent = getModel().getOWLThingClass();
        }
        return defaultParent;
    }

    protected final void setDefaultParent(OWLNamedClass parent) {
        defaultParent = parent;
    }

    protected boolean isMetaActive() {
        return WizardAPI.getMetaActive();
    }
}
