package uk.ac.man.cs.mig.coode.protege.wizard.api;

import edu.stanford.smi.protege.model.KnowledgeBase;
import uk.ac.man.cs.mig.coode.protege.wizard.util.IdGenerator;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.BasicNameValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         02-Jun-2005
 */
public interface WizardManager {

  public WizardOptions getUserOptions();

  public Properties getProperties();

  public void addProperties(InputStream propertiesStream) throws IOException;

  public Frame getMainWindow();

  public KnowledgeBase getKnowledgeBase();

  void kill();

  IdGenerator getDefaultIdGenerator();

  void setDefaultIdGenerator(IdGenerator idGen);

  BasicNameValidator getDefaultValidator(KnowledgeBase kb, PropertiesCollection pc);

  public void updateUI();

  public void setActiveFrameInUI(edu.stanford.smi.protege.model.Frame f);

  public java.util.List getActiveFramesFromUI();

  String getDefaultDirectory();

  void handleErrors(java.util.List errors);

  void handleErrors(Exception e);
}
