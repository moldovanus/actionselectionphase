package uk.ac.man.cs.mig.coode.protege.wizard.util;

import edu.stanford.smi.protege.model.Cls;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Nov-2004
 */
public class ClassIdentifierTester {

  private static ClassIdentifier _ci = null;

  public static void registerClassIdentifier(ClassIdentifier ci) {
    _ci = ci;
  }

  public static boolean isUserClass(Object cls) {
    if (_ci != null) {
      return _ci.isUserClass(cls);
    } else {
      throw new NullPointerException("HAVE NOT SET THE class identifier - call ClassIdentifierTester.registerClassIdentifier()");
    }
  }

  public static void printClassAttributes(Object cls){
    if (cls instanceof Cls){
      Cls c = (Cls)cls;
      System.out.println("cls = " + c + "{");
      System.out.print("Abstract = " + c.isAbstract());
      System.out.print(", ClsMetaCls = " + c.isClsMetaCls());
      System.out.print(", Concrete = " + c.isConcrete());
      System.out.print(", Editable = " + c.isEditable());
      System.out.print(", Included = " + c.isIncluded());
      System.out.print(", MetaCls = " + c.isMetaCls());
      System.out.print(", System = " + c.isSystem());
      System.out.print(", Visible = " + c.isVisible() + "}");
    }
    else{
      System.out.println("Object is not of type Cls");
    }
  }
}
