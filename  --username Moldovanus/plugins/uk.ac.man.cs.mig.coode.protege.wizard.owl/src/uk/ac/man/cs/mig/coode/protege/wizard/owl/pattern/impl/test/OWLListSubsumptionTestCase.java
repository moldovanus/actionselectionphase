package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.test;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.ui.cls.ConvertToDefinedClassAction;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelAllTripleStoresWriter;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         06-Mar-2006
 */
public class OWLListSubsumptionTestCase extends TestCase {

    private String filename = "test-list-subsumption.owl";

    private String fact = "3490"; // pass if primitive's parent != owl:Thing  (build 301)
    private String pellet = "8081"; // always fails EndsWith (build 301)
    private String racer = "8080"; // PASS racer 1_7_24 and Pro (build 301)

    private String reasonerURL = "http://localhost:" + pellet;

    ProtegeOWLReasoner reasoner;

    private TestListOntology ont;

    public void testEndsWithSingleElement() throws DIGReasonerException {
        OWLClass listEndsA = ont.createDefinedList("listEndsA", null, ont.A_singleton, null, null);

        assertSubsumes(listEndsA, ont.prim_abba);
        assertSubsumes(listEndsA, ont.prim_abcdcba);
        assertNotSubsumes(listEndsA, ont.prim_abcdabcd);
    }

    public void testEndsWith() throws DIGReasonerException {
        OWLClass listEndsBA = ont.createDefinedList("listEndsBA", null, ont.BA, null, null);

        assertSubsumes(listEndsBA, ont.prim_abba);
        assertSubsumes(listEndsBA, ont.prim_abcdcba);
        assertNotSubsumes(listEndsBA, ont.prim_cabc);
        assertNotSubsumes(listEndsBA, ont.prim_caca);
        assertNotSubsumes(listEndsBA, ont.prim_abcdbadc);
    }

    public void testEndsWithMinMatch() throws DIGReasonerException {
        OWLClass listEndsABCD = ont.createDefinedList("listEndsABCD", null, ont.ABCD, null, null);

        assertSubsumes(listEndsABCD, ont.prim_abcd);
        assertNotSubsumes(listEndsABCD, ont.prim_abba);
    }

    public void testContainsSingleElement() throws DIGReasonerException {
        OWLClass listContainsC = ont.createDefinedList("listContainsC", null, null, ont.C_singleton, null);
        assertSubsumes(listContainsC, ont.prim_abcd); // once
        assertSubsumes(listContainsC, ont.prim_abcdcba); // twice
        assertSubsumes(listContainsC, ont.prim_caba); // starts
        assertSubsumes(listContainsC, ont.prim_bbc); //ends
        assertNotSubsumes(listContainsC, ont.prim_abba); // not
    }

    public void testContains() throws DIGReasonerException {
        OWLClass listContainsBC = ont.createDefinedList("listContainsBC", null, null, ont.BC, null);
        assertSubsumes(listContainsBC, ont.prim_abcd);
        assertSubsumes(listContainsBC, ont.prim_abcdcba);
        assertSubsumes(listContainsBC, ont.prim_bbc);
        assertNotSubsumes(listContainsBC, ont.prim_abba);
    }

    public void testContainsMinMatch() throws DIGReasonerException {
        OWLClass listContainsABCD = ont.createDefinedList("listContainsABCD", null, null, ont.ABCD, null);
        assertSubsumes(listContainsABCD, ont.prim_abcd);
        assertSubsumes(listContainsABCD, ont.prim_abcdabcd);
        assertNotSubsumes(listContainsABCD, ont.prim_abba);
        assertNotSubsumes(listContainsABCD, ont.prim_caba);
    }

    public void testStartsWithSingleElement() throws DIGReasonerException {
        OWLClass listStartsA = ont.createDefinedList("listStartsA", ont.A_singleton, null, null, null);
        assertSubsumes(listStartsA, ont.prim_abcd);
        assertSubsumes(listStartsA, ont.prim_abcdcba);
        assertSubsumes(listStartsA, ont.prim_abba);
        assertNotSubsumes(listStartsA, ont.prim_baba);
    }

