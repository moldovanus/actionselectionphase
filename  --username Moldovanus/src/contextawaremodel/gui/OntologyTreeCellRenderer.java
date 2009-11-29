package contextawaremodel.gui;

import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class OntologyTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        Object object = ((DefaultMutableTreeNode)value).getUserObject();
        if (leaf && !(object instanceof RDFSNamedClass)) {
            setIcon(OWLIcons.getPropertiesIcon());
            setText(value.toString());
        } else if (object instanceof RDFSNamedClass) {
            setIcon(OWLIcons.getClassesIcon());
            setText(((RDFSNamedClass) object).getName());
        } else if (object instanceof DefaultOWLIndividual) {
            setIcon(OWLIcons.getImageIcon(OWLIcons.RDF_INDIVIDUAL));
            setText(((DefaultOWLIndividual)object).getName());
        } else {
            System.out.println(object.getClass().getName());
        }

        return this;
    }


}
