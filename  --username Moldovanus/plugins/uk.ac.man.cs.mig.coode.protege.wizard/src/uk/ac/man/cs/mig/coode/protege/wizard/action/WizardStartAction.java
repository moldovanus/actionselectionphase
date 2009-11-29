package uk.ac.man.cs.mig.coode.protege.wizard.action;

import edu.stanford.smi.protege.model.Frame;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.icon.WizardIcons;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.ProtegeWizard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         22-Jul-2004
 */
public class WizardStartAction extends AbstractAction {

  public static final boolean debug = false;

  private ProtegeWizard wizard;

  public WizardStartAction(ProtegeWizard theWizard) {
    super(theWizard.getWizardLabel() + "...", WizardIcons.getHatIcon());
    this.wizard = theWizard;
  }

  public void actionPerformed(ActionEvent e) {
    if (debug) System.out.println("WizardStartAction.actionPerformed");
    try {
      List frames = WizardAPI.getWizardManager().getActiveFramesFromUI();
      if (frames != null){
        if (debug) System.out.println("SETTING FRAME TO = " + frames);
        Frame kbEntity = (Frame)frames.get(0);
        wizard.setKbEntity(kbEntity);
      }
      else{
        System.err.println("NO FRAME FOUND!!!!!!!!!!!!!!!!!!!");
      }
    }
    catch (Exception we) {
      we.printStackTrace();
      //System.err.println("COULDN't FIND A SELECTION FROM THE GUI");
    }
    wizard.showModalDialog();
  }
}
