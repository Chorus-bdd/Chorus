package org.chorusbdd.chorus.output;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * Created by nickebbutt on 18/03/2018.
 */
public class AbstractOutputWriterTest {
    protected String captureOutput(Consumer<Consumer<String>> writeOutputMethod) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(byteArrayOutputStream);
            Consumer<String> println = printWriter::println;
            writeOutputMethod.accept(println);
            printWriter.flush();
        } finally {
            if ( printWriter != null) {
                printWriter.close();
            }
        }
        return byteArrayOutputStream.toString().replaceAll("\r\n", "\n");
    }
}
