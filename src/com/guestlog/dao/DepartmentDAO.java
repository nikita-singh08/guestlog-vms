package com.guestlog.dao;

import com.guestlog.db.DBConnection;
import com.guestlog.model.Department;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDAO {

    public List<Department> findByAdminId(int adminId) throws SQLException {
        String sql = "SELECT id, admin_id, name, created_at FROM departments WHERE admin_id = ? ORDER BY name";
        List<Department> list = new ArrayList<>();
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

    public List<Department> findAll() throws SQLException {
        String sql = "SELECT id, admin_id, name, created_at FROM departments ORDER BY name";
        List<Department> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Department create(Department d) throws SQLException {
        String sql = "INSERT INTO departments (admin_id, name) VALUES (?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getAdminId());
            ps.setString(2, d.getName().trim());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    d.setId(keys.getInt(1));
                }
            }
        }
        return d;
    }

    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM departments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Department> findById(int id) throws SQLException {
        String sql = "SELECT id, admin_id, name, created_at FROM departments WHERE id = ?";
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

    private Department mapRow(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setId(rs.getInt("id"));
        d.setAdminId(rs.getInt("admin_id"));
        d.setName(rs.getString("name"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            d.setCreatedAt(LocalDateTime.ofInstant(ts.toInstant(), java.time.ZoneId.systemDefault()));
        }
        return d;
    }
}
