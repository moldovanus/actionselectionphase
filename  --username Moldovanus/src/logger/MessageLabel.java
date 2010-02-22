/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JLabel;

/**
 *
 * @author Me
 */
public class MessageLabel extends JLabel {

    private Color headerColor;
    private String header;
    private ArrayList<String> message;

    public MessageLabel(Color headerColor, String header, ArrayList<String> message) {
        this.headerColor = headerColor;
        this.header = header;
        this.message = message;
    }

    public MessageLabel() {
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Color getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(Color headerColor) {
        this.headerColor = headerColor;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<String> message) {
        this.message = message;
    }

    @Override
    public void paint(Graphics g) {
        if (g == null) {
            return;
        }
        int width = this.getWidth();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawRoundRect(0, 0, width, 30, 10, 10);
        g2d.setBackground(Color.white);
        Color color = headerColor;
        for (int i = 2; i < 29; i++) {
            g2d.setColor(color);
            g2d.drawLine(2, i, width - 2, i);
            if (i % 2 == 0) {
                color = color.darker();
            }
        }

        g2d.setColor(Color.white);
        g2d.drawString(header, 3, 20);

        g2d.setColor(Color.black);

        int y_index = 45;

        for (String s : message) {
            g2d.drawString(s, 15, y_index);
            y_index += 15;
        }
        g2d.setColor(headerColor);
        g2d.drawRoundRect(0, 0, width, y_index, 10, 10);

        this.setPreferredSize(new Dimension(this.getWidth(), y_index));
        this.setSize(new Dimension(this.getWidth(), y_index));

    }
}
