package com.guestlog.dao;

import com.guestlog.db.DBConnection;
import com.guestlog.model.Course;
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

public class CourseDAO {

    public List<Course> findByDepartmentId(int departmentId) throws SQLException {
        String sql = "SELECT id, department_id, name, total_seats, filled_seats, created_at FROM courses "
                + "WHERE department_id = ? ORDER BY name";
        List<Course> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Optional<Course> findById(int id) throws SQLException {
        String sql = "SELECT id, department_id, name, total_seats, filled_seats, created_at FROM courses WHERE id = ?";
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

    public Course create(Course c) throws SQLException {
        String sql = "INSERT INTO courses (department_id, name, total_seats, filled_seats) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getDepartmentId());
            ps.setString(2, c.getName().trim());
            ps.setInt(3, c.getTotalSeats());
            ps.setInt(4, Math.max(0, c.getFilledSeats()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getInt(1));
                }
            }
        }
        return c;
    }

    public boolean updateSeats(int courseId, int totalSeats, int filledSeats) throws SQLException {
        String sql = "UPDATE courses SET total_seats = ?, filled_seats = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, totalSeats);
            ps.setInt(2, filledSeats);
            ps.setInt(3, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean incrementFilledSeats(Connection conn, int courseId) throws SQLException {
        String sql = "UPDATE courses SET filled_seats = filled_seats + 1 WHERE id = ? AND filled_seats < total_seats";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean decrementFilledSeats(Connection conn, int courseId) throws SQLException {
        String sql = "UPDATE courses SET filled_seats = filled_seats - 1 WHERE id = ? AND filled_seats > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() == 1;
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setDepartmentId(rs.getInt("department_id"));
        c.setName(rs.getString("name"));
        c.setTotalSeats(rs.getInt("total_seats"));
        c.setFilledSeats(rs.getInt("filled_seats"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            c.setCreatedAt(LocalDateTime.ofInstant(ts.toInstant(), java.time.ZoneId.systemDefault()));
        }
        return c;
    }
}
