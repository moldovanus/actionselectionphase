package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         09-Oct-2005
 */
public class MetaOntologyNotImportedException extends Exception{

  public MetaOntologyNotImportedException() {
    super("The CO-ODE meta ontology must be imported before this pattern can be used");
  }
}
