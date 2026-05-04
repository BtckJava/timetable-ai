package com.ocr.javafx.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class NotificationUtils {

    private static final double TOAST_WIDTH = 360;
    private static final double TOAST_MARGIN = 20;
    private static final double TOAST_SPACING = 10;
    private static final Duration TOAST_VISIBLE_DURATION = Duration.seconds(10);
    private static final Duration TOAST_FADE_DURATION = Duration.millis(450);
    private static final List<Popup> ACTIVE_TOASTS = new ArrayList<>();

    private NotificationUtils() {
    }

    public static void showSuccess(String message) {
        showToast("\u2714", sanitize(message), "#16a34a", "#f0fdf4", "#bbf7d0");
    }

    public static void showWarning(String message) {
        showToast("\u26A0", sanitize(message), "#b45309", "#fffbeb", "#fde68a");
    }

    public static void showError(String message) {
        showToast("\u2716", sanitize(message), "#b91c1c", "#fef2f2", "#fecaca");
    }

    private static String sanitize(String message) {
        return message == null || message.isBlank() ? "Thông báo hệ thống." : message.trim();
    }

    private static void showToast(String icon, String message, String textColor, String bgColor, String borderColor) {
        Platform.runLater(() -> {
            Label iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: " + textColor + ";");

            Label textLabel = new Label(message);
            textLabel.setWrapText(true);
            textLabel.setMaxWidth(TOAST_WIDTH - 70);
            textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: " + textColor + ";");
            HBox.setHgrow(textLabel, Priority.ALWAYS);

            HBox root = new HBox(10, iconLabel, textLabel);
            root.setPadding(new Insets(12, 14, 12, 14));
            root.setMaxWidth(TOAST_WIDTH);
            root.setMinWidth(TOAST_WIDTH);
            root.setMouseTransparent(true);
            root.setStyle(
                    "-fx-background-color: " + bgColor + ";"
                            + "-fx-background-radius: 12;"
                            + "-fx-border-color: " + borderColor + ";"
                            + "-fx-border-radius: 12;"
                            + "-fx-border-width: 1;"
                            + "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.25), 18, 0.18, 0, 3);"
            );

            Popup popup = new Popup();
            popup.setAutoFix(true);
            popup.setAutoHide(false);
            popup.setHideOnEscape(false);
            popup.setConsumeAutoHidingEvents(false);
            popup.getContent().add(root);

            Window owner = resolveOwnerWindow();
            if (owner == null) {
                return;
            }
            popup.show(owner);

            synchronized (ACTIVE_TOASTS) {
                ACTIVE_TOASTS.add(popup);
                if (ACTIVE_TOASTS.size() > 4) {
                    Popup oldest = ACTIVE_TOASTS.remove(0);
                    oldest.hide();
                }
                repositionToasts();
            }

            FadeTransition fadeIn = new FadeTransition(Duration.millis(180), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            PauseTransition wait = new PauseTransition(TOAST_VISIBLE_DURATION);

            FadeTransition fadeOut = new FadeTransition(TOAST_FADE_DURATION, root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                popup.hide();
                synchronized (ACTIVE_TOASTS) {
                    ACTIVE_TOASTS.remove(popup);
                    repositionToasts();
                }
            });

            new SequentialTransition(fadeIn, wait, fadeOut).play();
        });
    }

    private static Window resolveOwnerWindow() {
        for (Window window : Window.getWindows()) {
            if (window != null && window.isShowing()) {
                return window;
            }
        }
        return null;
    }

    private static void repositionToasts() {
        cleanupHiddenToasts();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        double x = bounds.getMaxX() - TOAST_WIDTH - TOAST_MARGIN;
        double y = bounds.getMaxY() - TOAST_MARGIN;

        for (int i = ACTIVE_TOASTS.size() - 1; i >= 0; i--) {
            Popup popup = ACTIVE_TOASTS.get(i);
            if (popup.getContent().isEmpty()) {
                continue;
            }
            double h = popup.getContent().get(0).prefHeight(TOAST_WIDTH);
            y -= h;
            popup.setX(x);
            popup.setY(y);
            y -= TOAST_SPACING;
        }
    }

    private static void cleanupHiddenToasts() {
        Iterator<Popup> it = ACTIVE_TOASTS.iterator();
        while (it.hasNext()) {
            Popup popup = it.next();
            if (popup == null || !popup.isShowing()) {
                it.remove();
            }
        }
    }
}
