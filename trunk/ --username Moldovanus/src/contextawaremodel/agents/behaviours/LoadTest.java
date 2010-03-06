/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextawaremodel.agents.behaviours;

/**
 *
 * @author Me
 */
/*****************************************************************************
 *                        Web3d.org Copyright (c) 2004-2007
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/
import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

import org.web3d.x3d.sai.*;

/**
 * An example of how to use Xj3D Specific SAI routines.
 *
 * @author Alan Hudson
 * @version
 */
public class LoadTest extends JFrame {

    /**
     * Constructor for the demo.
     */
    public LoadTest() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container contentPane = getContentPane();

        // Setup browser parameters
        HashMap requestedParameters = new HashMap();
        requestedParameters.put("Xj3D_ShowConsole", Boolean.FALSE);
        requestedParameters.put("Xj3D_FPSShown", Boolean.TRUE);
        requestedParameters.put( "Xj3D_NavbarShown", Boolean.TRUE );

        // Create an SAI component
        X3DComponent x3dComp = BrowserFactory.createX3DComponent(requestedParameters);

        // Add the component to the UI
        JComponent x3dPanel = (JComponent) x3dComp.getImplementation();
        contentPane.add(x3dPanel, BorderLayout.CENTER);

        // Get an external browser
        ExternalBrowser x3dBrowser = x3dComp.getBrowser();

        boolean useXj3D = true;

        if (x3dBrowser.getName().indexOf("Xj3D") < 0) {
            System.out.println("Not running on Xj3D, extended functions disabled");
            useXj3D = false;
        }
        setSize(500, 500);
        setVisible(true);

        // Create an X3D scene by loading a file
        final X3DScene mainScene = x3dBrowser.createX3DFromURL(new String[]{"src/scene.x3d"});

        // Replace the current world with the new one
        x3dBrowser.replaceWorld(mainScene);

        if (!useXj3D) {
            return;
        }

        /**
         * <ROUTE fromNode="Timer" fromField="fraction_changed" toNode="YellowCI" toField="set_fraction"/>
         * <ROUTE fromNode="YellowCI" fromField="value_changed" toNode="YellowLight" toField="diffuseColor"/>
         */
        X3DNode touch = mainScene.getNamedNode("LightTouchSensor");
        if (touch == null) {
            System.out.println("Couldn't find TouchSensor named: TOUCH_SENSOR");
            return;
        }

        final float[] f = new float[]{1, 1, 1};
        SFTime touchTimeField = (SFTime) touch.getField("touchTime");

        //X3DNode sphere = mainScene.getNamedNode("boxColor");
        //SFColor color = (SFColor) sphere.getField("diffuseColor");
        //color.setValue(f);

        touchTimeField.addX3DEventListener(new X3DFieldEventListener() {

            public void readableFieldChanged(X3DFieldEvent xdfe) {
                //<ROUTE fromNode="YellowCI" fromField="value_changed" toNode="YellowLight" toField="diffuseColor"/>

                // <ROUTE fromNode="Timer" fromField="fraction_changed" toNode="RedCI" toField="set_fraction"/>
                //X3DNode TS = mainScene.getNamedNode("YellowLight");
                //X3DNode PI = mainScene.getNamedNode("YellowCI");

                //X3DNode timer = mainScene.getNamedNode("Timer");


                X3DNode light = mainScene.getNamedNode("Light");
                SFBool lightIntensity = (SFBool) light.getField("on");

                if (lightIntensity.getValue()) {
                    lightIntensity.setValue(false);
                } else {
                    lightIntensity.setValue(true);
                }
                System.out.println(lightIntensity.getValue());
                //mainScene.addRoute(timer, "fraction_changed", PI, "set_fraction");
                //mainScene.addRoute(PI, "value_changed", TS, "diffuseColor");
                System.out.println("Clicked");

            }
        });

        //float[] f = new float[3];
        //color.getValue(f);
        //System.out.println("" + f[0] + "   " + f[1] + "   " + f[2]);

    }

    /**
     * Main method.
     *
     * @param args None handled
     */
    public static void main(String[] args) {

        LoadTest demo = new LoadTest();
    }
}
