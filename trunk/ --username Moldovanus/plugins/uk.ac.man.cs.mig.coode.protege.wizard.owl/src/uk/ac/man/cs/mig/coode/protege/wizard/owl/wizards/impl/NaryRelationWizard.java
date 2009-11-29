package uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.impl;

import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import uk.ac.man.cs.mig.coode.protege.wizard.api.WizardAPI;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.editor.FrameTableCellEditorFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.component.matrix.renderer.FrameTableCellRendererFactory;
import uk.ac.man.cs.mig.coode.protege.wizard.exception.WizardInitException;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.component.matrix.PropRangeMatrixModel;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.OWLPatternDescriptor;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.pattern.impl.NaryRelation;
import uk.ac.man.cs.mig.coode.protege.wizard.owl.wizards.OWLPatternWizard;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.ListEntryPage;
import uk.ac.man.cs.mig.coode.protege.wizard.page.impl.MatrixPage;
import uk.ac.man.cs.mig.coode.protege.wizard.util.Console;
import uk.ac.man.cs.mig.coode.protege.wizard.validation.StringValidator;

import java.util.HashMap;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         28-Oct-2004
 */
public class NaryRelationWizard extends OWLPatternWizard {

    private static final String domainClsProp = "domainClsProp";
    private static final String attributePropertyNamesProp = "attributePropertyNamesProp";
    private static final String propRangeProp = "propRangeProp";
    private static final String propertyModelProp = "propertyModelProp";

    private ListEntryPage attributesPage;

    protected void initialise() throws NullPointerException, WizardInitException {
        super.initialise();

        addUniqueProperty(domainClsProp, null);
        addUniqueProperty(attributePropertyNamesProp, CollectionUtilities.createCollection("value"));
        addUniqueProperty(propRangeProp, CollectionUtilities.createCollection("range"));
        addUniqueProperty(propertyModelProp, new PropRangeMatrixModel(this, propRangeProp, attributePropertyNamesProp));

        StringValidator v = getDefaultValidator();
        attributesPage = new ListEntryPage("2", attributePropertyNamesProp, v, false,
                getOwlHelper().getNamespacePrefixes(true));
        addPage(attributesPage);

        addPage(new MatrixPage("3", propertyModelProp,
                new FrameTableCellRendererFactory(),
                new FrameTableCellEditorFactory(getPageProperty("3", "rangeselect"))));
    }

    protected void patternNameChanged(String newName) {
        String prefix = newName + "_";
        attributesPage.setPrefixes(CollectionUtilities.createCollection(prefix));
        attributesPage.setPrefixText(prefix);
    }

    protected String getDefaultName() {
        return null;
    }

    protected boolean handleFinished(Console output) // could return errors
    {
        PropRangeMatrixModel rangesModel = (PropRangeMatrixModel) getProperty(propertyModelProp);
        HashMap propHash = rangesModel.getMap();

        OWLPatternDescriptor descr = new OWLPatternDescriptor() {
            public OWLModel getModel() {
                return NaryRelationWizard.this.getModel();
            }
//
//      public String getName() {
//        return getPatternName();
//      }
//
//      public OWLNamedClass getParentClass() {
//        return getPatternParent();
//      }
        };

        NaryRelation pattern =
                new NaryRelation(descr,
                        getPatternName(), getPatternParent(),
                        (OWLNamedClass) getProperty(domainClsProp),
                        propHash,
                        WizardAPI.getWizardManager().getDefaultIdGenerator());

        if (pattern != null) {
            OWLUI.selectResource(pattern.getBaseClass(), null);
        }

        return pattern != null;
    }
}
