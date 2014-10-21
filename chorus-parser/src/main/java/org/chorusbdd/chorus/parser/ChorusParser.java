package org.chorusbdd.chorus.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by nick on 21/10/2014.
 */
public interface ChorusParser<E> {

    List<E> parse(Reader reader) throws IOException, ParseException;
}
