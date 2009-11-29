package uk.ac.man.cs.mig.coode.protege.wizard.owl;

import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         25-Apr-2005
 */
public class PatternListener extends ModelAdapter {

  private boolean removing = false;

  public void classDeleted(RDFSClass rdfsClass) {
//    System.out.println("PatternListener.classDeleted "+ rdfsClass.getWizardLabel());
//    if (!removing){ // make sure we don't keep asking while deleting the parts
//
//      System.out.println("superclasses = " + rdfsClass.getPureSuperclasses());
//
//      OWLPattern vp = OWLPattern.getPatternFromPart(rdfsClass);
//      if (vp != null){
//        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(WizardAPI.getWizardManager().getMainWindow(),
//                                                                    "Deleted part of pattern. Would you like to delete the whole pattern?",
//                                                                    "Pattern Found",
//                                                                    JOptionPane.YES_NO_OPTION,
//                                                                    JOptionPane.QUESTION_MESSAGE)){
//          removing = true;
//          vp.removeFromModel();
//          removing = false;
//        }
//      }
//    }
  }
}
