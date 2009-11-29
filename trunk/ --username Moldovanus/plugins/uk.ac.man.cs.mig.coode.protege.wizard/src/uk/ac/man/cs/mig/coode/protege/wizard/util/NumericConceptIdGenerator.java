package uk.ac.man.cs.mig.coode.protege.wizard.util;

import edu.stanford.smi.protege.model.KnowledgeBase;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Feb-2005
 */
public class NumericConceptIdGenerator implements IdGenerator {

  private String prefixString = "";
  private long currentId = 0;
  private int paddedLen = 0;
  private KnowledgeBase kb;

  public NumericConceptIdGenerator(String prefix, long startNumber, int padding, KnowledgeBase kb) {
    prefixString = prefix;
    currentId = startNumber;
    paddedLen = padding;
    this.kb = kb;
  }

  public String getNextId() {
    String newTerm;
    do {
      newTerm = prefixString + pad(currentId, paddedLen);
      currentId++;
    } while (kb.getFrame(newTerm) != null);

    return newTerm;
  }

  public String getPrefix() {
    return prefixString;
  }

  public int getPadding() {
    return paddedLen;
  }

  private String pad(long currentId, int length) {
    StringBuffer s = new StringBuffer(Long.toString(currentId));
    int paddingChars = length - s.length();
    while (paddingChars > 0) {
      s.insert(0, '0');
      paddingChars--;
    }
    return s.toString();
  }
}

