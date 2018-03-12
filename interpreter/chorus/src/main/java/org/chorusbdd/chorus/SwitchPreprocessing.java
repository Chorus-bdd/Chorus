package org.chorusbdd.chorus;

import org.chorusbdd.chorus.interpreter.startup.ChorusConfigProperty;
import org.chorusbdd.chorus.logging.ChorusOut;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nickebbutt on 12/03/2018.
 */
class SwitchPreprocessing {
    
    static boolean handleVersionOrHelpSwitches(String[] args) {
        boolean result = true;
        if ( args.length > 0 ) {
            switch(args[0]) {
                case "--version" :
                    showVersion();
                    result = false;
                    break;
                case "--help" :
                    showHelp();
                    result = false;
                    break;
            }
        }
        return result;
    }

    private static void showHelp() {
        ChorusOut.out.print(ChorusConfigProperty.getHelpText());
    }

    private static void showVersion() {
        Properties p = new Properties();
        String version = "Unknown Version";
        try {
            InputStream resourceAsStream = SwitchPreprocessing.class.getResourceAsStream("/CHORUS_VERSION");
            if ( resourceAsStream != null) {
                p.load(resourceAsStream);
                version = p.getOrDefault("chorusVersion", version).toString();
            }
        } catch (Throwable t) {
            //ignore
        } finally {
            ChorusOut.out.println("Chorus Version: " + version);
        }
    }
}
