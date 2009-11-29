package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardOptions;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLPattern;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         25-Apr-2005
 */
public abstract class DefaultOWLPattern implements OWLPattern {

    private static final String IS_PART_OF_PATTERN_ANNOT = "meta:isPartOfPattern";
    private static final String HAS_PATTERN_PART_ANNOT = "meta:hasPatternPart";

    private OWLNamedClass baseClass;
    private Collection patternParts;
    private OWLModel model;

    abstract protected OWLNamedClass getPatternParentClass();

    public DefaultOWLPattern(OWLPatternDescriptor descr) {
        this.model = descr.getModel();
    }

    public void setBaseClass(OWLNamedClass base) {
        this.baseClass = base;
    }

    public OWLNamedClass getBaseClass() {
        return this.baseClass;
    }

    public void removeFromModel() {
        for (Iterator i = patternParts.iterator(); i.hasNext();) {
            ((RDFResource) i.next()).delete();
        }
    }

    public OWLProperty getIsPartOfPatternAnnotProp() {
        return model.getOWLProperty(IS_PART_OF_PATTERN_ANNOT);
    }

    public OWLProperty getPatternPartsAnnotProp() {
        return model.getOWLProperty(HAS_PATTERN_PART_ANNOT);
    }

    public Collection getPatternParts() {
        return patternParts;
    }

    /**
     * Not yet implemented
     *
     * @param okb
     * @param part
     * @return OWLPattern
     */
    public OWLPattern getPatternFromPart(OWLModel okb, RDFResource part) {
        OWLPattern pattern = null;

        if (isPartOfPattern(okb, part)) {
            RDFResource patternRoot = (RDFResource) part.getPropertyValue(getIsPartOfPatternAnnotProp());

//      if (patternRoot != null){
//          pattern = new OWLValuePartition(patternRoot);
//      }
        }
        return pattern;
    }

    public static boolean isPartOfPattern(OWLModel okb, RDFResource part) {
        return false;
    }

    protected void addPatternPart(RDFResource part) {

        if (patternParts == null) {
            patternParts = new ArrayList();
        }

        OwlKbHelper helper = OwlKbHelper.getHelper(model);

        if (WizardAPI.getWizardManager().getUserOptions().getBoolean(WizardOptions.OPTION_IMPORT_META)) {

            helper.addAnnotation(part,
                    getIsPartOfPatternAnnotProp(),
                    getBaseClass(),
                    null);

            helper.addAnnotation(getBaseClass(),
                    getPatternPartsAnnotProp(),
                    part,
                    null);
        }

        patternParts.add(part);
    }

    protected OWLModel getModel() {
        return model;
    }
}
