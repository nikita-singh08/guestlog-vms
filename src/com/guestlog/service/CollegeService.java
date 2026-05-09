package com.guestlog.service;

import com.guestlog.dao.BranchDAO;
import com.guestlog.dao.CounsellorDAO;
import com.guestlog.dao.CourseDAO;
import com.guestlog.dao.DepartmentDAO;
import com.guestlog.model.Branch;
import com.guestlog.model.Counsellor;
import com.guestlog.model.Course;
import com.guestlog.model.Department;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * College admin operations: academic structure, faculty, and seat configuration.
 */
public class CollegeService {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final BranchDAO branchDAO = new BranchDAO();
    private final CounsellorDAO counsellorDAO = new CounsellorDAO();

    public List<Department> listDepartments(int adminId) throws SQLException {
        return departmentDAO.findByAdminId(adminId);
    }

    public Department addDepartment(int adminId, String name) throws SQLException {
        Department d = new Department();
        d.setAdminId(adminId);
        d.setName(name);
        return departmentDAO.create(d);
    }

    public List<Course> listCourses(int departmentId) throws SQLException {
        return courseDAO.findByDepartmentId(departmentId);
    }

    public Course addCourse(int departmentId, String name, int totalSeats) throws SQLException {
        Course c = new Course();
        c.setDepartmentId(departmentId);
        c.setName(name);
        c.setTotalSeats(totalSeats);
        c.setFilledSeats(0);
        return courseDAO.create(c);
    }

    public void updateCourseSeats(int courseId, int totalSeats, int filledSeats) throws SQLException {
        if (filledSeats > totalSeats) {
            throw new SQLException("Filled seats cannot exceed total seats.");
        }
        courseDAO.updateSeats(courseId, totalSeats, filledSeats);
    }

    public List<Branch> listBranches(int courseId) throws SQLException {
        return branchDAO.findByCourseId(courseId);
    }

    public Branch addBranch(int courseId, String name) throws SQLException {
        Branch b = new Branch();
        b.setCourseId(courseId);
        b.setName(name);
        return branchDAO.create(b);
    }

    public List<Counsellor> listCounsellors(int adminId) throws SQLException {
        return counsellorDAO.findByAdminId(adminId);
    }

    public Optional<Department> findDepartmentById(int id) throws SQLException {
        return departmentDAO.findById(id);
    }

    public Counsellor addCounsellor(int adminId, Integer departmentId, String name, String email, String phone)
            throws SQLException {
        Counsellor c = new Counsellor();
        c.setAdminId(adminId);
        c.setDepartmentId(departmentId);
        c.setName(name);
        c.setEmail(email);
        c.setPhone(phone);
        return counsellorDAO.create(c);
    }
