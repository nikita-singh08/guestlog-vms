package com.guestlog.ui.admin;

import com.guestlog.model.Admin;
import com.guestlog.model.Branch;
import com.guestlog.model.Counsellor;
import com.guestlog.model.Course;
import com.guestlog.model.Department;
import com.guestlog.model.Visitor;
import com.guestlog.service.CollegeService;
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
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {

    private final JFrame parent;
    private final int adminId;
    private final CollegeService collegeService = new CollegeService();
    private final VisitorService visitorService = new VisitorService();

    private final CardLayout cards = new CardLayout();
    private final JPanel main = new JPanel(cards);

    private DashboardCard cardVisitors;
    private DashboardCard cardToday;
    private DashboardCard cardDepartments;

    private JComboBox<Department> structureDeptCombo;
    private JComboBox<Course> structureCourseCombo;
    private JTextField deptNameField;
    private JTextField courseNameField;
    private JSpinner courseSeatsSpinner;
    private JTextField branchNameField;

    private JTextField counsellorName;
    private JTextField counsellorEmail;
    private JTextField counsellorPhone;
    private JComboBox<Department> counsellorDeptCombo;
    private DefaultTableModel counsellorModel;
    private JTable counsellorTable;

    private DefaultTableModel seatModel;

    private JTextField visitorSearch;
    private DefaultTableModel visitorModel;
    private JTable visitorTable;

    public AdminDashboard(JFrame parent) {
        this.parent = parent;
        Admin me = SessionManager.getAdmin();
        if (me == null) {
            throw new IllegalStateException("Admin session required");
        }
        this.adminId = me.getId();

        setTitle("GuestLog • " + me.getCollegeName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 740));
        setLocationRelativeTo(parent);
        getContentPane().setLayout(new BorderLayout());

        main.setOpaque(false);
        main.add(buildHome(), "HOME");
        main.add(wrap(buildStructurePanel()), "STRUCTURE");
        main.add(wrap(buildCounsellorsPanel()), "COUNS");
        main.add(wrap(buildSeatsPanel()), "SEATS");
        main.add(wrap(buildVisitorsPanel()), "VISITORS");

        add(buildSidebar(me.getCollegeName()), BorderLayout.WEST);
        add(main, BorderLayout.CENTER);
        cards.show(main, "HOME");
        refreshHome();
    }

    private JPanel wrap(JPanel inner) {
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.setBorder(new EmptyBorder(12, 12, 12, 12));
        w.add(inner, BorderLayout.CENTER);
        return w;
    }

    private JPanel buildSidebar(String college) {
        JPanel bar = new JPanel(new GridBagLayout());
        bar.setBackground(UITheme.BG_SIDEBAR);
        bar.setPreferredSize(new Dimension(248, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(22, 14, 4, 14);
        gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        JLabel logo = new JLabel("GuestLog");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        bar.add(logo, gbc);
        gbc.gridy++;
        JLabel sub = new JLabel("<html><div style='color:#94a3b8;width:200px'>" + college + "</div></html>");
        bar.add(sub, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(14, 10, 4, 10);
        bar.add(nav("Dashboard", "HOME"), gbc);
        gbc.gridy++;
        bar.add(nav("Departments & courses", "STRUCTURE"), gbc);
        gbc.gridy++;
        bar.add(nav("Counsellors / faculty", "COUNS"), gbc);
        gbc.gridy++;
        bar.add(nav("Seat management", "SEATS"), gbc);
        gbc.gridy++;
        bar.add(nav("Registered visitors", "VISITORS"), gbc);
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

    private JButton nav(String text, String card) {
        JButton b = new JButton(text);
        b.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        b.setBackground(new Color(51, 65, 85));
        b.setForeground(Color.WHITE);
        b.setFont(UITheme.FONT_SIDEBAR);
        b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        b.setFocusPainted(false);
        b.addActionListener(e -> {
            cards.show(main, card);
            if ("HOME".equals(card)) {
                refreshHome();
            } else if ("COUNS".equals(card)) {
                reloadCounsellors();
            } else if ("SEATS".equals(card)) {
                reloadSeats();
            } else if ("VISITORS".equals(card)) {
                reloadVisitors();
            } else if ("STRUCTURE".equals(card)) {
                reloadStructureCombos();
            }
        });
        return b;
    }

    private JPanel buildHome() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel title = new JLabel("Dashboard");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        p.add(title, BorderLayout.NORTH);
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        stats.setOpaque(false);
        cardVisitors = new DashboardCard("Total visitors", "0");
        cardToday = new DashboardCard("Visitors today", "0");
        cardDepartments = new DashboardCard("Departments", "0");
        stats.add(cardVisitors);
        stats.add(cardToday);
        stats.add(cardDepartments);
        p.add(stats, BorderLayout.CENTER);
        return p;
    }

    private void refreshHome() {
        try {
            cardVisitors.setValueText(String.valueOf(visitorService.totalVisitorsForAdmin(adminId)));
            cardToday.setValueText(String.valueOf(visitorService.visitorsTodayForAdmin(adminId)));
            cardDepartments.setValueText(String.valueOf(collegeService.listDepartments(adminId).size()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dashboard", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildStructurePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Academic structure");
        title.setFont(UITheme.FONT_TITLE);
        p.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        p.add(new JLabel("New department"), gbc);
        gbc.gridx = 1;
        deptNameField = new JTextField(18);
        p.add(deptNameField, gbc);
        gbc.gridx = 2;
        RoundedButton addDept = new RoundedButton("Add department");
        styleAccent(addDept);
        addDept.addActionListener(e -> addDepartment());
        p.add(addDept, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        p.add(new JLabel("Department for course"), gbc);
        gbc.gridx = 1;
        structureDeptCombo = new JComboBox<>();
        p.add(structureDeptCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        p.add(new JLabel("Course name"), gbc);
        gbc.gridx = 1;
        courseNameField = new JTextField(18);
        p.add(courseNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        p.add(new JLabel("Total seats"), gbc);
        gbc.gridx = 1;
        courseSeatsSpinner = new JSpinner(new SpinnerNumberModel(60, 1, 10000, 1));
        p.add(courseSeatsSpinner, gbc);
        gbc.gridx = 2;
        RoundedButton addCourse = new RoundedButton("Add course");
        styleAccent(addCourse);
        addCourse.addActionListener(e -> addCourse());
        p.add(addCourse, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        p.add(new JLabel("Course for branch"), gbc);
        gbc.gridx = 1;
        structureCourseCombo = new JComboBox<>();
        p.add(structureCourseCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        p.add(new JLabel("Branch / section name"), gbc);
        gbc.gridx = 1;
        branchNameField = new JTextField(18);
        p.add(branchNameField, gbc);
        gbc.gridx = 2;
        RoundedButton addBranch = new RoundedButton("Add branch");
        styleAccent(addBranch);
        addBranch.addActionListener(e -> addBranch());
        p.add(addBranch, gbc);

        structureDeptCombo.addActionListener(e -> reloadStructureCourses());
        reloadStructureCombos();
        return p;
    }

    private void reloadStructureCombos() {
        Department prevD = (Department) structureDeptCombo.getSelectedItem();
        structureDeptCombo.removeAllItems();
        try {
            for (Department d : collegeService.listDepartments(adminId)) {
                structureDeptCombo.addItem(d);
            }
            if (prevD != null) {
                for (int i = 0; i < structureDeptCombo.getItemCount(); i++) {
                    if (structureDeptCombo.getItemAt(i).getId() == prevD.getId()) {
                        structureDeptCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Structure", JOptionPane.ERROR_MESSAGE);
        }
        reloadStructureCourses();
    }

    private void reloadStructureCourses() {
        Department d = (Department) structureDeptCombo.getSelectedItem();
        Course prevC = (Course) structureCourseCombo.getSelectedItem();
        structureCourseCombo.removeAllItems();
        if (d == null) {
            return;
        }
        try {
            for (Course c : collegeService.listCourses(d.getId())) {
                structureCourseCombo.addItem(c);
            }
            if (prevC != null) {
                for (int i = 0; i < structureCourseCombo.getItemCount(); i++) {
                    if (structureCourseCombo.getItemAt(i).getId() == prevC.getId()) {
                        structureCourseCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Courses", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDepartment() {
        if (FormValidator.isBlank(deptNameField.getText())) {
            JOptionPane.showMessageDialog(this, "Enter department name.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            collegeService.addDepartment(adminId, deptNameField.getText().trim());
            deptNameField.setText("");
            reloadStructureCombos();
            reloadCounsellorDepartments();
            JOptionPane.showMessageDialog(this, "Department added.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCourse() {
        Department d = (Department) structureDeptCombo.getSelectedItem();
        if (d == null) {
            JOptionPane.showMessageDialog(this, "Select a department.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (FormValidator.isBlank(courseNameField.getText())) {
            JOptionPane.showMessageDialog(this, "Enter course name.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int seats = (Integer) courseSeatsSpinner.getValue();
        try {
            collegeService.addCourse(d.getId(), courseNameField.getText().trim(), seats);
            courseNameField.setText("");
            reloadStructureCourses();
            reloadSeats();
            JOptionPane.showMessageDialog(this, "Course added.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBranch() {
        Course c = (Course) structureCourseCombo.getSelectedItem();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a course.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (FormValidator.isBlank(branchNameField.getText())) {
            JOptionPane.showMessageDialog(this, "Enter branch name.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            collegeService.addBranch(c.getId(), branchNameField.getText().trim());
            branchNameField.setText("");
            JOptionPane.showMessageDialog(this, "Branch added.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildCounsellorsPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);
        JLabel title = new JLabel("Counsellors & faculty");
        title.setFont(UITheme.FONT_TITLE);
        p.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        counsellorName = new JTextField(16);
        form.add(counsellorName, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        counsellorEmail = new JTextField(16);
        form.add(counsellorEmail, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Phone"), gbc);
        gbc.gridx = 1;
        counsellorPhone = new JTextField(16);
        form.add(counsellorPhone, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Department (optional)"), gbc);
        gbc.gridx = 1;
        counsellorDeptCombo = new JComboBox<>();
        counsellorDeptCombo.addItem(null);
        form.add(counsellorDeptCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        RoundedButton add = new RoundedButton("Add counsellor");
        styleAccent(add);
        add.addActionListener(e -> addCounsellor());
        form.add(add, gbc);

        p.add(form, BorderLayout.NORTH);

        counsellorModel = new DefaultTableModel(new String[] { "ID", "Name", "Email", "Phone", "Department" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        counsellorTable = new JTable(counsellorModel);
        counsellorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        counsellorTable.setRowHeight(22);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(new JScrollPane(counsellorTable), BorderLayout.CENTER);
        RoundedButton del = new RoundedButton("Remove selected");
        del.setBackground(UITheme.DANGER);
        del.setForeground(Color.WHITE);
        del.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        del.addActionListener(e -> deleteCounsellor());
        south.add(del, BorderLayout.SOUTH);
        p.add(south, BorderLayout.CENTER);

        reloadCounsellorDepartments();
        return p;
    }

    private void reloadCounsellorDepartments() {
        Department sel = (Department) counsellorDeptCombo.getSelectedItem();
        counsellorDeptCombo.removeAllItems();
        counsellorDeptCombo.addItem(null);
        try {
            for (Department d : collegeService.listDepartments(adminId)) {
                counsellorDeptCombo.addItem(d);
            }
            if (sel != null) {
                for (int i = 0; i < counsellorDeptCombo.getItemCount(); i++) {
                    Department d = counsellorDeptCombo.getItemAt(i);
                    if (d != null && d.getId() == sel.getId()) {
                        counsellorDeptCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Departments", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadCounsellors() {
        counsellorModel.setRowCount(0);
        try {
            for (Counsellor c : collegeService.listCounsellors(adminId)) {
                String dept = "-";
                if (c.getDepartmentId() != null) {
                    var opt = collegeService.findDepartmentById(c.getDepartmentId());
                    if (opt.isPresent()) {
                        dept = opt.get().getName();
                    }
                }
                counsellorModel.addRow(new Object[] { c.getId(), c.getName(), c.getEmail(), c.getPhone(), dept });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Counsellors", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCounsellor() {
        if (FormValidator.isBlank(counsellorName.getText())) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Department d = (Department) counsellorDeptCombo.getSelectedItem();
        Integer deptId = d == null ? null : d.getId();
        try {
            collegeService.addCounsellor(
                    adminId,
                    deptId,
                    counsellorName.getText().trim(),
                    counsellorEmail.getText() == null ? null : counsellorEmail.getText().trim(),
                    counsellorPhone.getText() == null ? null : counsellorPhone.getText().trim());
            counsellorName.setText("");
            counsellorEmail.setText("");
            counsellorPhone.setText("");
            reloadCounsellors();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCounsellor() {
        int row = counsellorTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int id = (int) counsellorModel.getValueAt(counsellorTable.convertRowIndexToModel(row), 0);
        try {
            collegeService.removeCounsellor(id);
            reloadCounsellors();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildSeatsPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);
        JLabel title = new JLabel("Seat availability by course");
        title.setFont(UITheme.FONT_TITLE);
        p.add(title, BorderLayout.NORTH);

        seatModel = new DefaultTableModel(
                new String[] { "Course ID", "Department", "Course", "Total", "Filled", "Remaining" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(seatModel);
        table.setRowHeight(22);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        RoundedButton edit = new RoundedButton("Update total seats (selected)");
        styleAccent(edit);
        edit.addActionListener(e -> editSeatTotal(table));
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.setOpaque(false);
        RoundedButton ref = new RoundedButton("Refresh");
        styleAccent(ref);
        ref.addActionListener(e -> reloadSeats());
        bot.add(ref);
        bot.add(edit);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    private void reloadSeats() {
        seatModel.setRowCount(0);
        try {
            for (Department d : collegeService.listDepartments(adminId)) {
                for (Course c : collegeService.listCourses(d.getId())) {
                    seatModel.addRow(new Object[] {
                        c.getId(),
                        d.getName(),
                        c.getName(),
                        c.getTotalSeats(),
                        c.getFilledSeats(),
                        c.getRemainingSeats()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Seats", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSeatTotal(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course row.", "Seats", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int courseId = (int) seatModel.getValueAt(table.convertRowIndexToModel(row), 0);
        int filled = (int) seatModel.getValueAt(table.convertRowIndexToModel(row), 4);
        JSpinner sp = new JSpinner(new SpinnerNumberModel((int) seatModel.getValueAt(table.convertRowIndexToModel(row), 3),
                filled, 100000, 1));
        int res = JOptionPane.showConfirmDialog(this, sp, "New total seats (min = filled: " + filled + ")",
                JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) {
            return;
        }
        int total = (Integer) sp.getValue();
        try {
            collegeService.updateCourseSeats(courseId, total, filled);
            reloadSeats();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildVisitorsPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Visitor records");
        title.setFont(UITheme.FONT_TITLE);
        top.add(title, BorderLayout.WEST);
        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        search.setOpaque(false);
        visitorSearch = new JTextField(20);
        RoundedButton go = new RoundedButton("Search");
        styleAccent(go);
        go.addActionListener(e -> reloadVisitors());
        search.add(visitorSearch);
        search.add(go);
        top.add(search, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        visitorModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Email", "Dept", "Course", "Branch", "Purpose", "Date" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        visitorTable = new JTable(visitorModel);
        visitorTable.setRowHeight(22);
        visitorTable.setAutoCreateRowSorter(true);
        p.add(new JScrollPane(visitorTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bot.setOpaque(false);
        RoundedButton ed = new RoundedButton("Edit selected");
        styleAccent(ed);
        ed.addActionListener(e -> editVisitor());
        RoundedButton del = new RoundedButton("Delete selected");
        del.setBackground(UITheme.DANGER);
        del.setForeground(Color.WHITE);
        del.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        del.addActionListener(e -> deleteVisitor());
        RoundedButton ref = new RoundedButton("Refresh");
        styleAccent(ref);
        ref.addActionListener(e -> reloadVisitors());
        bot.add(ref);
        bot.add(ed);
        bot.add(del);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    private void reloadVisitors() {
        visitorModel.setRowCount(0);
        try {
            for (Visitor v : visitorService.searchVisitorsForAdmin(adminId,
                    visitorSearch == null ? "" : visitorSearch.getText())) {
                visitorModel.addRow(new Object[] {
                    v.getId(),
                    v.getFullName(),
                    v.getEmail(),
                    v.getDepartmentName(),
                    v.getCourseName(),
                    v.getBranchName(),
                    v.getPurposeOfVisit(),
                    v.getVisitDate().toString()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Visitors", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editVisitor() {
        int row = visitorTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int id = (int) visitorModel.getValueAt(visitorTable.convertRowIndexToModel(row), 0);
        try {
            java.util.Optional<Visitor> opt = visitorService.findById(id);
            if (opt.isEmpty() || opt.get().getAdminId() != adminId) {
                return;
            }
            Visitor v = opt.get();
            JTextField name = new JTextField(v.getFullName());
            JTextField mobile = new JTextField(v.getMobile());
            JTextField email = new JTextField(v.getEmail());
            JComboBox<String> gender = new JComboBox<>(new String[] { "Male", "Female", "Other", "Prefer not to say" });
            gender.setSelectedItem(v.getGender());
            JTextArea addr = new javax.swing.JTextArea(v.getAddress(), 3, 20);
            JTextField purpose = new JTextField(v.getPurposeOfVisit());
            JTextField person = new JTextField(v.getPersonToMeet());

            JComboBox<Department> dCombo = new JComboBox<>();
            for (Department d : collegeService.listDepartments(adminId)) {
                dCombo.addItem(d);
            }
            selectComboById(dCombo, v.getDepartmentId());

            JComboBox<Course> cCombo = new JComboBox<>();
            Department sd = (Department) dCombo.getSelectedItem();
            if (sd != null) {
                for (Course c : collegeService.listCourses(sd.getId())) {
                    cCombo.addItem(c);
                }
            }
            selectComboById(cCombo, v.getCourseId());

            JComboBox<Branch> bCombo = new JComboBox<>();
            Course sc = (Course) cCombo.getSelectedItem();
            if (sc != null) {
                for (Branch b : collegeService.listBranches(sc.getId())) {
                    bCombo.addItem(b);
                }
            }
            selectComboById(bCombo, v.getBranchId());

            dCombo.addActionListener(e -> {
                cCombo.removeAllItems();
                bCombo.removeAllItems();
                Department dd = (Department) dCombo.getSelectedItem();
                if (dd == null) {
                    return;
                }
                try {
                    for (Course c : collegeService.listCourses(dd.getId())) {
                        cCombo.addItem(c);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            cCombo.addActionListener(e -> {
                bCombo.removeAllItems();
                Course cc = (Course) cCombo.getSelectedItem();
                if (cc == null) {
                    return;
                }
                try {
                    for (Branch b : collegeService.listBranches(cc.getId())) {
                        bCombo.addItem(b);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JSpinner dateSp = new JSpinner(new SpinnerDateModel(
                    Date.from(v.getVisitDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    null,
                    null,
                    java.util.Calendar.DAY_OF_MONTH));
            dateSp.setEditor(new JSpinner.DateEditor(dateSp, "yyyy-MM-dd"));
            JSpinner timeSp = new JSpinner(new SpinnerDateModel(
                    Date.from(v.getVisitDate().atTime(v.getVisitTime()).atZone(ZoneId.systemDefault()).toInstant()),
                    null,
                    null,
                    java.util.Calendar.HOUR_OF_DAY));
            timeSp.setEditor(new JSpinner.DateEditor(timeSp, "HH:mm"));

            JPanel gp = new JPanel(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(2, 2, 2, 2);
            gc.gridx = 0;
            gc.gridy = -1;
            addGrid(gp, gc, "Name", name);
            addGrid(gp, gc, "Mobile", mobile);
            addGrid(gp, gc, "Email", email);
            gc.gridx = 0;
            gc.gridy++;
            gp.add(new JLabel("Gender"), gc);
            gc.gridx = 1;
            gp.add(gender, gc);
            gc.gridx = 0;
            gc.gridy++;
            gp.add(new JLabel("Address"), gc);
            gc.gridx = 1;
            gp.add(new JScrollPane(addr), gc);
            gc.gridx = 0;
            gc.gridy++;
            gp.add(new JLabel("Department"), gc);
            gc.gridx = 1;
            gp.add(dCombo, gc);
            gc.gridx = 0;
            gc.gridy++;
            gp.add(new JLabel("Course"), gc);
            gc.gridx = 1;
            gp.add(cCombo, gc);
            gc.gridx = 0;
            gc.gridy++;
            gp.add(new JLabel("Branch"), gc);
            gc.gridx = 1;
            gp.add(bCombo, gc);
            addGrid(gp, gc, "Purpose", purpose);
            addGrid(gp, gc, "Person", person);
            gc.gridx = 0;
            gc.gridy++;
            gp.add(new JLabel("Date"), gc);
            gc.gridx = 1;
            gp.add(dateSp, gc);
            gc.gridx = 0;
            gc.gridy++;
            gp.add(new JLabel("Time"), gc);
            gc.gridx = 1;
            gp.add(timeSp, gc);

            int ok = JOptionPane.showConfirmDialog(this, new JScrollPane(gp), "Edit visitor",
                    JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) {
                return;
            }
            if (FormValidator.isBlank(name.getText()) || !FormValidator.isValidEmail(email.getText())) {
                JOptionPane.showMessageDialog(this, "Invalid name or email.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Department d = (Department) dCombo.getSelectedItem();
            Course c = (Course) cCombo.getSelectedItem();
            Branch b = (Branch) bCombo.getSelectedItem();
            if (d == null || c == null || b == null) {
                JOptionPane.showMessageDialog(this, "Select department, course, and branch.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            v.setFullName(name.getText().trim());
            v.setMobile(mobile.getText().trim());
            v.setEmail(email.getText().trim());
            v.setGender((String) gender.getSelectedItem());
            v.setAddress(addr.getText().trim());
            v.setDepartmentId(d.getId());
            v.setCourseId(c.getId());
            v.setBranchId(b.getId());
            v.setPurposeOfVisit(purpose.getText().trim());
            v.setPersonToMeet(person.getText().trim());
            Date dd = (Date) dateSp.getValue();
            v.setVisitDate(dd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            Date tt = (Date) timeSp.getValue();
            v.setVisitTime(tt.toInstant().atZone(ZoneId.systemDefault()).toLocalTime());

            visitorService.updateVisitorForAdmin(v);
            reloadVisitors();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Edit failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addGrid(JPanel gp, GridBagConstraints gc, String label, JTextField field) {
        gc.gridx = 0;
        gc.gridy++;
        gp.add(new JLabel(label), gc);
        gc.gridx = 1;
        gp.add(field, gc);
    }

    private void selectComboById(JComboBox<Department> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectComboById(JComboBox<Course> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectComboById(JComboBox<Branch> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void deleteVisitor() {
        int row = visitorTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int id = (int) visitorModel.getValueAt(visitorTable.convertRowIndexToModel(row), 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this visitor?", "Confirm", JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            visitorService.deleteVisitorForAdmin(id, adminId);
            reloadVisitors();
            reloadSeats();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleAccent(RoundedButton b) {
        b.setBackground(UITheme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    }

    private void logout() {
        SessionManager.clear();
        dispose();
        parent.setVisible(true);
    }
}