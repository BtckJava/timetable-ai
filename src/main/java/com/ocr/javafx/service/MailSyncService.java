package com.ocr.javafx.service;

import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.util.NotificationUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class MailSyncService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000L;
    private static final int FAILURE_THRESHOLD = 3;
    private static final long CIRCUIT_OPEN_TIMEOUT_MS = 60000L;
    private static final String MAIL_SERVER_BASE = "http://localhost:8080";
    private static final String MAIL_INTERNAL_KEY = "SieuCapVipPro_2026";
    private static final DateTimeFormatter REMINDER_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final MailSyncService INSTANCE = new MailSyncService();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private int failureCount = 0;
    private long circuitOpenUntil = 0L;

    private MailSyncService() {
    }

    public static MailSyncService getInstance() {
        return INSTANCE;
    }

    /** Gộp toast UI: tối đa một thông báo tổng kết mỗi lần đồng bộ. */
    private static final class SyncUiHints {
        private volatile boolean retryToastShown;
        private volatile boolean circuitToastShown;

        void notifyRetryOnce() {
            if (retryToastShown) {
                return;
            }
            synchronized (this) {
                if (retryToastShown) {
                    return;
                }
                retryToastShown = true;
            }
            NotificationUtils.showWarning("Mail Server phản hồi chậm. Đang thử lại các yêu cầu...");
        }

        void notifyCircuitOnce() {
            if (circuitToastShown) {
                return;
            }
            synchronized (this) {
                if (circuitToastShown) {
                    return;
                }
                circuitToastShown = true;
            }
            NotificationUtils.showError("Hệ thống Mail tạm ngưng kết nối để bảo trì. Vui lòng thử lại sau!");
        }

        boolean hasCircuitToast() {
            return circuitToastShown;
        }
    }

    public void syncSlots(List<ScheduleSlot> savedSlots, List<ScheduleSlot> deletedSlots, String email) {
        SyncUiHints hints = new SyncUiHints();
        int ok = 0;
        int total = 0;

        if (deletedSlots != null) {
            for (ScheduleSlot slot : deletedSlots) {
                if (slot == null || slot.getId() == null) {
                    continue;
                }
                String uri = MAIL_SERVER_BASE + "/api/reminders/schedule/" + slot.getId();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(uri))
                        .timeout(Duration.ofSeconds(30))
                        .header("X-Internal-Key", MAIL_INTERNAL_KEY)
                        .DELETE()
                        .build();
                total++;
                if (executeRequestWithResilience(request, hints)) {
                    ok++;
                }
            }
        }

        if (savedSlots != null) {
            for (ScheduleSlot slot : savedSlots) {
                if (slot == null || slot.getId() == null || slot.isCompleted()) {
                    continue;
                }
                LocalDate date = slot.getDate();
                LocalTime startTime = slot.getStartTime();
                if (date == null || startTime == null) {
                    continue;
                }
                LocalDateTime eventStart = LocalDateTime.of(date, startTime);
                if (!eventStart.isAfter(LocalDateTime.now())) {
                    continue;
                }
                if (email == null || email.isBlank()) {
                    System.err.println("Mail sync POST skipped: missing user email for slotId=" + slot.getId());
                    continue;
                }

                LocalDateTime reminder = eventStart.minusMinutes(15);
                String reminderTimeStr = reminder.format(REMINDER_TIME_FMT);
                String topic = slot.getTopic() != null ? slot.getTopic() : "";
                String subject = "Nhắc nhở lịch học: " + topic;
                String content = "Bạn có lịch học môn " + topic + " vào lúc " + startTime;
                String body = "{"
                        + "\"slotId\":" + slot.getId() + ","
                        + "\"email\":\"" + escapeJson(email) + "\","
                        + "\"subject\":\"" + escapeJson(subject) + "\","
                        + "\"content\":\"" + escapeJson(content) + "\","
                        + "\"reminderTime\":\"" + escapeJson(reminderTimeStr) + "\""
                        + "}";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(MAIL_SERVER_BASE + "/api/reminders/schedule"))
                        .timeout(Duration.ofSeconds(30))
                        .header("X-Internal-Key", MAIL_INTERNAL_KEY)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();
                total++;
                if (executeRequestWithResilience(request, hints)) {
                    ok++;
                }
            }
        }

        if (total == 0) {
            return;
        }

        if (ok == total) {
            NotificationUtils.showSuccess("Đã đồng bộ " + total + " nhắc nhở với Mail Server.");
            return;
        }

        if (hints.hasCircuitToast()) {
            if (ok > 0) {
                NotificationUtils.showWarning("Mail Server gián đoạn: " + ok + "/" + total + " yêu cầu thành công.");
            }
            return;
        }

        if (ok == 0) {
            NotificationUtils.showError("Không đồng bộ được Mail Server (" + total + " yêu cầu thất bại).");
        } else {
            NotificationUtils.showWarning("Đồng bộ Mail: " + ok + "/" + total + " yêu cầu thành công.");
        }
    }

    /**
     * Chỉ HTTP + circuit/retry; không toast lặp (toast do {@link #syncSlots} gộp).
     */
    private boolean executeRequestWithResilience(HttpRequest request, SyncUiHints hints) {
        long now = System.currentTimeMillis();
        synchronized (this) {
            if (now < circuitOpenUntil) {
                System.err.println("Circuit Breaker is OPEN. Bỏ qua request này (Fast-fail)");
                hints.notifyCircuitOnce();
                return false;
            }
        }

        for (int i = 1; i <= MAX_RETRIES; i++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    synchronized (this) {
                        failureCount = 0;
                        circuitOpenUntil = 0L;
                    }
                    return true;
                }
                if (statusCode >= 500) {
                    System.err.println("Lỗi Mail Server (status " + statusCode + "). Thử lại lần " + i);
                    if (i < MAX_RETRIES) {
                        hints.notifyRetryOnce();
                        sleepBeforeRetry();
                    }
                    continue;
                }

                System.err.println("Mail sync thất bại với status " + statusCode + " (không retry).");
                break;
            } catch (Exception ex) {
                System.err.println("Lỗi kết nối Mail Server. Thử lại lần " + i + ": " + ex.getMessage());
                if (i < MAX_RETRIES) {
                    hints.notifyRetryOnce();
                    sleepBeforeRetry();
                }
            }
        }

        synchronized (this) {
            failureCount++;
            if (failureCount >= FAILURE_THRESHOLD) {
                circuitOpenUntil = System.currentTimeMillis() + CIRCUIT_OPEN_TIMEOUT_MS;
                System.err.println("Server liên tục không phản hồi. NGẮT CẦU DAO trong 60 giây!");
                hints.notifyCircuitOnce();
            }
        }
        return false;
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.err.println("Retry sleep bị ngắt: " + ie.getMessage());
        }
    }

    private static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
