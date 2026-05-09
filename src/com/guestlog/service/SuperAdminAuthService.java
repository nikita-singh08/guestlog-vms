package com.guestlog.service;

import com.guestlog.dao.SuperAdminDAO;
import com.guestlog.model.SuperAdmin;
import com.guestlog.utils.HashUtil;
import java.sql.SQLException;
import java.util.Optional;

public class SuperAdminAuthService {

    private final SuperAdminDAO superAdminDAO = new SuperAdminDAO();

    public Optional<SuperAdmin> authenticate(String username, String password) throws SQLException {
        Optional<SuperAdmin> opt = superAdminDAO.findByUsername(username);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        SuperAdmin s = opt.get();
        String hash = HashUtil.sha256Hex(password);
        if (hash.equalsIgnoreCase(s.getPasswordHash())) {
            return Optional.of(s);
        }
        return Optional.empty();
    }
}
