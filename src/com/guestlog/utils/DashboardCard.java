package com.guestlog.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Simple dashboard metric card.
 */
public class DashboardCard extends JPanel {

    public DashboardCard(String title, String value) {
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        setPreferredSize(new Dimension(200, 100));

        JLabel titleL = new JLabel(title);
        titleL.setFont(UITheme.FONT_SMALL);
        titleL.setForeground(UITheme.TEXT_MUTED);

        JLabel valueL = new JLabel(value);
        valueL.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueL.setForeground(UITheme.TEXT_PRIMARY);

        add(titleL, BorderLayout.NORTH);
        add(valueL, BorderLayout.CENTER);
    }

    public void setValueText(String v) {
        ((JLabel) getComponent(1)).setText(v);
    }

    public void setAccent(Color color) {
        JLabel valueL = (JLabel) getComponent(1);
        valueL.setForeground(color);
    }
}
