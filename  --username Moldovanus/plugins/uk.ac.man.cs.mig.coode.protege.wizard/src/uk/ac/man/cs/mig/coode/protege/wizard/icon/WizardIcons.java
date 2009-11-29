package uk.ac.man.cs.mig.coode.protege.wizard.icon;

import edu.stanford.smi.protege.resource.Icons;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         27-Oct-2004
 */
public class WizardIcons {

  static Map myIconsCache = new HashMap();
  private static String pathFromPluginToImages = "images/";

  public static Icon getAllSubIcon() {
    return getImageIcon("allsub");
  }

  public static ImageIcon getDirectSubIcon() {
    return getImageIcon("directsub");
  }

  public static ImageIcon getLeafSubIcon() {
    return getImageIcon("leafsub");
  }

  public static ImageIcon getHatIcon() {
    return getImageIcon("hat");
  }

  public static ImageIcon getImageIcon(String name) {
    ImageIcon icon = (ImageIcon) myIconsCache.get(name);
    if (icon == null || icon.getIconWidth() == -1) {
      String partialName = pathFromPluginToImages + name;
      if (name.lastIndexOf('.') < 0) {
        partialName += ".gif";
      }
      icon = loadImageIcon(Icons.class, partialName);
      if (icon == null && !name.equals("Ugly")) {
        System.err.println("ICON: no image found, loading ugly");
        //icon = getImageIcon("Ugly");
      }
      myIconsCache.put(name, icon);
    }
    return icon;
  }

  static ImageIcon loadImageIcon(Class cls, String name) {
    ImageIcon icon = null;
    URL url = WizardIcons.class.getResource(name);
    if (url != null) {
      icon = new ImageIcon(url);
      if (icon.getIconWidth() == -1) {
        System.err.println("failed to load " + name);
      }
    }
    else {
      System.err.println("OUCH - couldn't find icon - URL is null");
    }
    return icon;
  }

}
