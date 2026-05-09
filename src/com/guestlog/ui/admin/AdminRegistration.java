package com.guestlog.ui.admin;

import com.guestlog.service.AuthService;
import com.guestlog.utils.FormValidator;
import com.guestlog.utils.RoundedButton;
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

public class AdminRegistration extends JFrame {

    public AdminRegistration(JFrame parent) {
        super("GuestLog • Register college");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 460);
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

        JLabel title = new JLabel("Create college account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        form.add(title, gbc);

        gbc.gridy++;
        JLabel sub = new JLabel("Admins can configure academic structure and visitor intake.");
        sub.setForeground(UITheme.TEXT_MUTED);
        form.add(sub, gbc);

        JTextField college = field(form, gbc, "College name");
        JTextField email = field(form, gbc, "Email");
        JTextField user = field(form, gbc, "Username");

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        JPasswordField pass = new JPasswordField(18);
        form.add(pass, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        RoundedButton save = new RoundedButton("Create account");
        save.setBackground(new Color(99, 102, 241));
        save.setForeground(Color.WHITE);
        save.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        form.add(save, gbc);

        save.addActionListener(e -> {
            try {
                if (FormValidator.isBlank(college.getText()) || FormValidator.isBlank(user.getText())
                        || FormValidator.isBlank(email.getText()) || FormValidator.isBlank(new String(pass.getPassword()))) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!FormValidator.isValidEmail(email.getText())) {
                    JOptionPane.showMessageDialog(this, "Enter a valid email.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                new AuthService().registerAdmin(user.getText(), new String(pass.getPassword()), email.getText(), college.getText());
                JOptionPane.showMessageDialog(this, "Account created. You can sign in now.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new AdminLogin(parent).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not register: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(form, BorderLayout.CENTER);
    }

    private JTextField field(JPanel form, GridBagConstraints gbc, String label) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField tf = new JTextField(18);
        form.add(tf, gbc);
        return tf;
    }
}
