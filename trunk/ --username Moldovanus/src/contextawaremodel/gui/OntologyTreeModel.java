package contextawaremodel.gui;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class OntologyTreeModel extends DefaultTreeModel {

    private OWLModel owlModel;
    
    public OntologyTreeModel(OWLModel owlModel, DefaultMutableTreeNode rootNode) {
        super(rootNode);
        this.owlModel = owlModel;

        System.out.println( "[GUI] Generating ontology class tree.");
        RDFSNamedClass rootClass = owlModel.getRDFSNamedClass("context-element");
        rootNode.setUserObject(rootClass);
        addChildClasses(rootNode, rootClass);
    }

    private void addChildClasses(DefaultMutableTreeNode rootNode, RDFSNamedClass rootClass) {
        for ( Iterator<RDFSNamedClass> childClasses = rootClass.getNamedSubclasses().iterator(); childClasses.hasNext(); ) {
            RDFSNamedClass childClass = childClasses.next();
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childClass);
            addChildClasses(childNode, childClass);
            rootNode.add(childNode);
        }
    }

    private synchronized boolean addIndividual(DefaultMutableTreeNode node, RDFResource individual) {
        RDFSNamedClass c;
        try {
            c = (RDFSNamedClass) node.getUserObject();
        } catch (Exception e) {
            return false;
        }
        if (individual.getProtegeType().getName().equals(c.getName())) {
            DefaultMutableTreeNode individualNode = new DefaultMutableTreeNode(individual);
            Object[] properties = owlModel.getRDFProperties().toArray();
            for (int k = 0; k < properties.length; k++) {
                RDFProperty p = (RDFProperty) properties[k];
                if (individual.getPropertyValue(p) != null && !p.getLocalName().equals("type")) {
                    String propertyName = p.getName();
                    String propertyValue = individual.getPropertyValue(p).toString();
                    individualNode.add(new DefaultMutableTreeNode(propertyName + ": " + propertyValue));
                }
            }
            node.add(individualNode);
            return true;
        } else {
            for ( int i = 0; i < node.getChildCount(); i++ ) {
                boolean result = addIndividual((DefaultMutableTreeNode)node.getChildAt(i), individual);
                if ( result ) return true;
            }
            return false;
        }
    }

    public void addIndividual(String name) {
        RDFResource individual = owlModel.getRDFResource(name);
        addIndividual((DefaultMutableTreeNode) root, individual);
    }

}
