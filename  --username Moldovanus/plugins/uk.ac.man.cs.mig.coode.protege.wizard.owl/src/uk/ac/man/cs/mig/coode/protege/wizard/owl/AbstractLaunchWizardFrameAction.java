package uk.ac.man.cs.mig.coode.protege.wizard.owl;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.ProtegeWizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         10-Aug-2004
 */
public abstract class AbstractLaunchWizardFrameAction extends ResourceAction {

  private ProtegeWizard wizard;

  public AbstractLaunchWizardFrameAction(String s, Icon icon) {
    super(s, icon);
  }

  protected abstract ProtegeWizard createWizard();

  public boolean isSuitable(Component component, RDFResource resource) {
    return true;
  }

  public void actionPerformed(ActionEvent e) {
    wizard = createWizard();
    wizard.setKbEntity(getResource());
    if (wizard.showModalDialog() == ProtegeWizard.WIZARD_FAIL){
      WizardAPI.getWizardManager().handleErrors(wizard.getErrors());
    }
  }
}
