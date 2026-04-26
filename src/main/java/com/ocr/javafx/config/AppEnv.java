package com.ocr.javafx.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Đọc cấu hình theo thứ tự: biến môi trường OS → system property → file {@code .env} (thư mục chạy app, thường là project root).
 */
public final class AppEnv {

    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .directory(System.getProperty("user.dir"))
            .load();

    private AppEnv() {
    }

    public static String get(String key) {
        String v = System.getenv(key);
        if (isPresent(v)) {
            return v.trim();
        }
        v = System.getProperty(key);
        if (isPresent(v)) {
            return v.trim();
        }
        v = DOTENV.get(key);
        if (isPresent(v)) {
            return v.trim();
        }
        return null;
    }

    public static String require(String key) {
        String v = get(key);
        if (!isPresent(v)) {
            throw new IllegalStateException(
                    "Thiếu cấu hình: " + key
                            + ". Đặt trong file .env (xem .env) hoặc biến môi trường / -D" + key + "=");
        }
        return v;
    }

    public static String getOrDefault(String key, String defaultValue) {
        String v = get(key);
        return isPresent(v) ? v : defaultValue;
    }

    private static boolean isPresent(String v) {
        return v != null && !v.isBlank();
    }
}
