package com.guestlog.utils;

import java.awt.Color;
import java.awt.Font;

/**
 * Shared colors and fonts for a consistent modern Swing look.
 */
public final class UITheme {

    public static final Color BG_APP = new Color(245, 247, 250);
    public static final Color BG_SIDEBAR = new Color(30, 41, 59);
    public static final Color BG_CARD = Color.WHITE;
    public static final Color ACCENT = new Color(59, 130, 246);
    public static final Color ACCENT_DARK = new Color(37, 99, 235);
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color DANGER = new Color(239, 68, 68);

    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_SIDEBAR = new Font("SansSerif", Font.PLAIN, 14);

    private UITheme() {
    }
}
