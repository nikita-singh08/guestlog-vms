package com.guestlog.main;

import com.guestlog.utils.UITheme;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public final class GuestLogApp {

    private GuestLogApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.put("Button.arc", 12);
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // fall back to default LAF
            }
            UIManager.put("Panel.background", UITheme.BG_APP);
            JFrame frame = new JFrame("GuestLog - Visitor Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new java.awt.Dimension(1100, 720));
            frame.setLocationRelativeTo(null);

            JPanel root = new JPanel(new java.awt.BorderLayout());
            root.setBackground(UITheme.BG_APP);
            root.setBorder(new EmptyBorder(0, 0, 0, 0));

            RoleSelection landing = new RoleSelection(frame);
            root.add(landing, java.awt.BorderLayout.CENTER);
            frame.setContentPane(root);
            frame.setVisible(true);
        });
    }
}
