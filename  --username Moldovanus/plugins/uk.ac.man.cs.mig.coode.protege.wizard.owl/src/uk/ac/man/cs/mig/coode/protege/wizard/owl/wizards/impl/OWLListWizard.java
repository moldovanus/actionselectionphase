package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.undo.UndoFrameStore;
import edu.stanford.smi.protegex.owl.ui.cls.ConvertToDefinedClassAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLListsPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.MetaListsPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.OWLList;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.OWLPatternWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.MultiSelectPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.OptionsPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Feb-2005
 */
public class OWLListWizard extends OWLPatternWizard {

    private static final String modOptionProp = "modOptionProp";
    private static final String elementsProp = "elementsProp";
    private static final String definedProp = "definedProp";
    public static final String disableUndoProp = "disableUndoProp";

    public static final String PRIM_CLASS_STR = "primitive class";
    public static final String DEF_CLASS_STR = "defined class";

    public static FrameStore undoFrameStore = null;
    private String leaveUndo;
    private String disableUndo;

    protected void initialise() throws NullPointerException, WizardInitException {
        super.initialise();

        addProperty(modOptionProp, OWLList.getStringForMatchOption(OWLList.MATCH_OPTION_EXACTLY));
        addProperty(elementsProp, null);

        MultiSelectPage listElementsPage = new MultiSelectPage("3", elementsProp, getModel().getOWLThingClass());
        listElementsPage.setDuplicateValuesAllowed(true);
        addPage(listElementsPage);

        addPage(new OptionsPage("4", modOptionProp, OWLList.getMatchOptionStrings()));

        AbstractList primDefOptions = new ArrayList(2);
        primDefOptions.add(DEF_CLASS_STR);
        primDefOptions.add(PRIM_CLASS_STR);
        addPage(new OptionsPage("5", definedProp, primDefOptions));

        if (undoFrameStore == null) {
            leaveUndo = getPageProperty("6", "option1");
            disableUndo = getPageProperty("6", "option2");
            addProperty(definedProp, PRIM_CLASS_STR);
            addProperty(disableUndoProp, leaveUndo);
            List options = new ArrayList(2);
            options.add(leaveUndo);
            options.add(disableUndo);
            addPage(new OptionsPage("6", disableUndoProp, options));
        }
    }

    protected void patternNameChanged(String newName) {
        // do nothing
    }

    protected String getDefaultName() {
        return getWizardProperty("default.list.name");
    }

    protected boolean handleFinished(Console output) {
        boolean result = false;
        try {
            String disableUndoOpt = (String) getProperty(disableUndoProp);

            // currently disable the UNDO framestore if the user requests it
            // otherwise the operation takes 100x longer and potentially
            // causes a StackOverflow
            if (disableUndoOpt.equals(disableUndo)) {
                List fstores = ((KnowledgeBase) getModel()).getFrameStores();
                for (Iterator i = fstores.iterator(); i.hasNext();) {
                    FrameStore f = (FrameStore) i.next();
                    if (f instanceof UndoFrameStore) {
                        System.err.println("DISABLING UNDO FRAMESTORE ... remember to restart");
                        undoFrameStore = f;
                    }
                }
                ((KnowledgeBase) getModel()).removeFrameStore(undoFrameStore);
            }

            OWLListsPatternDescriptor descr;

            if (isMetaActive()) {
                descr = new MetaListsPatternDescriptor(getModel());
            }
            else {
                descr = OWLList.createDefaultListElements(getModel());
            }

            OWLList listClass = new OWLList(descr, getPatternName(), getPatternParent());

            List elements = (List) getProperty(elementsProp);
            output.out("Number of elements in list: " + elements.size());
            int matchType = OWLList.getMatchOptionForString((String) getProperty(modOptionProp));
            listClass.addListConditions(elements, matchType);

            String primDefOpt = (String) getProperty(definedProp);
            if (primDefOpt.equals(DEF_CLASS_STR)) {
                ConvertToDefinedClassAction.performAction(listClass.getBaseClass());
            }

            OWLUI.selectResource(listClass.getBaseClass(), null);

            result = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
