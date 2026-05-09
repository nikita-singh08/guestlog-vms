package com.guestlog.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

/**
 * JButton with rounded corners and hover-friendly cursor.
 */
public class RoundedButton extends JButton {

    private final int arc;

    public RoundedButton(String text) {
        this(text, 18);
    }

    public RoundedButton(String text, int arc) {
        super(text);
        this.arc = arc;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = getBackground();
        if (!isEnabled()) {
            bg = bg.darker();
        } else if (getModel().isPressed()) {
            bg = bg.darker();
        } else if (getModel().isRollover()) {
            bg = new Color(
                    Math.min(255, bg.getRed() + 12),
                    Math.min(255, bg.getGreen() + 12),
                    Math.min(255, bg.getBlue() + 12));
        }

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        g2.setColor(new Color(0, 0, 0, 24));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        g2.dispose();

        super.paintComponent(g);
    }
}
