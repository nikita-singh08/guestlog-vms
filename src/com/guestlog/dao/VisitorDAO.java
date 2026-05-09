package com.guestlog.dao;

import com.guestlog.db.DBConnection;
import com.guestlog.model.Visitor;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisitorDAO {

    public Visitor insert(Connection conn, Visitor v) throws SQLException {
        String sql = "INSERT INTO visitors (admin_id, full_name, mobile, email, gender, address, "
                + "department_id, course_id, branch_id, purpose_of_visit, person_to_meet, visit_date, visit_time) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getAdminId());
            ps.setString(2, v.getFullName().trim());
            ps.setString(3, v.getMobile().trim());
            ps.setString(4, v.getEmail().trim());
            ps.setString(5, v.getGender());
            ps.setString(6, v.getAddress().trim());
            ps.setInt(7, v.getDepartmentId());
            ps.setInt(8, v.getCourseId());
            ps.setInt(9, v.getBranchId());
            ps.setString(10, v.getPurposeOfVisit().trim());
            ps.setString(11, v.getPersonToMeet().trim());
            ps.setDate(12, Date.valueOf(v.getVisitDate()));
            ps.setTime(13, Time.valueOf(v.getVisitTime()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    v.setId(keys.getInt(1));
                }
            }
        }
        return v;
    }

    public List<Visitor> searchAll(String searchText) throws SQLException {
        String like = searchText == null || searchText.isBlank() ? "%" : "%" + searchText.trim() + "%";
        String sql = "SELECT v.id, v.admin_id, v.full_name, v.mobile, v.email, v.gender, v.address, "
                + "v.department_id, v.course_id, v.branch_id, v.purpose_of_visit, v.person_to_meet, "
                + "v.visit_date, v.visit_time, v.created_at, "
                + "d.name AS dept_name, c.name AS course_name, b.name AS branch_name, a.college_name "
                + "FROM visitors v "
                + "JOIN departments d ON d.id = v.department_id "
                + "JOIN courses c ON c.id = v.course_id "
                + "JOIN branches b ON b.id = v.branch_id "
                + "JOIN admin a ON a.id = v.admin_id "
                + "WHERE v.full_name LIKE ? OR v.email LIKE ? OR v.mobile LIKE ? OR v.purpose_of_visit LIKE ? "
                + "OR a.college_name LIKE ? "
                + "ORDER BY v.visit_date DESC, v.visit_time DESC";
        return queryVisitorList(sql, ps -> {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);
        });
    }

    public List<Visitor> searchForAdmin(int adminId, String searchText) throws SQLException {
        String like = searchText == null || searchText.isBlank() ? "%" : "%" + searchText.trim() + "%";
        String sql = "SELECT v.id, v.admin_id, v.full_name, v.mobile, v.email, v.gender, v.address, "
                + "v.department_id, v.course_id, v.branch_id, v.purpose_of_visit, v.person_to_meet, "
                + "v.visit_date, v.visit_time, v.created_at, "
                + "d.name AS dept_name, c.name AS course_name, b.name AS branch_name, a.college_name "
                + "FROM visitors v "
                + "JOIN departments d ON d.id = v.department_id "
                + "JOIN courses c ON c.id = v.course_id "
                + "JOIN branches b ON b.id = v.branch_id "
                + "JOIN admin a ON a.id = v.admin_id "
                + "WHERE v.admin_id = ? AND (v.full_name LIKE ? OR v.email LIKE ? OR v.mobile LIKE ? "
                + "OR v.purpose_of_visit LIKE ?) "
                + "ORDER BY v.visit_date DESC, v.visit_time DESC";
        return queryVisitorList(sql, ps -> {
            ps.setInt(1, adminId);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);
        });
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Visitor> queryVisitorList(String sql, StatementBinder binder) throws SQLException {
        List<Visitor> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapJoinRow(rs));
                }
            }
        }
        return list;
    }

    public int countTodayAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM visitors WHERE visit_date = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Object[]> departmentCountsAll() throws SQLException {
        String sql = "SELECT d.name, COUNT(v.id) AS cnt FROM departments d "
                + "LEFT JOIN visitors v ON v.department_id = d.id GROUP BY d.id, d.name ORDER BY cnt DESC";
        return countPairs(sql);
    }

    public List<Object[]> branchCountsAll() throws SQLException {
        String sql = "SELECT CONCAT(c.name, ' - ', b.name) AS label, COUNT(v.id) AS cnt "
                + "FROM branches b "
                + "JOIN courses c ON c.id = b.course_id "
                + "LEFT JOIN visitors v ON v.branch_id = b.id "
                + "GROUP BY b.id, c.name, b.name ORDER BY cnt DESC";
        return countPairs(sql);
    }

    public List<Object[]> purposeCountsAll() throws SQLException {
        String sql = "SELECT purpose_of_visit, COUNT(*) AS cnt FROM visitors "
                + "GROUP BY purpose_of_visit ORDER BY cnt DESC";
        return countPairs(sql);
    }

    private List<Object[]> countPairs(String sql) throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new Object[] { rs.getString(1), rs.getInt(2) });
            }
        }
        return rows;
    }

    public Optional<Visitor> findById(int id) throws SQLException {
        String sql = "SELECT v.id, v.admin_id, v.full_name, v.mobile, v.email, v.gender, v.address, "
                + "v.department_id, v.course_id, v.branch_id, v.purpose_of_visit, v.person_to_meet, "
                + "v.visit_date, v.visit_time, v.created_at, "
                + "d.name AS dept_name, c.name AS course_name, b.name AS branch_name, a.college_name "
                + "FROM visitors v "
                + "JOIN departments d ON d.id = v.department_id "
                + "JOIN courses c ON c.id = v.course_id "
                + "JOIN branches b ON b.id = v.branch_id "
                + "JOIN admin a ON a.id = v.admin_id WHERE v.id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapJoinRow(rs));
            }
        }
    }

    public boolean update(Visitor v) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return update(conn, v);
        }
    }

    public boolean update(Connection conn, Visitor v) throws SQLException {
        String sql = "UPDATE visitors SET full_name=?, mobile=?, email=?, gender=?, address=?, "
                + "department_id=?, course_id=?, branch_id=?, purpose_of_visit=?, person_to_meet=?, "
                + "visit_date=?, visit_time=? WHERE id=? AND admin_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getFullName().trim());
            ps.setString(2, v.getMobile().trim());
            ps.setString(3, v.getEmail().trim());
            ps.setString(4, v.getGender());
            ps.setString(5, v.getAddress().trim());
            ps.setInt(6, v.getDepartmentId());
            ps.setInt(7, v.getCourseId());
            ps.setInt(8, v.getBranchId());
            ps.setString(9, v.getPurposeOfVisit().trim());
            ps.setString(10, v.getPersonToMeet().trim());
            ps.setDate(11, Date.valueOf(v.getVisitDate()));
            ps.setTime(12, Time.valueOf(v.getVisitTime()));
            ps.setInt(13, v.getId());
            ps.setInt(14, v.getAdminId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int id, int adminId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return delete(conn, id, adminId);
        }
    }

    public boolean delete(Connection conn, int id, int adminId) throws SQLException {
        String sql = "DELETE FROM visitors WHERE id = ? AND admin_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, adminId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteSuper(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return deleteSuper(conn, id);
        }
    }

    public boolean deleteSuper(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM visitors WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public int countForAdmin(int adminId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM visitors WHERE admin_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countTodayForAdmin(int adminId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM visitors WHERE admin_id = ? AND visit_date = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Visitor mapJoinRow(ResultSet rs) throws SQLException {
        Visitor v = new Visitor();
        v.setId(rs.getInt("id"));
        v.setAdminId(rs.getInt("admin_id"));
        v.setFullName(rs.getString("full_name"));
        v.setMobile(rs.getString("mobile"));
        v.setEmail(rs.getString("email"));
        v.setGender(rs.getString("gender"));
        v.setAddress(rs.getString("address"));
        v.setDepartmentId(rs.getInt("department_id"));
        v.setCourseId(rs.getInt("course_id"));
        v.setBranchId(rs.getInt("branch_id"));
        v.setPurposeOfVisit(rs.getString("purpose_of_visit"));
        v.setPersonToMeet(rs.getString("person_to_meet"));
        Date vd = rs.getDate("visit_date");
        if (vd != null) {
            v.setVisitDate(vd.toLocalDate());
        }
        Time vt = rs.getTime("visit_time");
        if (vt != null) {
            v.setVisitTime(vt.toLocalTime());
        }
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            v.setCreatedAt(LocalDateTime.ofInstant(ts.toInstant(), java.time.ZoneId.systemDefault()));
        }
        v.setDepartmentName(rs.getString("dept_name"));
        v.setCourseName(rs.getString("course_name"));
        v.setBranchName(rs.getString("branch_name"));
        v.setCollegeName(rs.getString("college_name"));
        return v;
    }
}
