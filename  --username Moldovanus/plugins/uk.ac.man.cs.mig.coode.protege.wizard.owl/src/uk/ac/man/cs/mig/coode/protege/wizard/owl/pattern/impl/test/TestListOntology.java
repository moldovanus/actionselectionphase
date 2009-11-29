package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.test;

import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.cls.ConvertToDefinedClassAction;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLListsPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.OWLList;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.test.TestOntology;

import java.util.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         09-Mar-2006
 */
public class TestListOntology extends TestOntology {

    private static TestListOntology instance;

    private Collection primitiveLists = new ArrayList();
    private Collection definedLists = new ArrayList();

    OWLNamedClass A;
    OWLNamedClass B;
    OWLNamedClass C;
    OWLNamedClass D;

    List<OWLClass> A_singleton;
    List<OWLClass> C_singleton;
    List<OWLClass> ABCD;
    List<OWLClass> ABBA;
    List<OWLClass> BABA;
    List<OWLClass> CABA;
    List<OWLClass> CABC;
    List<OWLClass> CACA;
    List<OWLClass> ABCDCBA;
    List<OWLClass> ABCDABCD;
    List<OWLClass> ABCDBADC;
    List<OWLClass> BBC;
    List<OWLClass> AB;
    List<OWLClass> BC;
    List<OWLClass> CD;
    List<OWLClass> BA;

    // primitives
    OWLNamedClass prim_a;
    OWLNamedClass prim_c;
    OWLNamedClass prim_bbc;
    OWLNamedClass prim_abcd;
    OWLNamedClass prim_abba;
    OWLNamedClass prim_baba;
    OWLNamedClass prim_caba;
    OWLNamedClass prim_cabc;
    OWLNamedClass prim_caca;
    OWLNamedClass prim_abcdcba;
    OWLNamedClass prim_abcdabcd;
    OWLNamedClass prim_abcdbadc;

    public OWLNamedClass createPrimitiveList(List<OWLClass> elements) {
        String name = "prim_";
        for (Iterator<OWLClass> i = elements.iterator(); i.hasNext();) {
            name += i.next().getBrowserText();
        }
        OWLList owlList = new OWLList(descr, name, owlModel.getOWLThingClass());
        owlList.addExactly(elements);
        primitiveLists.add(owlList.getBaseClass());
        return owlList.getBaseClass();
    }

    public OWLNamedClass createDefinedList(String name,
                                           List<OWLClass> starts,
                                           List<OWLClass> ends,
                                           List<OWLClass> contains,
                                           List<OWLClass> exactly) {
        OWLList testList = new OWLList(descr, name, null);
        if (starts != null) testList.addStartsWith(starts);
        if (ends != null) testList.addEndsWith(ends);
        if (contains != null) testList.addContains(contains);
        if (exactly != null) testList.addExactly(exactly);
        ConvertToDefinedClassAction.performAction(testList.getBaseClass());
        definedLists.add(testList.getBaseClass());
        return testList.getBaseClass();
    }

    public static TestListOntology createInstance() {
        if (instance == null) {
            instance = new TestListOntology();
        }
        return instance;
    }

    protected TestListOntology() {
        super();

        OWLNamedClass elementValue = owlModel.createOWLNamedClass("ElementValue");

        A = owlModel.createOWLNamedSubclass("A", elementValue);
        B = owlModel.createOWLNamedSubclass("B", elementValue);
        C = owlModel.createOWLNamedSubclass("C", elementValue);
        D = owlModel.createOWLNamedSubclass("D", elementValue);

        OWLUtil.ensureSubclassesDisjoint(elementValue);

        A_singleton = Collections.singletonList((OWLClass) A);
        C_singleton = Collections.singletonList((OWLClass) C);
        AB = Arrays.asList(new OWLClass[]{A, B});
        BC = Arrays.asList(new OWLClass[]{B, C});
        CD = Arrays.asList(new OWLClass[]{C, D});
        BA = Arrays.asList(new OWLClass[]{B, A});
        BBC = Arrays.asList(new OWLClass[]{B, B, C});
        ABCD = Arrays.asList(new OWLClass[]{A, B, C, D});
        ABBA = Arrays.asList(new OWLClass[]{A, B, B, A});
        BABA = Arrays.asList(new OWLClass[]{B, A, B, A});
        CABA = Arrays.asList(new OWLClass[]{C, A, B, A});
        CABC = Arrays.asList(new OWLClass[]{C, A, B, C});
        CACA = Arrays.asList(new OWLClass[]{C, A, C, A});
        ABCDCBA = Arrays.asList(new OWLClass[]{A, B, C, D, C, B, A});
        ABCDABCD = Arrays.asList(new OWLClass[]{A, B, C, D, A, B, C, D});
        ABCDBADC = Arrays.asList(new OWLClass[]{A, B, C, D, B, A, D, C});

        ////// primitives
        prim_a = createPrimitiveList(A_singleton);
        prim_c = createPrimitiveList(C_singleton);
        prim_bbc = createPrimitiveList(BBC);
        prim_abcd = createPrimitiveList(ABCD);
        prim_abba = createPrimitiveList(ABBA);
        prim_baba = createPrimitiveList(BABA);
        prim_caba = createPrimitiveList(CABA);
        prim_cabc = createPrimitiveList(CABC);
        prim_caca = createPrimitiveList(CACA);
        prim_abcdcba = createPrimitiveList(ABCDCBA);
        prim_abcdabcd = createPrimitiveList(ABCDABCD);
        prim_abcdbadc = createPrimitiveList(ABCDBADC);
    }

    public OWLModel getOWLModel() {
        return owlModel;
    }

    public OWLListsPatternDescriptor getDescr() {
        return descr;
    }

    public Collection<OWLClass> getPrimitiveLists() {
        return primitiveLists;
    }

    public Collection<OWLClass> getDefinedLists() {
        return definedLists;
    }

    public OWLNamedClass createListOfLength(int len, String name) {
        ArrayList list = new ArrayList(len);
        while (len-- > 0) {
            list.add(A);
        }
        OWLNamedClass listClass = createPrimitiveList(list);
        listClass = (OWLNamedClass) listClass.rename(name);
        return listClass;
    }
}