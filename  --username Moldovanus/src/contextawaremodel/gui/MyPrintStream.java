package contextawaremodel.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JTextArea;

public class MyPrintStream extends ByteArrayOutputStream {

    private JTextArea textArea;
    private PrintStream systemOut;

    public MyPrintStream(PrintStream systemOut, JTextArea textArea) {
        this.systemOut = systemOut;
        this.textArea = textArea;
    }

    @Override
    public void flush() throws IOException {
        String s = this.toString();
        this.reset();
        systemOut.print(s);
        textArea.setText(textArea.getText()+s);
    }
}
