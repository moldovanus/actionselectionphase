package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.IdGenerator;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         23-Sep-2005
 */
public class OWLEnumeration extends DefaultOWLPattern {

    private static final String IS_PART_OF_PATTERN_ANNOT = "meta:isPartOfEnumeration";

    protected OWLProperty property;

    private OWLNamedClass parent;

    public OWLEnumeration(OWLPatternDescriptor descr,
                          String name,
                          OWLNamedClass parent,
                          Collection valueIndividualNames,
                          boolean valuesDifferent,
                          String propName,
                          OWLNamedClass domainCls,
                          IdGenerator idGen) {

        super(descr);

        this.parent = parent;

        OwlKbHelper helper = OwlKbHelper.getHelper(getModel());
        helper.setIdGenerator(idGen);

        OWLNamedClass enumClass = (OWLNamedClass) helper.createClass(name,
                parent);
        enumClass.addSuperclass(getPatternParentClass());
        setBaseClass(enumClass);
        addPatternPart(enumClass);

        // @@TODO if there are more Enumerations, we should make this one disjoint from them

        Collection valueIndividuals = helper.createInstances(valueIndividualNames, enumClass);

        for (Iterator i = valueIndividuals.iterator(); i.hasNext();) {
            OWLIndividual ind = (OWLIndividual) i.next();
            addPatternPart(ind);
        }

        if (valuesDifferent) {
            helper.makeAllDifferent(valueIndividuals); // make all the values different
        }

        // create an equivalent set restriction on the enumeration
        //trace("Creating an equivalent set restriction on " + enumClass.getName());
        helper.addEquivalentSetRestriction(enumClass, valueIndividuals);

        // create the property associated with the enum
        //trace("Creating an associated property: " + propName);
        property = helper.createNewObjectProperty(propName, domainCls, enumClass, true);
        addPatternPart(property);
    }

    public OWLProperty getIsPartOfPatternAnnotProp() {
        return getModel().getOWLProperty(IS_PART_OF_PATTERN_ANNOT);
    }

    protected OWLNamedClass getPatternParentClass() {
        return parent;
    }
}
