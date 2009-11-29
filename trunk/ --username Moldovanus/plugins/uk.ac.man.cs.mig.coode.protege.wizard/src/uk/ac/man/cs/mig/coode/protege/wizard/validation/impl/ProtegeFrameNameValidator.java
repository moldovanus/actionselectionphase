package uk.ac.man.cs.mig.coode.protege.wizard.validation.impl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         27-Jul-2004
 */
public class ProtegeFrameNameValidator extends BasicNameValidator {

  private static final String inclusions = "a-zA-Z$£_";
  private static final String exclusions = "#~@\'\\s:;()=,\\*!\"%\\^\\&|\\[\\]{}<>¬\\\\/";
  public static final String exclusionsSet = "[" + exclusions + "]";
  private static final String validFormat = "[" + inclusions + "]+[^" + exclusions + "]*";

  protected KnowledgeBase theKnowledgebase;

  public ProtegeFrameNameValidator(KnowledgeBase kb, PropertiesCollection pc) {
    super(pc);
    this.theKnowledgebase = kb;
  }

  public String fix(String s, Collection currentEntries, String currentProp, Collection log) {
    return super.fix(s.replaceAll(exclusionsSet, "_"), currentEntries, currentProp, log);
  }

  public boolean isUnique(String s, Collection currentEntries, String currentPropertyName, Collection errors) {
    return ((super.isUnique(s, currentEntries, currentPropertyName, errors)) &&
            (!frameNameUsedInKB(s, theKnowledgebase, errors)));
  }

  public boolean isValid(String s, Collection currentEntries, String currentProp, Collection errors) {
    //System.out.println("ProtegeFrameNameValidator.isValid");
    boolean valid = false;
    if (super.isValid(s, currentEntries, currentProp, errors)) {
      if (isFrameNameLegal(s, errors)) {
        if (!frameNameUsedInKB(s, theKnowledgebase, errors)) {
          valid = true;
        }
      }
    }
    return valid;
  }

  protected boolean isFrameNameLegal(String name, Collection errors) {
    boolean result = true;
    if (!name.matches(validFormat)){
      if (errors != null){
        errors.add("Illegal characters in name (spaces and/or punctuation)");
      }
      result = false;
    }
    return result;
  }

  private boolean frameNameUsedInKB(String s, KnowledgeBase kb, Collection errors) {
    boolean result = false;
    if ((kb != null) && (kb.getFrame(s) != null)){
      if (errors != null){
        errors.add("The knowledgebase already uses that name, sorry!");
      }
      result = true;
    }
    return result;
  }
}
