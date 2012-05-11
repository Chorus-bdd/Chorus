package org.chorusbdd.chorus.core.interpreter.scanner;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 11/05/12
* Time: 15:31
* To change this template use File | Settings | File Templates.
*/
public interface FilenameFilter {
    /**
     * All paths will be represented
     * using forward slashes and no
     * files will begin with a slash
     */
    public boolean accept(String filename);
}
