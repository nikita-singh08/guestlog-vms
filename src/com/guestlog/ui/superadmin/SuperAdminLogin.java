package com.guestlog.ui.superadmin;

import com.guestlog.model.SuperAdmin;
import com.guestlog.service.SuperAdminAuthService;
import com.guestlog.utils.FormValidator;
import com.guestlog.utils.RoundedButton;
import com.guestlog.utils.SessionManager;
import com.guestlog.utils.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class SuperAdminLogin extends JFrame {

    public SuperAdminLogin(JFrame parent) {
        super("GuestLog • Super Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 360);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BG_APP);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(24, 28, 24, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("Super administrator");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        form.add(title, gbc);

        gbc.gridy++;
        JLabel sub = new JLabel("Sign in to manage administrators and global analytics.");
        sub.setForeground(UITheme.TEXT_MUTED);
        form.add(sub, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(new JLabel("Username"), gbc);

        gbc.gridx = 1;
        JTextField user = new JTextField(18);
        form.add(user, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        JPasswordField pass = new JPasswordField(18);
        form.add(pass, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        RoundedButton login = new RoundedButton("Sign in");
        login.setBackground(UITheme.ACCENT);
        login.setForeground(Color.WHITE);
        login.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        form.add(login, gbc);

        login.addActionListener(e -> {
            String u = user.getText();
            String p = new String(pass.getPassword());
            if (FormValidator.isBlank(u) || FormValidator.isBlank(p)) {
                JOptionPane.showMessageDialog(this, "Enter username and password.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                SuperAdminAuthService auth = new SuperAdminAuthService();
                java.util.Optional<SuperAdmin> opt = auth.authenticate(u, p);
                if (opt.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                SessionManager.setSuperAdmin(opt.get());
                dispose();
                new SuperAdminDashboard(parent).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(form, BorderLayout.CENTER);
    }
}
