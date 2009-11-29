package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.OWLEnumeration;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.validation.ResourceNSValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.OWLPatternWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ClassTreePage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ListEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.OptionPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ValidatedStringEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         07-Oct-2004
 */
public class EnumerationWizard extends OWLPatternWizard {

    private static final String enumPropNameProp = "enumPropNameProp";
    private static final String domainProp = "domainProp";
    private static final String enumValuesProp = "enumValuesProp";
    private static final String differentProp = "differentProp";

    protected void initialise() throws NullPointerException, WizardInitException {
        super.initialise();

        addUniqueProperty(enumPropNameProp, null);
        addUniqueProperty(domainProp, null);
        addUniqueProperty(enumValuesProp, null);
        addUniqueProperty(differentProp, Boolean.TRUE);

        StringValidator v = getDefaultValidator();

        OwlKbHelper helper = getOwlHelper();

        addPage(new ListEntryPage("3", enumValuesProp, v, false, helper.getNamespacePrefixes(true)));

        addPage(new ValidatedStringEntryPage("1", enumPropNameProp, v));

        addPage(new OptionPage("4", differentProp));

        addPage(new ClassTreePage("2", domainProp, owlThingProp));
    }

    protected void patternNameChanged(String newName) {
        String propertyPrefix = WizardAPI.getProperty("property.has");
        newName = ResourceNSValidator.prependTextToName(newName, propertyPrefix);
        setProperty(enumPropNameProp, newName);
    }

    protected String getDefaultName() {
        return null;
    }

    protected boolean handleFinished(Console output) {

        OWLPatternDescriptor descr = new OWLPatternDescriptor() {
            public OWLModel getModel() {
                return EnumerationWizard.this.getModel();
            }
//
//      public String getName() {
//        return getPatternName();
//      }
//
//      public OWLNamedClass getParentClass() {
//        return getPatternParent();
//      }
        };

        OWLEnumeration pattern =
                new OWLEnumeration(descr,
                        getPatternName(), getPatternParent(),
                        (Collection) getProperty(enumValuesProp),
                        ((Boolean) getProperty(differentProp)).booleanValue(),
                        (String) getProperty(enumPropNameProp),
                        (OWLNamedClass) getProperty(domainProp),
                        WizardAPI.getWizardManager().getDefaultIdGenerator());

        if (pattern != null) {
            OWLUI.selectResource(pattern.getBaseClass(), null);
        }

        return (pattern != null);
    }
}
