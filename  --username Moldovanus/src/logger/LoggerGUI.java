/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Me
 */
public class LoggerGUI {

    private JFrame frame;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private ArrayList<Object[]> messages;
    private String logPath = "";
    private String date;

    public LoggerGUI() {
        init();
        setAllProperties();
        addComponents();
        frame.setVisible(true);
        date = new java.util.Date().toString();
        date = date.replaceAll("[ :]", "_");
    }

    private void init() {
        frame = new JFrame("Logger");
        messages = new ArrayList<Object[]>();
        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);
        scrollPane.setAutoscrolls(true);
    }

    private void setAllProperties() {
        frame.setBounds(300, 300, 600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        textArea.setWrapStyleWord(true);
    }

    private void addComponents() {
        frame.add(scrollPane, "Center");
    }

    public void log(Color messageColor, String header, ArrayList<String> message) {

        String date = new java.util.Date().toString();
        messages.add(new Object[]{messageColor, header, message, date});


        textArea.append("\n" + header + " \t[" + date + "]\n");
        for (String s : message) {
            textArea.append("\t" + s + "\n");
        }
        textArea.repaint();
        textArea.setCaretPosition(textArea.getText().length());

        savePDF();
    }

    public void savePDF() {
        try {

            String logName = logPath + "log_" + date + ".pdf";
            Document document = new Document();
            File file = new File(logName);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(logName));
            document.open();

            for (Object[] o : messages) {
                Font headerFont = new Font();
                Color c = (Color) o[0];
                BaseColor baseColor = new BaseColor(c.getRed(), c.getGreen(), c.getBlue());
                headerFont.setColor(baseColor);

                Paragraph p = new Paragraph("\n" + (String) o[1] + " \t [" + (String) o[3] + "]\n");
                p.setFont(headerFont);

                for (String s : (ArrayList<String>) o[2]) {
                    Paragraph p1 = new Paragraph("\t " + s);

                    p.add(p1);
                }
                document.add(p);
            }

            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(LoggerGUI.class.getName()).log(Level.SEVERE, null, ex);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoggerGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
}
