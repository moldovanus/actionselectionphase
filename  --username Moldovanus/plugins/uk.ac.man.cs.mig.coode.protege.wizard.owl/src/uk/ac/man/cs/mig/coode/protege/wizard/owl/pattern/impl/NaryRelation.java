package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         23-Sep-2005
 */
public class NaryRelation extends DefaultOWLPattern {

    private static final String IS_PART_OF_PATTERN_ANNOT = "meta:isPartOfNaryRelation";

    private OWLProperty naryRelation;

    private OWLNamedClass parent;

    public NaryRelation(OWLPatternDescriptor descr,
                        String name, OWLNamedClass parent,
                        OWLNamedClass domainCls,
                        HashMap attributeHash,
                        IdGenerator idGen) {
        super(descr);

        this.parent = parent;

        OwlKbHelper helper = OwlKbHelper.getHelper(getModel());
        helper.setIdGenerator(idGen);

        // @@TODO maybe could have a KbHelper.createClassWithIteratorWhenRequired()
        String relationName = name.replaceFirst("has_", "") + "Relation"; // @@TODO or is_

        OWLNamedClass naryCls;
        int count = 0;
        do {
            naryCls = getModel().getOWLNamedClass(relationName + count++);
        }
        while (naryCls != null);

        relationName = relationName + --count;

        naryCls = (OWLNamedClass) helper.createClass(relationName, parent);
        naryCls.addSuperclass(getPatternParentClass());
        setBaseClass(naryCls);
        addPatternPart(naryCls);

        naryRelation = helper.createNewObjectProperty(name,
                domainCls,
                (OWLNamedClass) naryCls,
                true);
        addPatternPart(naryRelation);

        Collection attributeNames = attributeHash.keySet();
        for (Iterator i = attributeNames.iterator(); i.hasNext();) {
            String attrname = (String) i.next();

            OWLNamedClass range = (OWLNamedClass) attributeHash.get(attrname);
            if (range == null) {
                range = (OWLNamedClass) helper.createClassAtRoot("rangePlaceHolder_" + attrname);
            }

            addPatternPart(range);

            OWLProperty attrProp = helper.createNewObjectProperty(attrname,
                    (OWLNamedClass) naryCls,
                    range,
                    false);
            addPatternPart(attrProp);

            helper.addSomeValuesFromRestriction((OWLNamedClass) naryCls, attrProp, range);
        }
    }

    public OWLProperty getIsPartOfPatternAnnotProp() {
        return getModel().getOWLProperty(IS_PART_OF_PATTERN_ANNOT);
    }

    protected OWLNamedClass getPatternParentClass() {
        return parent;
    }
}
