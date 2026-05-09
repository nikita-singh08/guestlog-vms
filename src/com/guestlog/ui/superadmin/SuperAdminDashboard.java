package com.guestlog.ui.superadmin;

import com.guestlog.model.Admin;
import com.guestlog.model.Visitor;
import com.guestlog.service.AuthService;
import com.guestlog.service.SuperAdminService;
import com.guestlog.service.VisitorService;
import com.guestlog.utils.DashboardCard;
import com.guestlog.utils.FormValidator;
import com.guestlog.utils.RoundedButton;
import com.guestlog.utils.SessionManager;
import com.guestlog.utils.UITheme;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class SuperAdminDashboard extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel main = new JPanel(cards);
    private final VisitorService visitorService = new VisitorService();
    private final SuperAdminService superAdminService = new SuperAdminService();

    private DefaultTableModel visitorModel;
    private JTextField visitorSearch;
    private DashboardCard cardTotal;
    private DashboardCard cardToday;
    private DashboardCard cardAdmins;

    private DefaultTableModel adminModel;
    private JTable adminTable;

    private final JFrame parent;

    public SuperAdminDashboard(JFrame parent) {
        this.parent = parent;
        setTitle("GuestLog • Super administrator");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 760));
        setLocationRelativeTo(parent);
        getContentPane().setLayout(new BorderLayout());

        JPanel sidebar = buildSidebar();
        main.setOpaque(false);
        main.add(buildDashboardHome(), "HOME");
        main.add(wrapScroll(buildAdminsPanel()), "ADMINS");
        main.add(wrapScroll(buildVisitorsPanel()), "VISITORS");

        add(sidebar, BorderLayout.WEST);
        add(main, BorderLayout.CENTER);

        refreshAll();
    }

    private JPanel wrapScroll(JPanel p) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(16, 16, 16, 16));
        wrap.add(p, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildSidebar() {
        JPanel bar = new JPanel(new GridBagLayout());
        bar.setBackground(UITheme.BG_SIDEBAR);
        bar.setPreferredSize(new Dimension(240, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(24, 16, 12, 16);

        JLabel logo = new JLabel("GuestLog");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        bar.add(logo, gbc);

        gbc.gridy++;
        JLabel sub = new JLabel("Super Admin");
        sub.setForeground(new Color(148, 163, 184));
        sub.setFont(UITheme.FONT_SMALL);
        bar.add(sub, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 12, 4, 12);

        bar.add(navButton("Dashboard", "HOME"), gbc);
        gbc.gridy++;
        bar.add(navButton("Admins", "ADMINS"), gbc);
        gbc.gridy++;
        bar.add(navButton("Visitors", "VISITORS"), gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        RoundedButton out = new RoundedButton("Logout");
        out.setBackground(UITheme.DANGER);
        out.setForeground(Color.WHITE);
        out.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        out.addActionListener(e -> logout());
        bar.add(out, gbc);

        return bar;
    }

    private JButton navButton(String text, String card) {
        JButton b = new JButton(text);
        b.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        b.setBackground(new Color(51, 65, 85));
        b.setForeground(Color.WHITE);
        b.setFont(UITheme.FONT_SIDEBAR);
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        b.setFocusPainted(false);
        b.addActionListener(e -> {
            cards.show(main, card);
            refreshAll();
        });
        return b;
    }

    private JPanel buildDashboardHome() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Operations overview");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        p.add(title, BorderLayout.NORTH);

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        stats.setOpaque(false);
        cardTotal = new DashboardCard("Total visitors", "0");
        cardToday = new DashboardCard("Visitors today", "0");
        cardAdmins = new DashboardCard("College admins", "0");
        stats.add(cardTotal);
        stats.add(cardToday);
        stats.add(cardAdmins);

        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setOpaque(false);

        grid.add(buildAnalyticsTable("Department-wise counts", new String[] { "Department", "Visitors" }));
        grid.add(buildAnalyticsTable("Branch-wise counts", new String[] { "Course / Branch", "Visitors" }));

        JPanel south = new JPanel(new GridLayout(1, 2, 12, 12));
        south.setOpaque(false);
        south.add(buildPurposeTable());

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);
        center.add(stats, BorderLayout.NORTH);
        center.add(grid, BorderLayout.CENTER);
        center.add(south, BorderLayout.SOUTH);

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAnalyticsTable(String title, String[] cols) {
        JPanel wrap = new JPanel(new BorderLayout(4, 4));
        wrap.setBackground(UITheme.BG_CARD);
        wrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JLabel l = new JLabel(title);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_PRIMARY);
        wrap.add(l, BorderLayout.NORTH);
        DefaultTableModel tm = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(tm);
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);
        wrap.add(new JScrollPane(table), BorderLayout.CENTER);
        wrap.putClientProperty("model", tm);

        if (title.startsWith("Department")) {
            wrap.putClientProperty("kind", "dept");
        } else {
            wrap.putClientProperty("kind", "branch");
        }
        return wrap;
    }

    private JPanel buildPurposeTable() {
        JPanel wrap = new JPanel(new BorderLayout(4, 4));
        wrap.setBackground(UITheme.BG_CARD);
        wrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JLabel l = new JLabel("Purpose-wise counts");
        l.setFont(UITheme.FONT_BODY);
        wrap.add(l, BorderLayout.NORTH);
        DefaultTableModel tm = new DefaultTableModel(new String[] { "Purpose", "Visitors" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(tm);
        table.setRowHeight(22);
        wrap.add(new JScrollPane(table), BorderLayout.CENTER);
        wrap.putClientProperty("model", tm);
        wrap.putClientProperty("kind", "purpose");
        return wrap;
    }

    private JPanel buildAdminsPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setOpaque(false);
        JLabel title = new JLabel("College administrators");
        title.setFont(UITheme.FONT_TITLE);
        top.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        RoundedButton add = new RoundedButton("Add admin");
        add.setBackground(UITheme.ACCENT);
        add.setForeground(Color.WHITE);
        add.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        add.addActionListener(e -> showAddAdminDialog());
        RoundedButton del = new RoundedButton("Remove selected");
        del.setBackground(UITheme.DANGER);
        del.setForeground(Color.WHITE);
        del.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        del.addActionListener(e -> removeSelectedAdmin());
        actions.add(add);
        actions.add(del);
        top.add(actions, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        adminModel = new DefaultTableModel(new String[] { "ID", "Username", "Email", "College" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(adminModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        this.adminTable = table;
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildVisitorsPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("All visitor records");
        title.setFont(UITheme.FONT_TITLE);
        top.add(title, BorderLayout.WEST);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        search.setOpaque(false);
        visitorSearch = new JTextField(24);
        RoundedButton go = new RoundedButton("Search");
        styleAccent(go);
        go.addActionListener(e -> reloadVisitors());
        search.add(visitorSearch);
        search.add(go);
        top.add(search, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        visitorModel = new DefaultTableModel(
                new String[] { "ID", "College", "Name", "Email", "Dept", "Course", "Branch", "Purpose", "Date", "Time" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(visitorModel);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(22);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        RoundedButton refresh = new RoundedButton("Refresh");
        styleAccent(refresh);
        refresh.addActionListener(e -> reloadVisitors());
        RoundedButton delete = new RoundedButton("Delete selected");
        delete.setBackground(UITheme.DANGER);
        delete.setForeground(Color.WHITE);
        delete.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        delete.addActionListener(e -> deleteSelectedVisitor(table));
        bottom.add(refresh);
        bottom.add(delete);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void styleAccent(RoundedButton b) {
        b.setBackground(UITheme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    }

    private void refreshAll() {
        try {
            int total = visitorService.totalVisitors();
            int today = visitorService.countVisitorsToday();
            int admins = superAdminService.listAdmins().size();
            cardTotal.setValueText(String.valueOf(total));
            cardToday.setValueText(String.valueOf(today));
            cardAdmins.setValueText(String.valueOf(admins));

            JPanel home = (JPanel) main.getComponent(0);
            fillHomeTables(home);
            reloadAdmins();
            reloadVisitors();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Refresh failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillHomeTables(java.awt.Container root) {
        for (java.awt.Component comp : root.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel jp = (JPanel) comp;
                Object kind = jp.getClientProperty("kind");
                if (kind != null) {
                    DefaultTableModel tm = (DefaultTableModel) jp.getClientProperty("model");
                    tm.setRowCount(0);
                    try {
                        if ("dept".equals(kind)) {
                            for (Object[] row : visitorService.departmentAnalytics()) {
                                tm.addRow(new Object[] { row[0], row[1] });
                            }
                        } else if ("branch".equals(kind)) {
                            for (Object[] row : visitorService.branchAnalytics()) {
                                tm.addRow(new Object[] { row[0], row[1] });
                            }
                        } else if ("purpose".equals(kind)) {
                            for (Object[] row : visitorService.purposeAnalytics()) {
                                tm.addRow(new Object[] { row[0], row[1] });
                            }
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Analytics", JOptionPane.ERROR_MESSAGE);
                    }
                }
                fillHomeTables(jp);
            }
        }
    }

    private void reloadAdmins() {
        adminModel.setRowCount(0);
        try {
            for (Admin a : superAdminService.listAdmins()) {
                adminModel.addRow(new Object[] { a.getId(), a.getUsername(), a.getEmail(), a.getCollegeName() });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Admins", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadVisitors() {
        visitorModel.setRowCount(0);
        try {
            List<Visitor> list = visitorService.searchAllVisitors(visitorSearch != null ? visitorSearch.getText() : "");
            for (Visitor v : list) {
                visitorModel.addRow(new Object[] {
                    v.getId(),
                    v.getCollegeName(),
                    v.getFullName(),
                    v.getEmail(),
                    v.getDepartmentName(),
                    v.getCourseName(),
                    v.getBranchName(),
                    v.getPurposeOfVisit(),
                    v.getVisitDate().toString(),
                    v.getVisitTime().toString()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Visitors", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedAdmin() {
        if (adminTable == null) {
            return;
        }
        int row = adminTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an administrator.", "Admins", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) adminModel.getValueAt(adminTable.convertRowIndexToModel(row), 0);
        int ok = JOptionPane.showConfirmDialog(this, "Delete this admin and all related data?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            superAdminService.deleteAdmin(id);
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedVisitor(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a visitor.", "Visitors", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) visitorModel.getValueAt(table.convertRowIndexToModel(row), 0);
        int ok = JOptionPane.showConfirmDialog(this, "Delete this visitor and free a course seat?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            visitorService.deleteVisitorSuper(id);
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddAdminDialog() {
        JTextField college = new JTextField();
        JTextField email = new JTextField();
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(new JLabel("College name"), gbc);
        gbc.gridx = 1;
        p.add(college, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        p.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        p.add(email, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        p.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        p.add(user, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        p.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        p.add(pass, gbc);

        int res = JOptionPane.showConfirmDialog(this, p, "New college admin", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            if (FormValidator.isBlank(college.getText()) || FormValidator.isBlank(user.getText())
                    || FormValidator.isBlank(email.getText()) || FormValidator.isBlank(new String(pass.getPassword()))) {
                JOptionPane.showMessageDialog(this, "All fields required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new AuthService().registerAdmin(user.getText(), new String(pass.getPassword()), email.getText(), college.getText());
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        SessionManager.clear();
        dispose();
        parent.setVisible(true);
    }
}
