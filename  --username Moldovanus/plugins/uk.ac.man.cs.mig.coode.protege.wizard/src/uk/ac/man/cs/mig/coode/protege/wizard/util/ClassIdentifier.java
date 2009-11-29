package uk.ac.man.cs.mig.coode.protege.wizard.util;



/**
 * An iterface that can be implemented to help categorise whether a class has
 * certain attributes (eg if this is a user created class)
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Nov-2004
 */
public interface ClassIdentifier {
  public boolean isUserClass(Object cls);
}
