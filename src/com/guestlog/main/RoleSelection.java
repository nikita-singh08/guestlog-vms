package com.guestlog.main;

import com.guestlog.ui.admin.AdminLogin;
import com.guestlog.ui.admin.AdminRegistration;
import com.guestlog.ui.superadmin.SuperAdminLogin;
import com.guestlog.ui.visitor.VisitorRegistration;
import com.guestlog.utils.RoundedButton;
import com.guestlog.utils.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Landing screen with role selection and sidebar layout.
 */
public class RoleSelection extends JPanel {

    public RoleSelection(JFrame parent) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_APP);

        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(UITheme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(280, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(32, 28, 8, 28);

        JLabel brand = new JLabel("<html><div style='color:white;'>GuestLog</div></html>");
        brand.setFont(new Font("SansSerif", Font.BOLD, 28));
        brand.setForeground(Color.WHITE);
        sidebar.add(brand, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(4, 28, 24, 28);
        JLabel tag = new JLabel("Visitor Management");
        tag.setFont(UITheme.FONT_SUBTITLE);
        tag.setForeground(new Color(203, 213, 225));
        sidebar.add(tag, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(12, 28, 6, 28);
        JLabel navHint = new JLabel("Choose portal");
        navHint.setFont(UITheme.FONT_SMALL);
        navHint.setForeground(new Color(148, 163, 184));
        sidebar.add(navHint, gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel foot = new JLabel("<html><div style='color:#94a3b8;font-size:11px;'>Desktop app • Swing</div></html>");
        foot.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.insets = new Insets(8, 28, 24, 28);
        sidebar.add(foot, gbc);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(48, 48, 48, 48));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Welcome");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Select how you want to access GuestLog.");
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        JPanel titles = new JPanel(new java.awt.GridLayout(0, 1, 4, 4));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);
        header.add(titles, BorderLayout.WEST);
        content.add(header, BorderLayout.NORTH);

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cards.setOpaque(false);

        RoundedButton btnSuper = roleButton("Super Admin", "System owner console", UITheme.ACCENT);
        RoundedButton btnAdmin = roleButton("College Admin", "Register & manage campus", new Color(99, 102, 241));
        RoundedButton btnVisitor = roleButton("Visitor Kiosk", "Self-service registration", new Color(16, 185, 129));

        btnSuper.addActionListener(e -> new SuperAdminLogin(parent).setVisible(true));
        btnAdmin.addActionListener(e -> showAdminChooser(parent));
        btnVisitor.addActionListener(e -> new VisitorRegistration(parent).setVisible(true));

        cards.add(btnSuper);
        cards.add(btnAdmin);
        cards.add(btnVisitor);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(cards, BorderLayout.NORTH);
        content.add(centerWrap, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private void showAdminChooser(JFrame parent) {
        javax.swing.JDialog dlg = new javax.swing.JDialog(parent, "College admin", true);
        dlg.setLayout(new BorderLayout(12, 12));
        JPanel p = new JPanel(new java.awt.GridLayout(0, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        RoundedButton login = new RoundedButton("Sign in");
        styleButton(login, UITheme.ACCENT);
        login.addActionListener(e -> {
            dlg.dispose();
            new AdminLogin(parent).setVisible(true);
        });

        RoundedButton reg = new RoundedButton("Register college");
        styleButton(reg, UITheme.TEXT_MUTED);
        reg.setForeground(Color.WHITE);
        reg.addActionListener(e -> {
            dlg.dispose();
            new AdminRegistration(parent).setVisible(true);
        });

        p.add(login);
        p.add(reg);
        dlg.add(p, BorderLayout.CENTER);
        dlg.pack();
        dlg.setLocationRelativeTo(parent);
        dlg.setVisible(true);
    }

    private RoundedButton roleButton(String title, String subtitle, Color bg) {
        RoundedButton b = new RoundedButton("<html><center><b style='font-size:15px;'>" + title + "</b><br/>"
                + "<span style='font-size:12px;color:#475569;'>" + subtitle + "</span></center></html>");
        b.setVerticalTextPosition(SwingConstants.CENTER);
        b.setHorizontalTextPosition(SwingConstants.CENTER);
        styleButton(b, bg);
        b.setPreferredSize(new Dimension(240, 120));
        return b;
    }

    private void styleButton(RoundedButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(UITheme.FONT_BODY);
        b.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        b.setFocusPainted(false);
    }
}
