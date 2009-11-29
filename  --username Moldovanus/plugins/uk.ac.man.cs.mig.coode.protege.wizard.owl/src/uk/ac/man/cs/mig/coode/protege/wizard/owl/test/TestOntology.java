package uk.ac.man.cs.mig.coode.protege.wizard.owl.test;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import uk.ac.man.cs.mig.coode.protege.wizard.api.DefaultWizardManager;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardManager;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLListsPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.DefaultOWLListsPatternDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         09-Mar-2006
 */
public class TestOntology {

    protected Project project;
    protected JenaOWLModel owlModel;
    protected OWLNamedClass owlThing;
    protected OWLListsPatternDescriptor descr;

    protected TestOntology() {
        OntDocumentManager.getInstance().reset(true);
        Collection errors = new ArrayList();
        final JenaKnowledgeBaseFactory factory = new JenaKnowledgeBaseFactory();
        project = Project.createNewProject(factory, errors);
        project.setKnowledgeBaseFactory(factory);
        owlModel = (JenaOWLModel) project.getKnowledgeBase();
        owlThing = owlModel.getOWLThingClass();

        WizardManager wizardManager = new DefaultWizardManager(owlModel);
        WizardAPI.setWizardManager(wizardManager);

        try {
            // @@TODO could look for a file with the current language
            File localPropsFile = new File(PluginUtilities.getPluginsDirectory() + File.separator +
                    getPluginDirectory() + File.separator + "plugin.properties");
            WizardAPI.getWizardManager().addProperties(new FileInputStream(localPropsFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        } // don't worry if no file is found

        System.out.println("MODEL: " + owlModel);

        descr = DefaultOWLListsPatternDescriptor.getDescriptor(owlModel);
    }

    private String getPluginDirectory() {
        return "uk.ac.man.cs.mig.coode.protege.wizard.owl";
    }

    public void save(String filepath) {
        try {
            File outFile = new File(filepath);
            owlModel.save(outFile.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
