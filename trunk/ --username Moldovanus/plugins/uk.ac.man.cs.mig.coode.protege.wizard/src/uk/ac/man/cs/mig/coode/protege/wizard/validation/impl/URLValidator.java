package uk.ac.man.cs.mig.coode.protege.wizard.validation.impl;

import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         14-Feb-2005
 */
public class URLValidator implements StringValidator {

  public boolean isValid(String s, Collection currentEntries, String currentProp, Collection errors) {
    boolean result = false;
    if (s.length() == 0){
      result = true;
    }
    else{
    try {
      URL testURL = new URL(s);
      result = true;
    }
    catch (MalformedURLException e) {
      errors.add(e.getMessage());
    }
    }
    return result;
  }

  public String fix(String s, Collection currentEntries, String currentProp, Collection log) {
    return "Fix NOT implemented for the URL validator";
  }
}
