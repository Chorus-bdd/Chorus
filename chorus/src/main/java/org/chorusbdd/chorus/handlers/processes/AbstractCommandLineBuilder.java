package org.chorusbdd.chorus.handlers.processes;

import java.util.ArrayList;
import java.util.List;

/**
 * User: nick
 * Date: 12/12/13
 * Time: 09:08
 */
public abstract class AbstractCommandLineBuilder {
    
    public abstract List<String> buildCommandLine();

    protected List<String> getSpaceSeparatedTokens(String spaceSeparated) {
        List<String> tokens = new ArrayList<String>();
        String[] j = spaceSeparated.split(" ");
        for ( String s : j ) {
            if ( s.trim().length() > 0) {
                tokens.add(s);
            }
        }
        return tokens;
    }

}
