package com.guestlog.utils;

import java.util.regex.Pattern;

/**
 * Basic form validation helpers for visitor and admin forms.
 */
public final class FormValidator {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern MOBILE = Pattern.compile("^[0-9+\\-\\s()]{7,20}$");

    private FormValidator() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE.matcher(mobile.trim()).matches();
    }
}
