package com.guestlog.ui.visitor;

import com.guestlog.dao.AdminDAO;
import com.guestlog.model.Admin;
import com.guestlog.model.Branch;
import com.guestlog.model.Course;
import com.guestlog.model.Department;
import com.guestlog.model.Visitor;
import com.guestlog.service.CollegeService;
import com.guestlog.service.VisitorService;
import com.guestlog.utils.FormValidator;
import com.guestlog.utils.RoundedButton;
import com.guestlog.utils.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class VisitorRegistration extends JFrame {

    private final CollegeService collegeService = new CollegeService();
    private final VisitorService visitorService = new VisitorService();
    private final AdminDAO adminDAO = new AdminDAO();

    private JComboBox<Admin> collegeCombo;
    private JComboBox<Department> deptCombo;
    private JComboBox<Course> courseCombo;
    private JComboBox<Branch> branchCombo;
    private JTextField nameField;
    private JTextField mobileField;
    private JTextField emailField;
    private JComboBox<String> genderCombo;
    private JTextArea addressArea;
    private JTextField purposeField;
    private JTextField personField;
    private JSpinner visitDateSpinner;
    private JSpinner visitTimeSpinner;

    public VisitorRegistration(JFrame parent) {
        super("GuestLog • Visitor registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(840, 720);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BG_APP);

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Visitor registration");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel personal = section("Personal details");
        personal.setLayout(new GridBagLayout());
        GridBagConstraints pg = new GridBagConstraints();
        pg.insets = new Insets(4, 4, 4, 4);
        pg.fill = GridBagConstraints.HORIZONTAL;
        pg.gridx = 0;
        pg.gridy = 0;
        personal.add(new JLabel("Full name"), pg);
        pg.gridx = 1;
        nameField = new JTextField(22);
        personal.add(nameField, pg);
        pg.gridx = 0;
        pg.gridy++;
        personal.add(new JLabel("Mobile"), pg);
        pg.gridx = 1;
        mobileField = new JTextField(22);
        personal.add(mobileField, pg);
        pg.gridx = 0;
        pg.gridy++;
        personal.add(new JLabel("Email"), pg);
        pg.gridx = 1;
        emailField = new JTextField(22);
        personal.add(emailField, pg);
        pg.gridx = 0;
        pg.gridy++;
        personal.add(new JLabel("Gender"), pg);
        pg.gridx = 1;
        genderCombo = new JComboBox<>(new String[] { "Male", "Female", "Other", "Prefer not to say" });
        personal.add(genderCombo, pg);
        pg.gridx = 0;
        pg.gridy++;
        personal.add(new JLabel("Address"), pg);
        pg.gridx = 1;
        addressArea = new JTextArea(3, 22);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        personal.add(new JScrollPane(addressArea), pg);

        JPanel academic = section("Academic selection");
        academic.setLayout(new GridBagLayout());
        GridBagConstraints ag = new GridBagConstraints();
        ag.insets = new Insets(4, 4, 4, 4);
        ag.fill = GridBagConstraints.HORIZONTAL;
        ag.gridx = 0;
        ag.gridy = 0;
        academic.add(new JLabel("College"), ag);
        ag.gridx = 1;
        collegeCombo = new JComboBox<>();
        academic.add(collegeCombo, ag);
        ag.gridx = 0;
        ag.gridy++;
        academic.add(new JLabel("Department"), ag);
        ag.gridx = 1;
        deptCombo = new JComboBox<>();
        academic.add(deptCombo, ag);
        ag.gridx = 0;
        ag.gridy++;
        academic.add(new JLabel("Course"), ag);
        ag.gridx = 1;
        courseCombo = new JComboBox<>();
        academic.add(courseCombo, ag);
        ag.gridx = 0;
        ag.gridy++;
        academic.add(new JLabel("Branch / section"), ag);
        ag.gridx = 1;
        branchCombo = new JComboBox<>();
        academic.add(branchCombo, ag);

        JPanel visit = section("Visit details");
        visit.setLayout(new GridBagLayout());
        GridBagConstraints vg = new GridBagConstraints();
        vg.insets = new Insets(4, 4, 4, 4);
        vg.fill = GridBagConstraints.HORIZONTAL;
        vg.gridx = 0;
        vg.gridy = 0;
        visit.add(new JLabel("Purpose of visit"), vg);
        vg.gridx = 1;
        purposeField = new JTextField(22);
        visit.add(purposeField, vg);
        vg.gridx = 0;
        vg.gridy++;
        visit.add(new JLabel("Person to meet"), vg);
        vg.gridx = 1;
        personField = new JTextField(22);
        visit.add(personField, vg);
        vg.gridx = 0;
        vg.gridy++;
        visit.add(new JLabel("Visit date"), vg);
        vg.gridx = 1;
        visitDateSpinner = new JSpinner(new SpinnerDateModel(
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null,
                null,
                java.util.Calendar.DAY_OF_MONTH));
        visitDateSpinner.setEditor(new JSpinner.DateEditor(visitDateSpinner, "yyyy-MM-dd"));
        visit.add(visitDateSpinner, vg);
        vg.gridx = 0;
        vg.gridy++;
        visit.add(new JLabel("Visit time"), vg);
        vg.gridx = 1;
        visitTimeSpinner = new JSpinner(new SpinnerDateModel(
                Date.from(LocalDate.now().atTime(LocalTime.of(9, 0)).atZone(ZoneId.systemDefault()).toInstant()),
                null,
                null,
                java.util.Calendar.HOUR_OF_DAY));
        visitTimeSpinner.setEditor(new JSpinner.DateEditor(visitTimeSpinner, "HH:mm"));
        visit.add(visitTimeSpinner, vg);

        gbc.gridy = 0;
        form.add(personal, gbc);
        gbc.gridy++;
        form.add(academic, gbc);
        gbc.gridy++;
        form.add(visit, gbc);

        RoundedButton save = new RoundedButton("Submit registration");
        save.setBackground(UITheme.ACCENT);
        save.setForeground(Color.WHITE);
        save.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        form.add(save, gbc);

        root.add(new JScrollPane(form), BorderLayout.CENTER);
        add(root);

        wireEvents();
        loadColleges();
        save.addActionListener(e -> submit());
    }

    private JPanel section(String title) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                UITheme.FONT_BODY,
                UITheme.TEXT_PRIMARY));
        return p;
    }

    private void wireEvents() {
        collegeCombo.addActionListener(e -> loadDepartments());
        deptCombo.addActionListener(e -> loadCourses());
        courseCombo.addActionListener(e -> loadBranches());
    }

    private void loadColleges() {
        collegeCombo.removeAllItems();
        try {
            List<Admin> admins = adminDAO.findAll();
            for (Admin a : admins) {
                collegeCombo.addItem(a);
            }
            collegeCombo.setRenderer(new javax.swing.DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(
                        javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Admin) {
                        Admin ad = (Admin) value;
                        setText(ad.getCollegeName() + " (" + ad.getUsername() + ")");
                    }
                    return this;
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Load colleges", JOptionPane.ERROR_MESSAGE);
        }
        loadDepartments();
    }

    private Admin selectedAdmin() {
        return (Admin) collegeCombo.getSelectedItem();
    }

    private void loadDepartments() {
        deptCombo.removeAllItems();
        courseCombo.removeAllItems();
        branchCombo.removeAllItems();
        Admin a = selectedAdmin();
        if (a == null) {
            return;
        }
        try {
            for (Department d : collegeService.listDepartments(a.getId())) {
                deptCombo.addItem(d);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Departments", JOptionPane.ERROR_MESSAGE);
        }
        loadCourses();
    }

    private void loadCourses() {
        courseCombo.removeAllItems();
        branchCombo.removeAllItems();
        Department d = (Department) deptCombo.getSelectedItem();
        if (d == null) {
            return;
        }
        try {
            for (Course c : collegeService.listCourses(d.getId())) {
                courseCombo.addItem(c);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Courses", JOptionPane.ERROR_MESSAGE);
        }
        loadBranches();
    }

    private void loadBranches() {
        branchCombo.removeAllItems();
        Course c = (Course) courseCombo.getSelectedItem();
        if (c == null) {
            return;
        }
        try {
            for (Branch b : collegeService.listBranches(c.getId())) {
                branchCombo.addItem(b);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Branches", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submit() {
        try {
            if (FormValidator.isBlank(nameField.getText())) {
                JOptionPane.showMessageDialog(this, "Enter full name.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!FormValidator.isValidMobile(mobileField.getText())) {
                JOptionPane.showMessageDialog(this, "Enter a valid mobile number.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!FormValidator.isValidEmail(emailField.getText())) {
                JOptionPane.showMessageDialog(this, "Enter a valid email.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (FormValidator.isBlank(addressArea.getText())) {
                JOptionPane.showMessageDialog(this, "Enter address.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Admin admin = selectedAdmin();
            Department dept = (Department) deptCombo.getSelectedItem();
            Course course = (Course) courseCombo.getSelectedItem();
            Branch branch = (Branch) branchCombo.getSelectedItem();
            if (admin == null || dept == null || course == null || branch == null) {
                JOptionPane.showMessageDialog(this, "Select college, department, course, and branch.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (course.getRemainingSeats() <= 0) {
                JOptionPane.showMessageDialog(this, "No seats available for this course.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (FormValidator.isBlank(purposeField.getText()) || FormValidator.isBlank(personField.getText())) {
                JOptionPane.showMessageDialog(this, "Enter visit purpose and person to meet.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date d = (Date) visitDateSpinner.getValue();
            LocalDate visitDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date t = (Date) visitTimeSpinner.getValue();
            LocalTime visitTime = t.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

            Visitor v = new Visitor();
            v.setAdminId(admin.getId());
            v.setFullName(nameField.getText());
            v.setMobile(mobileField.getText());
            v.setEmail(emailField.getText());
            v.setGender((String) genderCombo.getSelectedItem());
            v.setAddress(addressArea.getText());
            v.setDepartmentId(dept.getId());
            v.setCourseId(course.getId());
            v.setBranchId(branch.getId());
            v.setPurposeOfVisit(purposeField.getText());
            v.setPersonToMeet(personField.getText());
            v.setVisitDate(visitDate);
            v.setVisitTime(visitTime);

            visitorService.registerVisitor(v);
            JOptionPane.showMessageDialog(
                    this,
                    "Registration completed successfully. Your visit is recorded.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
