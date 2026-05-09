package com.guestlog.utils;

import com.guestlog.model.Admin;
import com.guestlog.model.SuperAdmin;

/**
 * Holds the signed-in user for the current JVM session (desktop app).
 */
public final class SessionManager {

    private static SuperAdmin currentSuperAdmin;
    private static Admin currentAdmin;

    private SessionManager() {
    }

    public static void setSuperAdmin(SuperAdmin superAdmin) {
        currentSuperAdmin = superAdmin;
        currentAdmin = null;
    }

    public static SuperAdmin getSuperAdmin() {
        return currentSuperAdmin;
    }

    public static void setAdmin(Admin admin) {
        currentAdmin = admin;
        currentSuperAdmin = null;
    }

    public static Admin getAdmin() {
        return currentAdmin;
    }

    public static void clear() {
        currentSuperAdmin = null;
        currentAdmin = null;
    }

    public static boolean isSuperAdminLoggedIn() {
        return currentSuperAdmin != null;
    }

    public static boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }
}
