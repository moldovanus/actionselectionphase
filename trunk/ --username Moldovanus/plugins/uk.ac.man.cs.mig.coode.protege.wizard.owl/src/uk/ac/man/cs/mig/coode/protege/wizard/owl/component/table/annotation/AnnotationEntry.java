package uk.ac.man.cs.mig.coode.protege.wizard.owl.component.table.annotation;

import edu.stanford.smi.protegex.owl.model.OWLProperty;


/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         10-Aug-2004
 */
public class AnnotationEntry {

  public final static int FIELD_ANNOT_PROP = 0;
  public final static int FIELD_VALUE = 1;
  public final static int FIELD_LANG = 2;
  public final static int FIELD_USE = 3;

  public final static String[] fieldNames = {"Annotation", "Value", "Lang", "Use"};
  public final static Class[] fieldClasses = {OWLProperty.class, Object.class,
                                              String.class, Boolean.class};

  public OWLProperty annotationProp;
  public Object value;
  public String lang;
  public Boolean use;

  public AnnotationEntry(OWLProperty property, Object value, String lang, Boolean use) {
    this.annotationProp = property;
    this.value = value;
    this.lang = lang;
    this.use = use;
  }

  public void set(int field, Object newValue) {
    switch (field) {
      case FIELD_ANNOT_PROP:
        annotationProp = (OWLProperty) newValue;
        break;
      case FIELD_VALUE:
        value = newValue;
        break;
      case FIELD_LANG:
        lang = (String) newValue;
        break;
      case FIELD_USE:
        use = (Boolean) newValue;
        break;
    }
  }

  public Object get(int field) {
    Object result = null;

    switch (field) {
      case AnnotationEntry.FIELD_ANNOT_PROP:
        result = annotationProp;
        break;
      case AnnotationEntry.FIELD_VALUE:
        result = value;
        break;
      case AnnotationEntry.FIELD_LANG:
        result = lang;
        break;
      case AnnotationEntry.FIELD_USE:
        result = use;
        break;
    }
    return result;
  }

  public String toString() {
    return "ANNOTATION{" +
           annotationProp.getName() + ", " +
           value + ", " +
           lang + "}";
  }
}
