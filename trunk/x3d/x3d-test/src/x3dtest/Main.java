/*****************************************************************************
 *                        Web3d.org Copyright (c) 2001-2007
 *                               Java Source
 *
 * This source is licensed under the BSD license.
 * Please read docs/BSD.txt for the text of the license.
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

// External imports
import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

import org.web3d.x3d.sai.*;

/**
 * A simple example of how to use SAI to load a scene and modify a value.
 *
 * @author Alan Hudson
 * @version
 */
public class Main extends JFrame {

    /**
     * Constructor for the demo.
     */
    public Main() {
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container contentPane = getContentPane();

        // Setup browser parameters
        HashMap requestedParameters = new HashMap();

        // Create an SAI component
        X3DComponent x3dComp = BrowserFactory.createX3DComponent(requestedParameters);

        // Add the component to the UI
        JComponent x3dPanel = (JComponent)x3dComp.getImplementation();
        contentPane.add(x3dPanel, BorderLayout.CENTER);

        // Get an external browser
        ExternalBrowser x3dBrowser = x3dComp.getBrowser();
        
        setSize(600,500);
        setVisible(true);

        ProfileInfo profile = null;

        try {
            profile = x3dBrowser.getProfile("Immersive");
        } catch(NotSupportedException nse) {
            System.out.println("Immersive Profile not supported");
            System.exit(-1);
        }

        X3DScene mainScene = x3dBrowser.createScene(profile, null);

        X3DNode shape = mainScene.createNode("Shape");

        SFNode shape_geometry = (SFNode) (shape.getField("geometry"));
        X3DNode box = mainScene.createNode("Sphere");
 
        shape_geometry.setValue(box);

        mainScene.addRootNode(shape);
        x3dBrowser.replaceWorld(mainScene);
        
    }

    /**
     * Main method.
     *
     * @param args None handled
     */
    /*public static void main(String[] args) {

        Main demo = new Main();
    }*/

}