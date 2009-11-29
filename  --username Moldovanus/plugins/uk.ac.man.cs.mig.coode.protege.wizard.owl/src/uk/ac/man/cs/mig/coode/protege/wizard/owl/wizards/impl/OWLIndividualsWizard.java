package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import uk.ac.man.cs.mig.coode.protege.wizard.basic.wizards.EnterInstancesWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.OptionPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Oct-2004
 */
public class OWLIndividualsWizard extends EnterInstancesWizard {

    protected static final String differentProp = "differentProp";

    public boolean isSuitable(KnowledgeBase knowledgeBase) {
        return knowledgeBase instanceof OWLModel;
    }

    protected void initialise() throws NullPointerException, WizardInitException {
        super.initialise();

        addProperty(differentProp, Boolean.TRUE);

        addPage(new OptionPage("2", differentProp));
    }

    protected boolean handleFinished(Console output) {
        // create all the classes
        boolean result = super.handleFinished(output);

        // now add disjoints if appropriate
        if (((Boolean) getProperty(differentProp)).booleanValue()) {
            Object instantiatingClass = getProperty(superclassProp);

            if (instantiatingClass instanceof OWLNamedClass) {
                // ensure all individuals are different (also with any existing individuals)
                OWLNamedClass inst = (OWLNamedClass) instantiatingClass;
                OwlKbHelper helper = OwlKbHelper.getHelper(inst.getOWLModel());
                helper.makeAllDifferent(inst);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int num = new Integer(args[0]).intValue();
        for (int i = 0; i < num; i++) {
            System.out.println("indiv" + i);
        }
    }
}
