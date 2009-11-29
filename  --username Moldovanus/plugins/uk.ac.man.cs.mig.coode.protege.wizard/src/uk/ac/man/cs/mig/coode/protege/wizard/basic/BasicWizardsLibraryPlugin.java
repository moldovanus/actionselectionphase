package uk.ac.man.cs.mig.coode.protege.wizard.basic;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.clips.ClipsKnowledgeBaseFactory;
import edu.stanford.smi.protege.storage.xml.XMLKnowledgeBaseFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards.EnterInstancesWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards.EnterSubclassesWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards.NewClassWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.TextPage;
import uk.ac.man.cs.mig.coode.protege.wizard.plugin.AbstractWizardLibraryPlugin;
import uk.ac.man.cs.mig.coode.protege.wizard.plugin.launcher.menu.WizardLaunchMenu;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
public class BasicWizardsLibraryPlugin extends AbstractWizardLibraryPlugin {

    private static final boolean test = false;

    public boolean initialise() {
        boolean result = true;

        WizardLaunchMenu wm = getDefaultWizardMenu();
        wm.addWizard(new NewClassWizard());
        wm.addWizard(new EnterSubclassesWizard());
        wm.addWizard(new EnterInstancesWizard());

//    WizardLaunchMenu refMenu = createWizardMenu("Refactor");
//    refMenu.addWizard(new SeperateIdWizard());

        if (test) {
            wm.addWizard(new AbstractWizard("test post wizard focus", "test me") {

                protected void initialise() throws NullPointerException, WizardInitException {
                    addPage(new TextPage("str", "fish"));
                }

                protected boolean handleFinished(Console output) {
                    KbHelper h = getHelper();
                    Object one = h.createClassAtRoot("one");
                    Object two = h.createClass("two", one);
                    Object three = h.createClass("three", two);

                    WizardAPI.getWizardManager().setActiveFrameInUI((Frame) three);

                    return true;
                }
            });
        }

        if (result) {
            System.out.println("Loaded standard Protege wizards library");
        }

        return result;
    }

    protected String getPluginDirectory() {
        return "uk.ac.man.cs.mig.coode.protege.wizard";
    }

    public static boolean isSuitable(Project project, Collection reasons) {
        boolean suitable = 	project.getKnowledgeBaseFactory() instanceof ClipsKnowledgeBaseFactory || 
        					project.getKnowledgeBaseFactory() instanceof XMLKnowledgeBaseFactory;
        if (!suitable) {
            reasons.add("Basic Wizards can only be used with base Protege");
        }
        return suitable;
    }
}
