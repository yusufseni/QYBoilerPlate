// FlashMessageUtil.java
package com.yoesoff.plate.util;

import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Cookie;

public class FlashMessageUtil {
    public static final String FLASH_COOKIE = "FLASH_MSG";

    public static NewCookie createFlashCookie(String type, String msg) {
        String value = type + ":" + msg;
        return new NewCookie(FLASH_COOKIE, value, "/", null, "flash", 60, false, false);
    }

    public static String[] parseFlashCookie(Cookie cookie) {
        if (cookie == null || cookie.getValue() == null) return null;
        return cookie.getValue().split(":", 2);
    }

    public static NewCookie clearFlashCookie() {
        return new NewCookie(FLASH_COOKIE, "", "/", null, "flash", 0, false, false);
    }
}
