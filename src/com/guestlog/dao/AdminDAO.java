package com.guestlog.dao;

import com.guestlog.db.DBConnection;
import com.guestlog.model.Admin;
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

public class AdminDAO {

    public Optional<Admin> findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, college_name, created_at FROM admin WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        }
    }

    public List<Admin> findAll() throws SQLException {
        String sql = "SELECT id, username, password_hash, email, college_name, created_at FROM admin ORDER BY id";
        List<Admin> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Admin create(Admin admin) throws SQLException {
        String sql = "INSERT INTO admin (username, password_hash, email, college_name) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPasswordHash());
            ps.setString(3, admin.getEmail());
            ps.setString(4, admin.getCollegeName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    admin.setId(keys.getInt(1));
                }
            }
        }
        return admin;
    }

    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM admin WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Admin> findById(int id) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, college_name, created_at FROM admin WHERE id = ?";
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

    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getInt("id"));
        a.setUsername(rs.getString("username"));
        a.setPasswordHash(rs.getString("password_hash"));
        a.setEmail(rs.getString("email"));
        a.setCollegeName(rs.getString("college_name"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            a.setCreatedAt(LocalDateTime.ofInstant(ts.toInstant(), java.time.ZoneId.systemDefault()));
        }
        return a;
    }
}
