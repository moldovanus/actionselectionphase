package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl;

import edu.stanford.smi.protegex.owl.model.*;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardOptions;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLListsPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.util.OwlKbHelper;

import java.util.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         19-May-2005
 */
public class OWLList extends DefaultOWLPattern {

    private static final boolean debug = false;

    private OWLListsPatternDescriptor descr;

    /**
     * The list will be empty, regardless of the listElements that exist
     */
    public static final int MATCH_OPTION_NONE = -1;
    /**
     * The list will contain only the listElements given
     */
    public static final int MATCH_OPTION_EXACTLY = 0;
    /**
     * The list will start with the listElements given, but is open
     */
    public static final int MATCH_OPTION_STARTS = 1;
    /**
     * The list will end with the listElements given
     */
    public static final int MATCH_OPTION_ENDS = 2;
    /**
     * The list contains a sublist described by the listElements given
     */
    public static final int MATCH_OPTION_CONTAINS = 3;
    /**
     * The number of valid match options
     */
    public static final int NUM_MATCH_OPTIONS = 4;

    private static final String[] matchOptionStrings = new String[]{
            "exactly sequence...",
            "starts with sequence...",
            "ends with sequence...",
            "contains sequence..."};

    private static AbstractList<String> matchOptionStringsList;

    private static final boolean LIST_CLOSED = true;
    private static final boolean LIST_OPEN = false;
    private OWLNamedClass parent;

    public OWLList(OWLListsPatternDescriptor descr) {
        this(descr, null, null);
    }

    public OWLList(OWLListsPatternDescriptor descr, String name, OWLNamedClass parent) {
        super(descr);

        this.descr = descr;

        OWLModel model = descr.getModel();

        OWLNamedClass baseClass = model.createOWLNamedClass(name);

        setBaseClass(baseClass);

        setParent(parent);
    }

