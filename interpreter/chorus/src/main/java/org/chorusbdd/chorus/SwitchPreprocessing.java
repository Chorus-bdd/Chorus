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
