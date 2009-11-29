package uk.ac.man.cs.mig.coode.protege.wizard.owl;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards.NewClassWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OWLClassIdentifier;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.validation.ResourceNSValidatorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl.*;
import uk.ac.man.cs.mig.coode.protege.wizard.plugin.AbstractWizardLibraryPlugin;
import uk.ac.man.cs.mig.coode.protege.wizard.plugin.launcher.menu.WizardLaunchMenu;
import uk.ac.man.cs.mig.coode.protege.wizard.util.ClassIdentifierTester;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
public class OWLWizardsPlugin extends AbstractWizardLibraryPlugin {

    private static final boolean test = false;

    public static final String MENU_QUICK_OWL = "Quick OWL";
    public static final String MENU_PATTERNS = "Patterns";
    public static final String MENU_REFACTOR = "Refactor";
    public static final String MENU_OWL_TEST = "OWL Test Wizards";

    public static boolean isSuitable(Project project, Collection reasons) {
        boolean suitable = project.getKnowledgeBase() instanceof OWLModel;
        if (!suitable) {
            reasons.add("OWL Wizards can only be used with OWL models");
        }
        return suitable;
    }

    public boolean initialise() {
        boolean result = true;

        // make sure the default validator for wizards also check namespace
        wizardManager.setDefaultValidatorFactory(new ResourceNSValidatorFactory());

        // setup for identifying base level user defined classes
        ClassIdentifierTester.registerClassIdentifier(new OWLClassIdentifier());

        OWLModel owlModel = (OWLModel) wizardManager.getKnowledgeBase();
        OwlKbHelper helper = OwlKbHelper.getHelper(owlModel);

        /*
        // hide the meta ontology resources if they are imported
        Collection metaResources =
                helper.getResourcesFromNamespace(WizardAPI.getProperty("meta.url"),
                        RDFResource.class);
        if (metaResources != null) {
            helper.hideResources(metaResources);
            helper.ensureSubclassesAreVisible(metaResources, owlModel.getOWLThingClass(), true);
        }
        */

        // quick owl
        WizardLaunchMenu quickOwlMenu = createWizardMenu(MENU_QUICK_OWL);
        quickOwlMenu.addWizard(new OWLSubclassesWizard());
        quickOwlMenu.addWizard(new OWLPropertiesWizard());
        quickOwlMenu.addWizard(new OWLIndividualsWizard());
        quickOwlMenu.addWizard(new QuickRestrictionWizard());
        quickOwlMenu.addWizard(new NewClassWizard());
        quickOwlMenu.addWizard(new AnnotationWizard());

        // patterns
        WizardLaunchMenu patternWizards = createWizardMenu(MENU_PATTERNS);
        patternWizards.addWizard(new ValuePartitionWizard());
        patternWizards.addWizard(new EnumerationWizard());
        patternWizards.addWizard(new OWLListWizard());
        patternWizards.addWizard(new RDFListWizard());
        patternWizards.addWizard(new NaryRelationWizard());

        // refactor
        WizardLaunchMenu refMenu = createWizardMenu(MENU_REFACTOR);
        refMenu.addWizard(new MoveToNewNamespaceWizard());
//    refMenu.addWizard(new MoveRestrictionToAncestorClassWizard());
//    refMenu.addWizard(new SeparateIdWizard());
//    refMenu.addWizard(new ReifyRelationWizard());

        if (result) {
            System.out.println("Loaded OWL wizards library");
        }
        return result;
    }


    protected String getPluginDirectory() {
        return "uk.ac.man.cs.mig.coode.protege.wizard.owl";
    }
}
