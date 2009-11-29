package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.validation.NamespacePrefixValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ValidatedStringEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.URLValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractBulkResourceWizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         15-Oct-2004
 */
public class MoveToNewNamespaceWizard extends AbstractBulkResourceWizard {

    private static final String nsProp = "nsProp";
    private static final String nsURLProp = "nsURLProp";
    private ValidatedStringEntryPage uriPage;

    protected void initialise() throws NullPointerException, WizardInitException {
        super.initialise();

        addProperty(nsProp, null);
        addProperty(nsURLProp, null);

        addPropertyChangeListener(nsProp, new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

                OWLModel okb = (OWLModel) WizardAPI.getWizardManager().getKnowledgeBase();
                NamespaceManager nsm = okb.getNamespaceManager();
                Collection currentPrefixes = nsm.getPrefixes();
                String newValue = (String) propertyChangeEvent.getNewValue();
                if (!currentPrefixes.contains(newValue)) {
                    setProperty(nsURLProp, "");
                    uriPage.setEnabled(true);
                }
                else {
                    setProperty(nsURLProp, nsm.getNamespaceForPrefix(newValue));
                    uriPage.setEnabled(false);
                }
            }
        });

        NamespaceManager nsm = ((OWLModel) getKnowledgeBase()).getNamespaceManager();
        Collection namespacePrefixes = nsm.getPrefixes();

        addPage(new ValidatedStringEntryPage("2", nsProp, new NamespacePrefixValidator(), namespacePrefixes));

        uriPage = new ValidatedStringEntryPage("3", nsURLProp, new URLValidator());
        addPage(uriPage);
    }

    protected void getReadyToHandleResourceOperations(Console output) {
        String nsURL = (String) getProperty(nsURLProp);
        String nsName = (String) getProperty(nsProp);
        OWLModel okb = (OWLModel) getKnowledgeBase();
        NamespaceManager nsm = okb.getNamespaceManager();
        Collection currentPrefixes = nsm.getPrefixes();
        if (!currentPrefixes.contains(nsName)) {
            output.out("Creating new namespace: " + nsName + " for URL: " + nsURL + "#");
            nsm.setPrefix(nsURL + "#", nsName);
        }
    }

    protected boolean handleResourceOperation(Frame resource, Console output) {
        String nsName = (String) getProperty(nsProp);
        String previousName = resource.getName();
        String existingNamespace = ((RDFResource) resource).getNamespacePrefix();
        if (existingNamespace != null) {
            output.warn("moving entity " + resource.getBrowserText() + "from " + existingNamespace);
            String[] nameParts = previousName.split(":");
            previousName = nameParts[nameParts.length - 1];
        }
        if (nsName != null) {
            output.out("moving entity " + resource.getBrowserText() + " to " + nsName);
            resource.rename(nsName + ":" + previousName);
        }
        else {
            output.out("moving entity " + resource.getBrowserText() + " to default namespace");
            resource.rename(previousName);
        }
        return true;
    }
}