/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.tools.util;


import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 07-Jan-2009
 * Time: 10:31:53
 */
public class ImageUtils {

    public static final ImageIcon FEATURE_FAILED = getImageIcon("/images/cube_red.png");
    public static final ImageIcon FEATURE_NOT_IMPLEMENTED = getImageIcon("/images/cube_yellow.png");
    public static final ImageIcon FEATURE_OK = getImageIcon("/images/cube_green.png");
    public static final ImageIcon FEATURE_IN_PROGRESS = getImageIcon("/images/cube_yellow_play.png");
    public static final ImageIcon SCENARIO_FAILED = getImageIcon("/images/step_failed.png");
    public static final ImageIcon SCENARIO_NOT_IMPLEMENTED = getImageIcon("/images/step_not_implemented.png");
    public static final ImageIcon SCENARIO_OK = getImageIcon("/images/step_ok.png");
    public static final ImageIcon SCENARIO_IN_PROGRESS = getImageIcon("/images/step_in_progress.png");
    public static final ImageIcon SUITE_FAILED = getImageIcon("/images/cubes_red.png");
    public static final ImageIcon SUITE_NOT_IMPLEMENTED = getImageIcon("/images/cubes_yellow.png");
    public static final ImageIcon SUITE_IN_PROGRESS = getImageIcon("/images/cubes_yellow_play.png");
    public static final ImageIcon SUITE_OK = getImageIcon("/images/cubes_green.png");
    public static final ImageIcon REMOVE_TAB = getImageIcon("/images/dismiss.png");

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
