package com.guestlog.service;

import com.guestlog.dao.AdminDAO;
import com.guestlog.model.Admin;
import java.sql.SQLException;
import java.util.List;

public class SuperAdminService {

    private final AdminDAO adminDAO = new AdminDAO();

    public List<Admin> listAdmins() throws SQLException {
        return adminDAO.findAll();
    }

    public boolean deleteAdmin(int id) throws SQLException {
        return adminDAO.deleteById(id);
    }
}
