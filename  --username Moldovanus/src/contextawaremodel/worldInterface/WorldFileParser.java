package contextawaremodel.worldInterface;

import contextawaremodel.GlobalVars;
import contextawaremodel.WorldFileValidationException;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class WorldFileParser {

    private WorldFileValidator wf;
    private File wFile;
    private long lastChange;

    public WorldFileParser () {
        wf = new WorldFileValidator();
        wFile = new File(GlobalVars.WORLD_FILE);
        lastChange = 0;
    }

    //return True if World File was changed
    public boolean wasChanged () {
        if (lastChange != wFile.lastModified()) {
            this.lastChange = wFile.lastModified();
            return true;
        } else {
            return false;
        }
    }

    public WorldElement[] getWorld () {
        //first validate the world file
        try {
            Schema schema = wf.loadSchema(GlobalVars.WORLD_FILE_SCHEMA);
            wf.validateXml(schema, GlobalVars.WORLD_FILE);
        } catch (WorldFileValidationException wfve) {
            wfve.printStackTrace();
            return null;
        }

        try {
            File file = new File(GlobalVars.WORLD_FILE);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList nodeLst = doc.getElementsByTagName(GlobalVars.CONTEXT_ELEMENT);

            WorldElement[] res = new WorldElement[nodeLst.getLength()];

            for (int i = 0; i < nodeLst.getLength(); i++) {
                //iterate through context elements
                Node ceNode = nodeLst.item(i);
                NamedNodeMap nnm = ceNode.getAttributes();
                Attr ocn = (Attr)nnm.getNamedItem(GlobalVars.ONTOLOGY_CLASS_NAME);
                Attr oin = (Attr)nnm.getNamedItem(GlobalVars.ONTOLOGY_INDIVIDUAL_NAME);
                res[i] = new WorldElement(oin.getValue(), ocn.getValue());
                
                NodeList dlst = ceNode.getChildNodes();
                

                for (int j = 0; j < dlst.getLength(); j++) {
                    //iterate through context element data properties
                    Node pceNode = dlst.item(j);
                    if (pceNode.hasAttributes()) {
                        NamedNodeMap pnnm = pceNode.getAttributes();
                        Attr dn = (Attr)pnnm.getNamedItem(GlobalVars.DATA_NAME);
                        Attr dv = (Attr)pnnm.getNamedItem(GlobalVars.DATA_VALUE);
                        //Attr dn = (Attr)pnnm.getNamedItem(GlobalVars.DATA_NAME);
                        //Attr dv = (Attr)pnnm.getNamedItem(GlobalVars.DATA_VALUE);
                        //attach attributes
                        res[i].addAttribute(dn.getValue(), dv.getValue());
                    }
                }
           }
           return res;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }
}
