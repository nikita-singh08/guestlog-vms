package com.guestlog.dao;

import com.guestlog.db.DBConnection;
import com.guestlog.model.SuperAdmin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class SuperAdminDAO {

    public Optional<SuperAdmin> findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, email, created_at FROM super_admin WHERE username = ?";
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

    private SuperAdmin mapRow(ResultSet rs) throws SQLException {
        SuperAdmin s = new SuperAdmin();
        s.setId(rs.getInt("id"));
        s.setUsername(rs.getString("username"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setEmail(rs.getString("email"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            s.setCreatedAt(LocalDateTime.ofInstant(ts.toInstant(), java.time.ZoneId.systemDefault()));
        }
        return s;
    }
}
