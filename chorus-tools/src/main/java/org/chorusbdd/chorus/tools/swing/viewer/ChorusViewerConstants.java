package org.chorusbdd.chorus.tools.swing.viewer;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 17/05/12
 * Time: 23:38
 * To change this template use File | Settings | File Templates.
 */
public class ChorusViewerConstants {
    
    public static final Dimension DEFAULT_INITIAL_FRAME_SIZE = new Dimension(1024,768);
    
    //this needs to be half the width of main frame size
    public static final Dimension DEFAULT_SPLIT_PANE_CONTENT_SIZE = new Dimension(
        DEFAULT_INITIAL_FRAME_SIZE.width / 2, 
        DEFAULT_INITIAL_FRAME_SIZE.height
    );
}
