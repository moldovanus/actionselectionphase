package uk.ac.man.cs.mig.coode.protege.wizard.owl.test;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import uk.ac.man.cs.mig.coode.protege.wizard.api.DefaultWizardManager;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl.*;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.StringEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.AbstractWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.ProtegeWizard;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         02-Jun-2005
 */
public class TestOWLWizards {

    private JenaOWLModel model;
    private static ProtegeWizard[] wizards;

    public TestOWLWizards() throws OntologyLoadException {
        model = ProtegeOWL.createJenaOWLModel();

        model.createOWLNamedClass("A");
        model.createOWLNamedClass("B");
        model.createOWLNamedClass("C");
        model.createOWLNamedClass("D");

        model.createOWLObjectProperty("p");
        model.createOWLObjectProperty("q");
        model.createOWLObjectProperty("r");

        WizardAPI.setWizardManager(new DefaultWizardManager(model));

        wizards = new ProtegeWizard[]{
                new OWLSubclassesWizard(),
                new OWLIndividualsWizard(),
                new EnumerationWizard(),
                new ValuePartitionWizard(),
                new OWLListWizard(),
                new AnnotationWizard(),
                new RDFListWizard(),
                new MoveToNewNamespaceWizard(),
                new QuickRestrictionWizard(),
                new NaryRelationWizard(),
//      new SeparateIdWizard(),

// work in progress
//      new ReifyRelationWizard(),
//      new ImportWizard(),
//      new MoveRestrictionToAncestorClassWizard(),
                createWizardOnTheFly(),
                null // easier than keep on removing commas
        };

        for (int i = 0; i < wizards.length - 1; i++) {
            testWizard(wizards[i]);
        }
    }

    public static void main(String[] args) {

        TestOWLWizards ont;
		try {
			ont = new TestOWLWizards();
	        ont.save("c:\\Nick\\Ontologies\\List\\testwizardresults.owl");
		} catch (OntologyLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        System.exit(0);
    }

    private static void testWizard(ProtegeWizard wizard) {

        System.out.println("\nTESTING WIZARD " + wizard.getWizardLabel());

        int result = wizard.showModalDialog();

        switch (result) {
            case ProtegeWizard.WIZARD_FAIL:
                System.out.println("ERRORS: " + wizard.getErrors());
                System.exit(1);
                break;
            case ProtegeWizard.WIZARD_CANCEL:
                System.out.println("USER CANCELLED");
                break;
            case ProtegeWizard.WIZARD_SUCCESS:
                System.out.println("Wizard was successful");
                break;
        }
    }

    private static ProtegeWizard createWizardOnTheFly() {
        return new AbstractWizard("MyFishWizard.properties") {
            protected void initialise() throws NullPointerException, WizardInitException {
                addProperty("fish", "fish");
                addPage(new StringEntryPage("fishPage", "fish", 1));
            }

            protected boolean handleFinished(Console output) {
                return getHelper().createClassAtRoot((String) getProperty("fish")) != null;
            }
        };
    }

    public OWLModel getModel() {
        return model;
    }

    public void save(String fileName) {
        Collection errors = new ArrayList();
        try {
            model.save(new URI(fileName), FileUtils.langXMLAbbrev, errors);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("File saved with " + errors.size() + " errors.");
    }


}
