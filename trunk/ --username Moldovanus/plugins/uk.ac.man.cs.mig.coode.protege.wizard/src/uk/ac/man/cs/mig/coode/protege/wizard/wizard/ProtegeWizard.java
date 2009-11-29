package uk.ac.man.cs.mig.coode.protege.wizard.wizard;

import edu.stanford.smi.protege.model.KnowledgeBase;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.page.ProtegeWizardPage;

import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         02-Jun-2005
 */
public interface ProtegeWizard extends PropertiesSource{

  public static int WIZARD_SUCCESS = 0;
  public static int WIZARD_FAIL = 1;
  public static int WIZARD_CANCEL = 2;

  String getWizardLabel();
  String getWizardDescription();
  String getWizardProperty(String key);

  public String getMenuString();

  public String getPageLabel(String name);
  public String getPagePrompt(String name);
  public String getPageProperty(String name, String key);

  boolean addPage(ProtegeWizardPage thePage) throws WizardInitException;
  boolean addPage(int index, ProtegeWizardPage thePage) throws WizardInitException;

  void setKbEntity(edu.stanford.smi.protege.model.Frame entity);

  int showModalDialog();
  List getErrors();

  boolean isSuitable(KnowledgeBase kb);
  List canBeLaunched();
}
