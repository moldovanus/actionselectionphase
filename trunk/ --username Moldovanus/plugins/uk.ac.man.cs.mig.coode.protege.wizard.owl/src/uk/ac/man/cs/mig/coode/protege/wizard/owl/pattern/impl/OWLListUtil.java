package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl;

import edu.stanford.smi.protegex.owl.model.*;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLListsPatternDescriptor;

import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         09-Mar-2006
 */
public class OWLListUtil {

//    /**
//     *
//     * @param sublists each sublist must be appropriate for its part in the list
//     * ie 0..(n-1) must be open (non terminated)
//     * @param descr
//     * @return null
//     */
//    public static OWLList join (List<OWLList> sublists, OWLListsPatternDescriptor descr){
//        OWLList join = new OWLList(descr, null, null);
////        join.
//        return null;
//    }
//
//    /**
//     * Like join, but elements are inlined
//     * @return an OWLList containing the same elements as each list given in order
//     */
//    public static OWLList merge (){
//        return null;
//    }


    /**
     * A list of named lists, potentially separated by other things
     *
     * @param motifs
     * @param index
     * @param name
     * @param parent
     * @param anyClass
     * @param descr
     * @return the part of the fingerprint indicated by index
     */
    public static OWLNamedClass createFingerprint(List<OWLNamedClass> motifs, int index,
                                                  String name, OWLNamedClass parent,
                                                  OWLNamedClass anyClass,
                                                  OWLListsPatternDescriptor descr) {

        final OWLModel model = descr.getModel();

        OWLNamedClass thisPart = (OWLNamedClass) model.createSubclass(name + getSuffix(index), parent);

        // option 1
        OWLIntersectionClass motifFollowedByNextPart = model.createOWLIntersectionClass();
        RDFResource nextPart = null;
        if (index + 1 < motifs.size()) {
            nextPart = createFingerprint(motifs, index + 1, name, parent, anyClass, descr);
        } else {
            nextPart = parent;
        }

        OWLSomeValuesFrom restNextFingerprintOrSequence =
                model.createOWLSomeValuesFrom(descr.getRestProperty(), nextPart);

        motifFollowedByNextPart.addOperand((OWLNamedClass) motifs.get(index));
        motifFollowedByNextPart.addOperand(restNextFingerprintOrSequence);

        // option 2
        OWLIntersectionClass aminoAcidFollowedBySelf = model.createOWLIntersectionClass();

        OWLSomeValuesFrom contentsAminoAcid =
                model.createOWLSomeValuesFrom(descr.getContentsProperty(), anyClass);

        OWLSomeValuesFrom nextSelf =
                model.createOWLSomeValuesFrom(descr.getNextProperty(), thisPart);

        aminoAcidFollowedBySelf.addOperand(contentsAminoAcid);
        aminoAcidFollowedBySelf.addOperand(nextSelf);

        OWLUnionClass options = model.createOWLUnionClass();
        options.addOperand(motifFollowedByNextPart);
        options.addOperand(aminoAcidFollowedBySelf);

        OWLIntersectionClass def = model.createOWLIntersectionClass();
        def.addOperand(parent);
        def.addOperand(options);

        thisPart.addEquivalentClass(def);

        return thisPart;
    }

    private static String getSuffix(int index) {
        return (index > 0) ? "_beforeMotif" + (index + 1) : "";
    }
}
