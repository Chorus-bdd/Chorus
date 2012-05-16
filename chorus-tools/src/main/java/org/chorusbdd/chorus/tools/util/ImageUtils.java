package org.chorusbdd.chorus.tools.util;


import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 10:31:53
 */
public class ImageUtils {

    public static final ImageIcon FEATURE_FAILED = getImageIcon("/images/feature_failed_32x32.png");
    public static final ImageIcon FEATURE_NOT_IMPLEMENTED = getImageIcon("/images/feature_not_implemented_32x32.png");
    public static final ImageIcon FEATURE_OK = getImageIcon("/images/feature_ok_32x32.png");
    public static final ImageIcon SCENARIO_FAILED = getImageIcon("/images/scenario_failed_24x24.png");
    public static final ImageIcon SCENARIO_NOT_IMPLEMENTED = getImageIcon("/images/scenario_not_implemented_24x24.png");
    public static final ImageIcon SCENARIO_OK = getImageIcon("/images/scenario_ok_24x24.png");
    public static final ImageIcon STEP_FAILED = getImageIcon("/images/step_failed_16x16.png");
    public static final ImageIcon STEP_NOT_IMPLEMENTED = getImageIcon("/images/step_not_implemented_16x16.png");
    public static final ImageIcon STEP_RUNNING = getImageIcon("/images/step_running_16x16.png");
    public static final ImageIcon STEP_OK = getImageIcon("/images/step_ok_16x16.png");

    public static ImageIcon getImageIcon(String name) {
        ImageIcon imageIcon = null;
        try {
            imageIcon = new ImageIcon(ImageUtils.class.getResource(name));
        }
        catch (Exception e) {
            System.err.println("Unable to load Icon: " + name);
        }
        return imageIcon;
    }
}
