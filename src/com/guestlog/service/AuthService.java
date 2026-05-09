package com.guestlog.service;

import com.guestlog.dao.AdminDAO;
import com.guestlog.model.Admin;
import com.guestlog.utils.HashUtil;
import java.sql.SQLException;
import java.util.Optional;

public class AuthService {

    private final AdminDAO adminDAO = new AdminDAO();

    public Optional<Admin> authenticateAdmin(String username, String password) throws SQLException {
        Optional<Admin> opt = adminDAO.findByUsername(username);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        Admin a = opt.get();
        String hash = HashUtil.sha256Hex(password);
        if (hash.equalsIgnoreCase(a.getPasswordHash())) {
            return Optional.of(a);
        }
        return Optional.empty();
    }

    public Admin registerAdmin(String username, String password, String email, String collegeName) throws SQLException {
        Admin a = new Admin();
        a.setUsername(username.trim());
        a.setPasswordHash(HashUtil.sha256Hex(password));
        a.setEmail(email.trim());
        a.setCollegeName(collegeName.trim());
        return adminDAO.create(a);
    }
}