    public static OWLListsPatternDescriptor createDefaultListElements(OWLModel owlModel) {
        return DefaultOWLListsPatternDescriptor.getDescriptor(owlModel);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // below are all used for creating the various restrictions

    public boolean addStartsWith(List<OWLClass> elements) {
        OWLNamedClass base = getBaseClass();
        base.addSuperclass(createStartsWith(elements, LIST_OPEN));
        return true;
    }

    public boolean addExactly(List<OWLClass> elements) {
        OWLNamedClass base = getBaseClass();
        base.addSuperclass(createStartsWith(elements, LIST_CLOSED));
        return true;
    }

    public boolean addContains(List<OWLClass> elements) {
        OWLNamedClass base = getBaseClass();
        base.addSuperclass(createContainsRestriction(elements, LIST_OPEN));
        return true;
    }

    public boolean addEndsWith(List<OWLClass> elements) {
        OWLNamedClass base = getBaseClass();
        base.addSuperclass(createEndsInRestriction(elements));
        return true;
    }

    public boolean addConstrainContents(List<OWLClass> elementTypes) {
        OWLNamedClass base = getBaseClass();
        base.addSuperclass(createConstrainContentsRestriction(elementTypes));
        return true;
    }

    public boolean addStartsWith(OWLClass elementType) {
        return addStartsWith(Collections.singletonList(elementType));
    }

    public boolean addExactly(OWLClass elementType) {
        return addExactly(Collections.singletonList(elementType));
    }

    public boolean addContains(OWLClass elementType) {
        return addContains(Collections.singletonList(elementType));
    }

    public boolean addEndsWith(OWLClass elementType) {
        return addEndsWith(Collections.singletonList(elementType));
    }

    public boolean addConstrainContents(OWLClass elementType) {
        return addConstrainContents(Collections.singletonList(elementType));
    }

    /**
     * Add a set of conditions that correspond to the current list and the given match option
     *
     * @param matchOption defined by OWLList.MATCH_OPTION_... constants - eg MATCH_OPTION_EXACTLY
     */
    public boolean addListConditions(List<OWLClass> elements, int matchOption) {

        boolean result = false;

        int numElements = elements.size();

        String comment = null;

        boolean generateComments = WizardAPI.getWizardManager().getUserOptions().getBoolean(WizardOptions.OPTION_GENERATE_LISTS_DESCR);

        if (numElements > 0) {
            switch (matchOption) {
                case MATCH_OPTION_EXACTLY:
                    result = addExactly(elements);
                    break;
                case MATCH_OPTION_STARTS:
                    result = addStartsWith(elements);
                    break;
                case MATCH_OPTION_ENDS:
                    result = addEndsWith(elements);
                    break;
                case MATCH_OPTION_CONTAINS:
                    result = addContains(elements);
                    break;
                case MATCH_OPTION_NONE:
                default:
                    System.err.println("OWL List option " + matchOptionStrings[matchOption] + " NOT YET IMPLEMENTED");
            }

            if (generateComments) {
                comment = "A list that " + getStringForMatchOption(matchOption) + makeReadable(elements);
            }
        } else {
            getBaseClass().addSuperclass(createContentsRestriction(null));
            if (generateComments) {
                comment = "An empty list";
            }
        }

        if (comment != null) {
            OwlKbHelper helper = OwlKbHelper.getHelper(descr.getModel());
            helper.addClassAnnotation(getBaseClass(), descr.getModel().getRDFSCommentProperty(), comment, null);
        }

        return result;
    }

    //////////////////////////////////////////////////////////////////////////////

    private OWLRestriction createConstrainContentsRestriction(List<OWLClass> elementTypes) {
        OWLClass filler;
        if (elementTypes.size() > 1) {
            filler = getModel().createOWLIntersectionClass(elementTypes);
        } else {
            filler = elementTypes.get(0);
        }
        return getModel().createOWLAllValuesFrom(descr.getNextProperty(), filler);
    }

    private OWLClass createStartsWith(List<OWLClass> elements, boolean closed) {

        OWLClass axiom;

        if (!closed && elements.size() == 1) {
            axiom = createContentsRestriction((RDFResource) elements.get(0));
        } else {
            Collection<OWLRestriction> c = new ArrayList<OWLRestriction>(2);
            c.add(createContentsRestriction((RDFResource) elements.get(0)));
            c.add(createRemainderRestriction(elements, 1, LIST_CLOSED, closed));
            axiom = descr.getModel().createOWLIntersectionClass(c);
        }

        return axiom;
    }

    private OWLClass createContainsRestriction(List<OWLClass> elements, boolean closed) {
        Collection<OWLClass> parts = new ArrayList<OWLClass>(2);
        parts.add(createStartsWith(elements, closed));
        parts.add(createRemainderRestriction(elements, 0, LIST_OPEN, closed));
        return descr.getModel().createOWLUnionClass(parts);
    }

    private OWLClass createEndsInRestriction(List<OWLClass> elements) {
        return createContainsRestriction(elements, LIST_CLOSED);
    }

    private OWLRestriction createContentsRestriction(RDFResource content) {
        OWLRestriction restr;
        if (content == null) {
            if (debug) System.out.println("Creating empty list");
            restr = descr.getModel().createOWLMaxCardinality(descr.getContentsProperty(), 0);
        } else {
            if (debug) System.out.println("adding first element: " + content);
            restr = descr.getModel().createOWLSomeValuesFrom(descr.getContentsProperty(), content);
        }
        return restr;
    }

    private OWLRestriction createRemainderRestriction(List<OWLClass> all, int startindex, boolean startClosed, boolean endClosed) {
        OWLModel okb = descr.getModel();
        OWLRestriction restr = null;
        RDFResource fillerPointer = null;
        OWLProperty contentsProp = descr.getContentsProperty();
        OWLObjectProperty nextProp = descr.getNextProperty();
        OWLObjectProperty restProp = descr.getRestProperty();
        Collection<OWLClass> element = new ArrayList<OWLClass>(3);
        int size = all.size();

        // if this is a closed list, add a nil to the end
        if (endClosed) {
            fillerPointer = descr.getEmptyListClass();
        }

        // work backwards throught the list to avoid recursion
        for (int i = size - 1; i >= startindex; i--) {

            if ((size > 30) && (i % 10 == 0)) {
                System.out.println("LIST: " + i + " elements still to make");
            }

            element.clear();
            element.add(parent);

            RDFSClass current = (RDFSClass) all.get(i);

            // add this element's contents
            OWLSomeValuesFrom contents = okb.createOWLSomeValuesFrom(contentsProp, current);

            element.add(contents);

            // the fillerpointer will be null if this is the last element and the list is open
            if (fillerPointer != null) {// add the rest of the list
                element.add(okb.createOWLSomeValuesFrom(nextProp, fillerPointer));
            }

            fillerPointer = okb.createOWLIntersectionClass(element);
        }

        if (fillerPointer != null) {
            if (startClosed) {
                restr = okb.createOWLSomeValuesFrom(nextProp, fillerPointer);
            } else {
                restr = okb.createOWLSomeValuesFrom(restProp, fillerPointer);
            }
        }

        return restr;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // below are all used for presentation of the match options to the user

    public static String getStringForMatchOption(int matchOption) {
        return matchOptionStrings[matchOption];
    }

    public static int getMatchOptionForString(String s) {
        return getMatchOptionStrings().indexOf(s);
    }

    public static AbstractList<String> getMatchOptionStrings() {
        if (matchOptionStringsList == null) {
            matchOptionStringsList = new ArrayList<String>(NUM_MATCH_OPTIONS);
            for (int i = 0; i < NUM_MATCH_OPTIONS; i++) {
                matchOptionStringsList.add(matchOptionStrings[i]);
            }
        }
        return matchOptionStringsList;
    }


    private String makeReadable(List<OWLClass> elements) {
        StringBuffer string = new StringBuffer("(");
        for (Iterator<OWLClass> i = elements.iterator(); i.hasNext();) {
            OWLClass cls = i.next();
            string.append(cls.getBrowserText());
            if (i.hasNext()) {
                string.append(", ");
            }
        }
        string.append(")");
        return string.toString();
    }

    ////////////////////////////////////////////////////////////

    protected OWLNamedClass getPatternParentClass() {
        return parent;
    }

    private void setParent(OWLNamedClass parent) {
        if (parent != null) {
            OWLNamedClass base = getBaseClass();
            Iterator currentSuperclasses = base.getSuperclasses(false).iterator();
            this.parent = parent;
            base.addSuperclass(parent);

            while (currentSuperclasses.hasNext()) {
                OWLClass current = (OWLClass) currentSuperclasses.next();
                if (!(current == parent)) {
                    base.removeSuperclass(current);
                }
            }
        } else {
            this.parent = getModel().getOWLThingClass();
        }
    }


    /**
     * Currently just remove all restrictions on the class - needs to be implemented mopre carefully
     * to check whether these restrictions are part of the list description and not added by the
     * user afterwards.
     */
    private void removeListConditions() {
        OWLNamedClass base = getBaseClass();
        Iterator supers = base.getSuperclasses(true).iterator();
        while (supers.hasNext()) {
            Object currentSuper = supers.next();
            if (currentSuper instanceof OWLRestriction) {
                if (debug) System.out.println("Removing existing conditions");
                base.removeSuperclass((RDFSClass) currentSuper);
            }
        }
        base.setPropertyValues(descr.getModel().getRDFSCommentProperty(), new ArrayList());
    }
}