    public void testStartsWith() throws DIGReasonerException {
        OWLClass listStartsABCD =
                ont.createDefinedList("ListStartsABCD", ont.ABCD, null, null, null);
        assertSubsumes(listStartsABCD, ont.prim_abcdcba);
        assertNotSubsumes(listStartsABCD, ont.prim_abba);
        assertNotSubsumes(listStartsABCD, ont.prim_baba);
    }

    public void testStartsWithMinMatch() throws DIGReasonerException {
        OWLClass listStartsABBA =
                ont.createDefinedList("ListStartsABBA", ont.ABBA, null, null, null);
        assertSubsumes(listStartsABBA, ont.prim_abba);
    }

    public void testExactlySingleElement() throws DIGReasonerException {
        OWLClass listExactlyA =
                ont.createDefinedList("listExactlyA", null, null, null, ont.A_singleton);
        assertSubsumes(listExactlyA, ont.prim_a);
        assertNotSubsumes(listExactlyA, ont.prim_abcdcba);
        assertNotSubsumes(listExactlyA, ont.prim_abcdabcd);
        assertNotSubsumes(listExactlyA, ont.prim_baba);
    }

    public void testExactlySubsumption() throws DIGReasonerException {
        OWLClass listExactlyABCD = ont.createDefinedList("listExactlyABCD", null, null, null, ont.ABCD);
        assertSubsumes(listExactlyABCD, ont.prim_abcd);
        assertNotSubsumes(listExactlyABCD, ont.prim_abcdcba);
        assertNotSubsumes(listExactlyABCD, ont.prim_abcdabcd);
        assertNotSubsumes(listExactlyABCD, ont.prim_baba);
    }


    public void testStartsAndEndsWith() throws DIGReasonerException {
        OWLClass listStartsABEndsBA = ont.createDefinedList("listStartsABEndsBA", ont.AB, ont.BA, null, null);
        assertSubsumes(listStartsABEndsBA, ont.prim_abba);
        assertSubsumes(listStartsABEndsBA, ont.prim_abcdcba);
        assertNotSubsumes(listStartsABEndsBA, ont.prim_abcdabcd);
        assertNotSubsumes(listStartsABEndsBA, ont.prim_baba);
    }


    public void testStartsAndContains() throws DIGReasonerException {
        OWLClass listStartsABContainsCD =
                ont.createDefinedList("listStartsABContainsCD", ont.AB, null, ont.CD, null);
        assertNotSubsumes(listStartsABContainsCD, ont.prim_abba);
        assertSubsumes(listStartsABContainsCD, ont.prim_abcdcba);
        assertSubsumes(listStartsABContainsCD, ont.prim_abcdabcd);
        assertNotSubsumes(listStartsABContainsCD, ont.prim_baba);
    }


    public void testStartsAndContainsOverlap() throws DIGReasonerException {
        OWLClass listStartsABContainsBC =
                ont.createDefinedList("listStartsABContainsBC", ont.AB, null, ont.BC, null);
        assertSubsumes(listStartsABContainsBC, ont.prim_abcd);
        assertSubsumes(listStartsABContainsBC, ont.prim_abcdcba);
        assertNotSubsumes(listStartsABContainsBC, ont.prim_abba);
    }


    public void testStartsAndContainsWeirdOverlap() throws DIGReasonerException {

        OWLClass listStartsABContainsBC = ont.getOWLModel().getOWLNamedClass("listStartsABContainsBC");
        if (listStartsABContainsBC == null) {
            listStartsABContainsBC = ont.createDefinedList("listStartsABContainsBC", ont.AB, null, ont.BC, null);
        }
        OWLClass listStartsABContainsC =
                ont.createDefinedList("listStartsABContainsC", ont.AB, null, ont.C_singleton, null);
        assertSubsumes(listStartsABContainsC, listStartsABContainsBC);
    }


