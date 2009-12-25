/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.x3dCommand;

import org.web3d.x3d.sai.SFBool;
import org.web3d.x3d.sai.X3DNode;
import org.web3d.x3d.sai.X3DScene;

/**
 *
 * @author Me
 */
public class X3DOnOffCommand extends X3DCommand {

    public X3DOnOffCommand(String nodeName , boolean value) {
        super(nodeName , value);
    }

    @Override
    public void execute(X3DScene mainScene) {
        X3DNode sensor = mainScene.getNamedNode("Light");
        SFBool sensorState = (SFBool) sensor.getField("on");
        sensorState.setValue(value);
    }
}
