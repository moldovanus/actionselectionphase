package uk.ac.man.cs.mig.coode.protege.wizard.owl.validation;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.impl.ProtegeFrameNameValidator;
import uk.ac.man.cs.mig.coode.protege.wizard.wizard.PropertiesCollection;

import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         17-Mar-2005
 */
public class ResourceNSValidator extends ProtegeFrameNameValidator{

  public static final String PREFIX_CHARACTER = ":";

  public ResourceNSValidator(KnowledgeBase kb, PropertiesCollection pc) {
    super(kb, pc);
  }

  public boolean isValid(String s, Collection currentEntries, String currentProp, Collection errors) {
    boolean result = false;
    NamespaceManager nsm = ((OWLModel)theKnowledgebase).getNamespaceManager();
    String prefix = getPrefixFromName(s);

    //boolean test = s.startsWith("owl:");

    if ((prefix != null) && (nsm.getPrefixes().contains(prefix))){
      //if (test) System.out.println("has got a valid prefix " + prefix);
      if (s.length() > prefix.length()+1){
        //if (test) System.out.println("there is a core");
        String coreName = getCoreName(s);
        if (isUnique(s, currentEntries, currentProp, errors)){
          //if (test) System.out.println("is unique");
          if (isFrameNameLegal(coreName, errors)){
            //if (test) System.out.println("is legal");
            result = true;
          }
        }
      }
      else{
        //if (test) System.out.println("only namespace");
        errors.add("Valid namespace, but no name");
      }
    }
    else{
      //if (test) System.out.println("no namespace");
      result = super.isValid(s, currentEntries, currentProp, errors);
    }

    //if (test) System.out.println("result = " + result);

    return result;
  }

  public String fix(String s, Collection currentEntries, String currentProp, Collection log) {
    String pre = getPrefixFromName(s);
    if (pre == null){
      s = super.fix(s, currentEntries, currentProp, log);
    }
    else{
      pre = pre + PREFIX_CHARACTER;
      // ignore the first ":"
      s = pre + getCoreName(s).replaceAll(exclusionsSet, "_");
      String stem = new String(s);
      int postfix = 2;
      while (!isUnique(s, currentEntries, currentProp, log)) {
        s = stem + postfix++;
      }
    }

    return s;
  }

  public static String getCoreName(String s) {
    if (s != null){
      String pre = getPrefixFromName(s);
      if (pre != null){
        s = s.substring(pre.length()+1);
      }
    }
    return s;
  }

  public static String getPrefixFromName(String s){
    String[] parts = s.split(PREFIX_CHARACTER);
    String prefix = null;
    if (!(s.startsWith(PREFIX_CHARACTER)) &&
        ((parts.length > 1) || (s.endsWith(PREFIX_CHARACTER)))){
      prefix = parts[0];
    }
    return prefix;
  }

  public static String prependTextToName(String name, String pre){
    String prefix = getPrefixFromName(name);
    String result = pre + getCoreName(name);
    if (prefix != null){
      result = prefix + PREFIX_CHARACTER + result;
    }
    return result;
  }
}