    public void testClosedLists() throws DIGReasonerException {
        OWLClass emptyList = ont.getDescr().getEmptyListClass();
        RDFProperty restProp = ont.getDescr().getRestProperty();
        OWLNamedClass closedList = ont.getOWLModel().createOWLNamedClass("closedList");
        OWLSomeValuesFrom restEmpty = ont.getOWLModel().createOWLSomeValuesFrom(restProp, emptyList);
        closedList.addSuperclass(restEmpty);
        ConvertToDefinedClassAction.performAction(closedList);

        // all primitive classes here are closed
        Collection inferredSubs = reasoner.getDescendantClasses(closedList, null);
        Iterator<OWLClass> prims = ont.getPrimitiveLists().iterator();
        while (prims.hasNext()) {
            OWLClass prim = prims.next();
            try {
                assertContains(prim, inferredSubs);
            }
            catch (AssertionFailedError e) {
                fail("primitive: " + prim.getBrowserText());
            }
        }
    }

    public void testPrimitiveListsSubsumedByOWLList() throws DIGReasonerException {
        if (reasoner.isConnected()) {
            OWLClass listclass = ont.getDescr().getListClass();
            Collection inferredSubs = reasoner.getDescendantClasses(listclass, null);
            Iterator<OWLClass> prims = ont.getPrimitiveLists().iterator();
            while (prims.hasNext()) {
                assertContains(prims.next(), inferredSubs);
            }
        }
    }

    public void testDefinedListsSubsumedByOWLList() throws DIGReasonerException {
        if (reasoner.isConnected()) {
            OWLClass listclass = ont.getDescr().getListClass();
            Collection inferredSubs = reasoner.getDescendantClasses(listclass, null);
            Iterator<OWLClass> defs = ont.getDefinedLists().iterator();
            while (defs.hasNext()) {
                assertContains(defs.next(), inferredSubs);
            }
        }
    }


    public void testListOf250Elements() throws DIGReasonerException {


        if (reasoner.isConnected()) {
            OWLNamedClass longList = ont.createListOfLength(250, "longList250");
            OWLClass listStartsA = ont.getOWLModel().getOWLNamedClass("listStartsA");
            if (listStartsA == null) {
                listStartsA = ont.createDefinedList("listStartsA", ont.A_singleton, null, null, null);
            }
            OWLClass listEndsA = ont.getOWLModel().getOWLNamedClass("listEndsA");
            if (listEndsA == null) {
                listEndsA = ont.createDefinedList("listEndsA", null, ont.A_singleton, null, null);
            }
            assertSubsumes(listStartsA, longList);
            assertSubsumes(listEndsA, longList);
        }

        try {
            URI fileURI = new URI("file:///longlist.owl");
            OWLModelAllTripleStoresWriter writer = new OWLModelAllTripleStoresWriter(ont.getOWLModel(), fileURI, false);
            writer.write();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //ont.save("C:\\longlist.owl");
    }

    public void testSaveOntology() {
        ont.save(filename);
    }

    ////////////////// end of tests ////////////////////


    private void assertNotSubsumes(OWLClass def, OWLClass prim) throws DIGReasonerException {
        if (reasoner.isConnected()) {
            Collection<OWLClass> inferredSubs = inferredSubs = reasoner.getDescendantClasses(def, null);
            assertContainsNot(prim, inferredSubs.iterator());
        }
        else {
            fail("No reasoner detected on " + reasonerURL);
        }
    }

    private void assertSubsumes(OWLClass def, OWLClass prim) throws DIGReasonerException {
        if (reasoner.isConnected()) {
            Collection<OWLClass> inferredSubs = reasoner.getDescendantClasses(def, null);
            assertContains(prim, inferredSubs);
        }
        else {
            fail("No reasoner detected on " + reasonerURL);
        }
    }

    public static void assertContains(Object value, Collection collection) {
        assertTrue(collection.contains(value));
    }


    public static void assertContains(Object value, Iterator it) {
        assertTrue(Jena.set(it).contains(value));
    }


    public static void assertContainsNot(Object value, Iterator it) {
        assertFalse(Jena.set(it).contains(value));
    }

    protected void setUp() throws Exception {
        super.setUp();
        ont = TestListOntology.createInstance();
        reasoner = ReasonerManager.getInstance().createReasoner(ont.getOWLModel());
        reasoner.setURL(reasonerURL);
    }
}
