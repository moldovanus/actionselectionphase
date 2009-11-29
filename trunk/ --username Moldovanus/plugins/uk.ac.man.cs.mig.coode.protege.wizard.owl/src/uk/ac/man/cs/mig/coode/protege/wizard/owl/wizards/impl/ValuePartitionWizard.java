package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLValuePartitionDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.OWLValuePartition;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.validation.ResourceNSValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.OWLPatternWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ClassTreePage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ListEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.OptionPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ValidatedStringEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.util.IdGenerator;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         07-Oct-2004
 */
public class ValuePartitionWizard extends OWLPatternWizard {

    private static final String vpPropNameProp = "vpPropNameProp";
    private static final String domainProp = "domainProp";
    private static final String vpValuesProp = "vpValuesProp";
    private static final String funcProp = "funcProp";

    protected ListEntryPage valuesEntryPage;

    protected void initialise() throws NullPointerException, WizardInitException {
        super.initialise();

        addUniqueProperty(vpPropNameProp, null);
        addUniqueProperty(domainProp, null);
        addUniqueProperty(vpValuesProp, null);
        addUniqueProperty(funcProp, Boolean.TRUE);

        StringValidator v = getDefaultValidator();

        Collection prefixes = getOwlHelper().getNamespacePrefixes(true);

        valuesEntryPage = new ListEntryPage("3", vpValuesProp, v, true, prefixes);
        addPage(valuesEntryPage);

        addPage(new ValidatedStringEntryPage("1", vpPropNameProp, v));

        addPage(new OptionPage("4", funcProp));

        addPage(new ClassTreePage("2", domainProp, owlThingProp));
    }

    protected void patternNameChanged(String newName) {
        String propertyPrefix = WizardAPI.getProperty("property.has");
        newName = ResourceNSValidator.prependTextToName(newName, propertyPrefix);
        String nsPrefix = ResourceNSValidator.getPrefixFromName(newName);
        if (nsPrefix != null) {
            valuesEntryPage.setPrefixText(nsPrefix + ResourceNSValidator.PREFIX_CHARACTER);
        }
        setProperty(vpPropNameProp, newName);
    }

    protected String getDefaultName() {
        return null;
    }

    protected boolean handleFinished(Console output) {

        IdGenerator idGen = WizardAPI.getWizardManager().getDefaultIdGenerator();

        OWLValuePartitionDescriptor descr = new OWLValuePartitionDescriptor() {
            public boolean isPropFunctional() {
                return ((Boolean) getProperty(funcProp)).booleanValue();
            }

            public String getPropName() {
                return (String) getProperty(vpPropNameProp);
            }

            public OWLNamedClass getPropDomain() {
                return (OWLNamedClass) getProperty(domainProp);
            }

            public Object getValueNames() {
                return getProperty(vpValuesProp);
            }

            public OWLModel getModel() {
                return ValuePartitionWizard.this.getModel();
            }
        };

        OWLValuePartition vp = new OWLValuePartition(descr,
                getPatternName(),
                getPatternParent(),
                output, idGen);

        OWLUI.selectResource(vp.getBaseClass(), null);

        return vp != null;
    }
}
