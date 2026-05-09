package com.guestlog.dao;

import com.guestlog.db.DBConnection;
import com.guestlog.model.Counsellor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CounsellorDAO {

    public List<Counsellor> findByAdminId(int adminId) throws SQLException {
        String sql = "SELECT id, admin_id, department_id, name, email, phone, created_at FROM counsellors "
                + "WHERE admin_id = ? ORDER BY name";
        List<Counsellor> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Counsellor create(Counsellor c) throws SQLException {
        String sql = "INSERT INTO counsellors (admin_id, department_id, name, email, phone) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getAdminId());
            if (c.getDepartmentId() != null) {
                ps.setInt(2, c.getDepartmentId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, c.getName().trim());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getPhone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getInt(1));
                }
            }
        }
        return c;
    }

    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM counsellors WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Counsellor> findById(int id) throws SQLException {
        String sql = "SELECT id, admin_id, department_id, name, email, phone, created_at FROM counsellors WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        }
    }

    private Counsellor mapRow(ResultSet rs) throws SQLException {
        Counsellor c = new Counsellor();
        c.setId(rs.getInt("id"));
        c.setAdminId(rs.getInt("admin_id"));
        int deptId = rs.getInt("department_id");
        if (rs.wasNull()) {
            c.setDepartmentId(null);
        } else {
            c.setDepartmentId(deptId);
        }
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            c.setCreatedAt(LocalDateTime.ofInstant(ts.toInstant(), java.time.ZoneId.systemDefault()));
        }
        return c;
    }
}
