package org.chorusbdd.chorus.stepinvoker.catalogue;

import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * Created by nickebbutt on 13/03/2018.
 */
public interface CatalogueWriter {
    
    void writeCatalogue(Collection<StepInvoker> stepInvokers, PrintWriter writer);
}
