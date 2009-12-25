/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actionselection.x3dCommand;

import java.io.Serializable;
import org.web3d.x3d.sai.X3DScene;

/**
 *
 * @author Me
 */
public abstract class X3DCommand implements Serializable {

    protected String nodeName;
    protected boolean value;

    public X3DCommand(String nodeName, boolean value) {
        this.value = value;
        this.nodeName = nodeName;

    }
    public abstract void execute(X3DScene mainScene);
}
