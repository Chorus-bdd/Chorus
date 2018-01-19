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
