package uk.ac.man.cs.mig.coode.protege.wizard.plugin;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.AbstractProjectPlugin;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.ui.ProjectMenuBar;
import edu.stanford.smi.protege.ui.ProjectToolBar;
import edu.stanford.smi.protege.ui.ProjectView;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.plugin.launcher.menu.WizardLaunchMenu;
import uk.ac.man.cs.mig.coode.protege.wizard.util.KbHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         28-Jul-2004
 */
public abstract class AbstractWizardLibraryPlugin extends AbstractProjectPlugin {

  public static final boolean debug = false;

  protected static ProtegeGuiWizardManager wizardManager = null;

  public abstract boolean initialise();

  public final void afterShow(ProjectView projectView, ProjectToolBar projectToolBar, ProjectMenuBar projectMenuBar) {

    double starttime = System.currentTimeMillis();

    if (wizardManager == null) {
      Project project = ProjectManager.getProjectManager().getCurrentProject();
      wizardManager = new ProtegeGuiWizardManager(project.getKnowledgeBase(),
                                                  projectMenuBar);
      WizardAPI.setWizardManager(wizardManager);
    }

    try {
      // @@TODO could look for a file with the current language
      File localPropsFile = new File(PluginUtilities.getPluginsDirectory() + File.separator +
                                     getPluginDirectory() + File.separator + "plugin.properties");
      wizardManager.addProperties(new FileInputStream(localPropsFile));
    }
    catch (IOException e) {
    } // don't worry if no file is found

    initialise();

    double endtime = System.currentTimeMillis();
    //System.out.println("LOADED WIZARDS: " + ((endtime - starttime) / 1000.0));
  }

  protected abstract String getPluginDirectory();

  public final void beforeHide(ProjectView projectView, ProjectToolBar projectToolBar, ProjectMenuBar projectMenuBar) {
    if (wizardManager != null) {
      wizardManager.kill();
      wizardManager = null;
      WizardAPI.setWizardManager(null);
    }
    KbHelper.resetHelper();
  }

  public final WizardLaunchMenu createWizardMenu(String newMenuName) {
    return wizardManager.getNewWizardMenu(newMenuName);
  }

  public final WizardLaunchMenu getDefaultWizardMenu() {
    return wizardManager.getDefaultWizardMenu();
  }

  public final void afterCreate(Project project) {
  }

  public final void afterLoad(Project project) {
  }

  public final void beforeSave(Project project) {
  }

  public final void afterSave(Project p) {
  }

  public final void beforeClose(Project project) {
  }
}
