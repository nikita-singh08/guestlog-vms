-- GuestLog - Visitor Management System
-- MySQL 8.x schema
-- Run: mysql -u root -p < guestlog_schema.sql

CREATE DATABASE IF NOT EXISTS guestlog
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE guestlog;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS visitors;
DROP TABLE IF EXISTS counsellors;
DROP TABLE IF EXISTS branches;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS super_admin;

SET FOREIGN_KEY_CHECKS = 1;

-- Super administrators (system owners)
CREATE TABLE super_admin (
  id            INT UNSIGNED NOT NULL AUTO_INCREMENT,
  username      VARCHAR(64)  NOT NULL,
  password_hash VARCHAR(128) NOT NULL,
  email         VARCHAR(128) NOT NULL,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_super_admin_username (username)
) ENGINE=InnoDB;

-- College / institution administrators
CREATE TABLE admin (
  id             INT UNSIGNED NOT NULL AUTO_INCREMENT,
  username       VARCHAR(64)  NOT NULL,
  password_hash  VARCHAR(128) NOT NULL,
  email          VARCHAR(128) NOT NULL,
  college_name   VARCHAR(200) NOT NULL,
  created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_admin_username (username)
) ENGINE=InnoDB;

CREATE TABLE departments (
  id         INT UNSIGNED NOT NULL AUTO_INCREMENT,
  admin_id   INT UNSIGNED NOT NULL,
  name       VARCHAR(150) NOT NULL,
  created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_dept_admin_name (admin_id, name),
  CONSTRAINT fk_departments_admin
    FOREIGN KEY (admin_id) REFERENCES admin (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE courses (
  id            INT UNSIGNED NOT NULL AUTO_INCREMENT,
  department_id INT UNSIGNED NOT NULL,
  name          VARCHAR(150) NOT NULL,
  total_seats   INT UNSIGNED NOT NULL DEFAULT 60,
  filled_seats  INT UNSIGNED NOT NULL DEFAULT 0,
  created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_course_dept_name (department_id, name),
  CONSTRAINT fk_courses_department
    FOREIGN KEY (department_id) REFERENCES departments (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT chk_courses_seats CHECK (filled_seats <= total_seats)
) ENGINE=InnoDB;

CREATE TABLE branches (
  id         INT UNSIGNED NOT NULL AUTO_INCREMENT,
  course_id  INT UNSIGNED NOT NULL,
  name       VARCHAR(150) NOT NULL,
  created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_branch_course_name (course_id, name),
  CONSTRAINT fk_branches_course
    FOREIGN KEY (course_id) REFERENCES courses (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE counsellors (
  id             INT UNSIGNED NOT NULL AUTO_INCREMENT,
  admin_id       INT UNSIGNED NOT NULL,
  department_id  INT UNSIGNED NULL,
  name           VARCHAR(150) NOT NULL,
  email          VARCHAR(128) NULL,
  phone          VARCHAR(32)  NULL,
  created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_counsellors_admin
    FOREIGN KEY (admin_id) REFERENCES admin (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_counsellors_department
    FOREIGN KEY (department_id) REFERENCES departments (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE visitors (
  id                INT UNSIGNED NOT NULL AUTO_INCREMENT,
  admin_id          INT UNSIGNED NOT NULL,
  full_name         VARCHAR(200) NOT NULL,
  mobile            VARCHAR(32)  NOT NULL,
  email             VARCHAR(128) NOT NULL,
  gender            VARCHAR(20)  NOT NULL,
  address           VARCHAR(500) NOT NULL,
  department_id     INT UNSIGNED NOT NULL,
  course_id         INT UNSIGNED NOT NULL,
  branch_id         INT UNSIGNED NOT NULL,
  purpose_of_visit  VARCHAR(300) NOT NULL,
  person_to_meet    VARCHAR(200) NOT NULL,
  visit_date        DATE         NOT NULL,
  visit_time        TIME         NOT NULL,
  created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_visitors_admin (admin_id),
  KEY idx_visitors_visit_date (visit_date),
  CONSTRAINT fk_visitors_admin
    FOREIGN KEY (admin_id) REFERENCES admin (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_visitors_department
    FOREIGN KEY (department_id) REFERENCES departments (id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_visitors_course
    FOREIGN KEY (course_id) REFERENCES courses (id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_visitors_branch
    FOREIGN KEY (branch_id) REFERENCES branches (id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Seed super admin: username / password = superadmin / SuperAdmin@123
INSERT INTO super_admin (username, password_hash, email) VALUES
(
  'superadmin',
  SHA2('SuperAdmin@123', 256),
  'superadmin@guestlog.local'
);

-- Sample college admin: admin1 / Admin@123
INSERT INTO admin (username, password_hash, email, college_name) VALUES
(
  'admin1',
  SHA2('Admin@123', 256),
  'admin1@college.edu',
  'Demo College of Technology'
);

SET @demo_admin := LAST_INSERT_ID();

INSERT INTO departments (admin_id, name) VALUES
(@demo_admin, 'Computer Science'),
(@demo_admin, 'Management');

SET @dept_cs := (SELECT id FROM departments WHERE admin_id = @demo_admin AND name = 'Computer Science' LIMIT 1);
SET @dept_mgt := (SELECT id FROM departments WHERE admin_id = @demo_admin AND name = 'Management' LIMIT 1);

INSERT INTO courses (department_id, name, total_seats, filled_seats) VALUES
(@dept_cs, 'BCA', 60, 0),
(@dept_cs, 'MCA', 40, 0),
(@dept_mgt, 'BBA', 80, 0);

SET @course_bca := (SELECT id FROM courses WHERE department_id = @dept_cs AND name = 'BCA' LIMIT 1);
SET @course_mca := (SELECT id FROM courses WHERE department_id = @dept_cs AND name = 'MCA' LIMIT 1);
SET @course_bba := (SELECT id FROM courses WHERE department_id = @dept_mgt AND name = 'BBA' LIMIT 1);

INSERT INTO branches (course_id, name) VALUES
(@course_bca, 'Morning'),
(@course_bca, 'Evening'),
(@course_mca, 'Regular'),
(@course_bba, 'Regular');
