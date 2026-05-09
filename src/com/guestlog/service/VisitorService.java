package com.guestlog.service;

import com.guestlog.dao.AdminDAO;
import com.guestlog.dao.CourseDAO;
import com.guestlog.dao.VisitorDAO;
import com.guestlog.db.DBConnection;
import com.guestlog.model.Course;
import com.guestlog.model.Visitor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class VisitorService {

    private final VisitorDAO visitorDAO = new VisitorDAO();

    public void registerVisitor(Visitor v) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                CourseDAO courses = new CourseDAO();
                if (!courses.incrementFilledSeats(conn, v.getCourseId())) {
                    conn.rollback();
                    throw new SQLException("No seats available for the selected course.");
                }
                visitorDAO.insert(conn, v);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Visitor> searchAllVisitors(String q) throws SQLException {
        return visitorDAO.searchAll(q);
    }

    public int totalVisitorsForAdmin(int adminId) throws SQLException {
        return visitorDAO.countForAdmin(adminId);
    }

    public int visitorsTodayForAdmin(int adminId) throws SQLException {
        return visitorDAO.countTodayForAdmin(adminId);
    }

    public int totalVisitorsForAdmin(int adminId) throws SQLException {
        return visitorDAO.countForAdmin(adminId);
    }

    public int visitorsTodayForAdmin(int adminId) throws SQLException {
        return visitorDAO.countTodayForAdmin(adminId);
    }

    public List<Visitor> searchVisitorsForAdmin(int adminId, String q) throws SQLException {
        return visitorDAO.searchForAdmin(adminId, q);
    }

    public int countVisitorsToday() throws SQLException {
        return visitorDAO.countTodayAll();
    }

    public int totalVisitors() throws SQLException {
        String sql = "SELECT COUNT(*) FROM visitors";
        try (Connection conn = DBConnection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Object[]> departmentAnalytics() throws SQLException {
        return visitorDAO.departmentCountsAll();
    }

    public List<Object[]> branchAnalytics() throws SQLException {
        return visitorDAO.branchCountsAll();
    }

    public List<Object[]> purposeAnalytics() throws SQLException {
        return visitorDAO.purposeCountsAll();
    }

    public Optional<Visitor> findById(int id) throws SQLException {
        return visitorDAO.findById(id);
    }

    public void updateVisitorForAdmin(Visitor updated) throws SQLException {
        Optional<Visitor> oldOpt = visitorDAO.findById(updated.getId());
        if (oldOpt.isEmpty()) {
            throw new SQLException("Visitor not found.");
        }
        Visitor old = oldOpt.get();
        if (old.getAdminId() != updated.getAdminId()) {
            throw new SQLException("Unauthorized.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                CourseDAO courses = new CourseDAO();
                if (old.getCourseId() != updated.getCourseId()) {
                    if (!courses.decrementFilledSeats(conn, old.getCourseId())) {
                        conn.rollback();
                        throw new SQLException("Could not adjust old course seats.");
                    }
                    if (!courses.incrementFilledSeats(conn, updated.getCourseId())) {
                        courses.incrementFilledSeats(conn, old.getCourseId());
                        conn.rollback();
                        throw new SQLException("No seats available on the new course.");
                    }
                }
                if (!visitorDAO.update(conn, updated)) {
                    conn.rollback();
                    throw new SQLException("Update failed.");
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void deleteVisitorForAdmin(int visitorId, int adminId) throws SQLException {
        Optional<Visitor> oldOpt = visitorDAO.findById(visitorId);
        if (oldOpt.isEmpty()) {
            throw new SQLException("Visitor not found.");
        }
        Visitor old = oldOpt.get();
        if (old.getAdminId() != adminId) {
            throw new SQLException("Unauthorized.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                CourseDAO courses = new CourseDAO();
                if (!visitorDAO.delete(conn, visitorId, adminId)) {
                    conn.rollback();
                    throw new SQLException("Delete failed.");
                }
                if (!courses.decrementFilledSeats(conn, old.getCourseId())) {
                    conn.rollback();
                    throw new SQLException("Seat adjustment failed.");
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void deleteVisitorSuper(int visitorId) throws SQLException {
        Optional<Visitor> oldOpt = visitorDAO.findById(visitorId);
        if (oldOpt.isEmpty()) {
            throw new SQLException("Visitor not found.");
        }
        Visitor old = oldOpt.get();

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                CourseDAO courses = new CourseDAO();
                if (!visitorDAO.deleteSuper(conn, visitorId)) {
                    conn.rollback();
                    throw new SQLException("Delete failed.");
                }
                courses.decrementFilledSeats(conn, old.getCourseId());
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
