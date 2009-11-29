package uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.test;

import uk.ac.man.cs.mig.coode.protege.wizard.owl.test.AbstractOWLWizardTestCase;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Mar-2006
 */
public class OWLListTestCase extends AbstractOWLWizardTestCase {

    private static String filePath = "file:///list-test.owl";
    private static URI file;
    private static boolean testSaveAndLoad = true;

    {
        try {
            file = new URI(filePath);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

//    public void testListEndsWith() {
//        OWLNamedClass A = owlModel.createOWLNamedClass("A");
//        OWLNamedClass B = owlModel.createOWLNamedClass("B");
//
//        OWLListsPatternDescriptor descr = OWLList.createDefaultListElements(owlModel);
//
//        List<OWLClass> elements = new ArrayList<OWLClass>();
//
//        // 1 element
//        OWLList list_ends_A = new OWLList(descr);
//        elements.add(A);
//        list_ends_A.addEndsWith(elements);
//
//        // 2 elements
//        OWLList list_ends_AB = new OWLList(descr);
//        elements.add(B);
//        list_ends_AB.addEndsWith(elements);
//
//        // 6 elements
//        OWLList list_ends_ABABAB = new OWLList(descr);
//        elements.add(A);
//        elements.add(B);
//        elements.add(A);
//        elements.add(B);
//        list_ends_ABABAB.addEndsWith(elements);
//
//        if (testSaveAndLoad) {
//            saveAndLoad();
//        }
//    }
//
//    public void testListExactly() {
//        OWLNamedClass A = owlModel.createOWLNamedClass("A");
//        OWLNamedClass B = owlModel.createOWLNamedClass("B");
//
//        OWLListsPatternDescriptor descr = OWLList.createDefaultListElements(owlModel);
//
//        List<OWLClass> elements = new ArrayList<OWLClass>();
//
//        // 1 element
//        OWLList list_exactly_A = new OWLList(descr);
//        elements.add(A);
//        list_exactly_A.addExactly(elements);
//
//        // 2 elements
//        OWLList list_exactly_AB = new OWLList(descr);
//        elements.add(B);
//        list_exactly_AB.addExactly(elements);
//
//        // 6 elements
//        OWLList list_exactly_ABABAB = new OWLList(descr);
//        elements.add(A);
//        elements.add(B);
//        elements.add(A);
//        elements.add(B);
//        list_exactly_ABABAB.addExactly(elements);
//
//        if (testSaveAndLoad) {
//            saveAndLoad();
//        }
//    }
//
//    public void testListContains() {
//        OWLNamedClass A = owlModel.createOWLNamedClass("A");
//        OWLNamedClass B = owlModel.createOWLNamedClass("B");
//
//        OWLListsPatternDescriptor descr = OWLList.createDefaultListElements(owlModel);
//
//        List<OWLClass> elements = new ArrayList<OWLClass>();
//
//        // 1 element
//        OWLList list_contains_A = new OWLList(descr);
//        elements.add(A);
//        list_contains_A.addContains(elements);
//
//        // 2 elements
//        OWLList list_contains_AB = new OWLList(descr);
//        elements.add(B);
//        list_contains_AB.addContains(elements);
//
//        // 6 elements
//        OWLList list_contains_ABABAB = new OWLList(descr);
//        elements.add(A);
//        elements.add(B);
//        elements.add(A);
//        elements.add(B);
//        list_contains_ABABAB.addContains(elements);
//
//        if (testSaveAndLoad) {
//            saveAndLoad();
//        }
//    }
//
//    public void testListStartsWith() {
//        OWLNamedClass A = owlModel.createOWLNamedClass("A");
//        OWLNamedClass B = owlModel.createOWLNamedClass("B");
//
//        OWLListsPatternDescriptor descr = OWLList.createDefaultListElements(owlModel);
//
//        List<OWLClass> elements = new ArrayList<OWLClass>();
//
//        // 1 element
//        OWLList list_starts_A = new OWLList(descr);
//        elements.add(A);
//        list_starts_A.addStartsWith(elements);
//
//        // 2 elements
//        OWLList list_starts_AB = new OWLList(descr);
//        elements.add(B);
//        list_starts_AB.addStartsWith(elements);
//
//        // 6 elements
//        OWLList list_starts_ABABAB = new OWLList(descr);
//        elements.add(A);
//        elements.add(B);
//        elements.add(A);
//        elements.add(B);
//        list_starts_ABABAB.addStartsWith(elements);
//
//        if (testSaveAndLoad) {
//            saveAndLoad();
//        }
//    }
//
//    private void saveAndLoad() {
//        try {
//            owlModel.save(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Could not save");
//        }
//
//        try {
//            ProtegeOWL.createJenaOWLModelFromURI(filePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Could not load");
//        }
//    }
}
