package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLValuePartitionDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.util.IdGenerator;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         25-Apr-2005
 */
public class OWLValuePartition extends DefaultOWLPattern {

    private static final boolean debug = false;

    private static final String IS_PART_OF_PATTERN_ANNOT = "meta:isPartOfValuePartition";

    private Collection valueClasses;
    private OWLProperty property;

    private OWLNamedClass parent;

    public OWLValuePartition(OWLValuePartitionDescriptor descr,
                             String name,
                             OWLNamedClass parent,
                             Console output,
                             IdGenerator idGen) {
        super(descr);

        this.parent = parent;

        output.out("Creating a ValuePartition: " + name);
        OwlKbHelper helper = OwlKbHelper.getHelper(getModel());
        helper.setIdGenerator(idGen);

        OWLNamedClass mainVPClass = (OWLNamedClass) helper.createClass(name, parent);
        setBaseClass(mainVPClass);
        addPatternPart(mainVPClass);

        // @@TODO if there are more VPs, we should make this one disjoint from them

        valueClasses = createValueClasses(descr.getValueNames(), getModel());

        // ensure all direct subclasses are disjoint (also with their existing siblings)
        OWLUtil.ensureSubclassesDisjoint(mainVPClass);

        // always make values disjoint
        OwlKbHelper.ensureAllSubclassesDisjoint(valueClasses);

        // create a covering axiom on the value partition
        helper.addCoveringRestriction(mainVPClass, valueClasses);

        // create the property associated with the VP
        output.out("Creating a property: " + descr.getPropName());
        property = helper.createNewObjectProperty(descr.getPropName(),
                descr.getPropDomain(),
                mainVPClass,
                true);

        property.setFunctional(descr.isPropFunctional());

        addPatternPart(property);
    }

    public Collection createValueClasses(Object valueClassNames, OWLModel okb) {

        OwlKbHelper helper = OwlKbHelper.getHelper(okb);

        Collection classes = new ArrayList();

        if (valueClassNames instanceof Collection) {
            Collection newClasses = (Collection) valueClassNames;
            for (Iterator i = newClasses.iterator(); i.hasNext();) {
                String newClassName = (String) i.next();
                OWLNamedClass valueClass = (OWLNamedClass) helper.createClass(newClassName, getBaseClass());
                classes.add(valueClass);
                addPatternPart(valueClass);
            }
        } else if (valueClassNames instanceof DefaultMutableTreeNode) {
            classes.addAll(helper.createSubclasses((DefaultMutableTreeNode) valueClassNames,
                    getBaseClass()));
            for (Iterator i = classes.iterator(); i.hasNext();) {
                addPatternPart((OWLNamedClass) i.next());
            }
        } else {
            if (debug) System.err.println("wrong value names passed in");
        }

        return classes;
    }

    public OWLProperty getIsPartOfPatternAnnotProp() {
        return getModel().getOWLProperty(IS_PART_OF_PATTERN_ANNOT);
    }

    protected OWLNamedClass getPatternParentClass() {
        return parent;
    }
}