package com.jake.tradelogs.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MapperUtil {
    // ================= 유틸 메서드 =================

    public static String clean(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    public static String removeComma(String s) {
        if (s == null) return null;
        return s.replace(",", "").replace(" ", "");
    }

    public static LocalDate parseDate(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            s = s.trim().replaceAll("[^0-9]", "");
            if (s.length() == 8)
                return LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE);
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
