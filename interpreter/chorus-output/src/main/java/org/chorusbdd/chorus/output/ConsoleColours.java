/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.output;

/**
 * Created by nickebbutt on 19/01/2018.
 */
class ConsoleColours {

    private static final String RED="\033[0;31m";
    private static final String GREEN="\033[0;32m";
    private static final String YELLOW="\033[0;33m";
    private static final String BLUE="\033[0;34m";
    private static final String PURPLE="\033[0;35m";
    private static final String CYAN="\033[1;34m";
    private static final String WHITE="\033[1;37m";
    private static final String COLOR_NC="\033[0m";

    public String highlightGreen(String text) {
        return GREEN + text + COLOR_NC;
    }

    public String highlightRed(String text) {
        return RED + text + COLOR_NC;
    }
    
    public String highlightYellow(String text) {
        return YELLOW + text + COLOR_NC;
    }

    public String highlightBlue(String text) {
        return BLUE + text + COLOR_NC;
    }

    public String highlightPurple(String text) {
        return PURPLE + text + COLOR_NC;
    }

    public String highlightCyan(String text) {
        return CYAN + text + COLOR_NC;
    }
    
    public String highlightWhite(String text) {
        return WHITE + text + COLOR_NC;
    }
}
