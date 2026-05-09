package com.guestlog.dao;

import com.guestlog.db.DBConnection;
import com.guestlog.model.Branch;
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

public class BranchDAO {

    public List<Branch> findByCourseId(int courseId) throws SQLException {
        String sql = "SELECT id, course_id, name, created_at FROM branches WHERE course_id = ? ORDER BY name";
        List<Branch> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Branch> findAll() throws SQLException {
        String sql = "SELECT id, course_id, name, created_at FROM branches ORDER BY name";
        List<Branch> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Branch create(Branch b) throws SQLException {
        String sql = "INSERT INTO branches (course_id, name) VALUES (?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getCourseId());
            ps.setString(2, b.getName().trim());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    b.setId(keys.getInt(1));
                }
            }
        }
        return b;
    }

    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM branches WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Branch> findById(int id) throws SQLException {
        String sql = "SELECT id, course_id, name, created_at FROM branches WHERE id = ?";
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

    private Branch mapRow(ResultSet rs) throws SQLException {
        Branch b = new Branch();
        b.setId(rs.getInt("id"));
        b.setCourseId(rs.getInt("course_id"));
        b.setName(rs.getString("name"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            b.setCreatedAt(LocalDateTime.ofInstant(ts.toInstant(), java.time.ZoneId.systemDefault()));
        }
        return b;
    }
}
