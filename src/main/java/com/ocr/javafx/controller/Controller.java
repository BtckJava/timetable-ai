package com.ocr.javafx.controller;

import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.service.OpenRouterAI;
import com.ocr.javafx.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class Controller {

    @FXML
    void handleAskAI() {

        try {

            String result = OpenRouterAI.ask("Hãy khen tao đẹp trai");

            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void up(ActionEvent event) {
        System.out.println("Up button clicked");
    }

     public void down(ActionEvent event) {
        System.out.println("Down button clicked");
    }

     public void left(ActionEvent event) {
        System.out.println("Left button clicked");
    }

     public void right(ActionEvent event) {
        System.out.println("Right button clicked");
    }

    // Trong Controller của bạn
    private LocalDate currentWeekStart;

    public void initialize() {
        // Khi vừa mở app, lấy ngày thứ 2 của tuần hiện tại
        LocalDate today = LocalDate.now();
        currentWeekStart = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        renderCalendar();
    }

    // Xử lý nút Back
    public void onBackWeekClicked() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        renderCalendar();
    }

    // Xử lý nút Next
    public void onNextWeekClicked() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        renderCalendar();
    }

    public List<ScheduleSlot> getSlotsForWeek(LocalDate startOfWeek) {
        LocalDate endOfWeek = startOfWeek.plusDays(6); // Chủ nhật

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ScheduleSlot s WHERE s.date BETWEEN :start AND :end";
            return session.createQuery(hql, ScheduleSlot.class)
                    .setParameter("start", startOfWeek)
                    .setParameter("end", endOfWeek)
                    .list();
        }
    }

    @FXML
    private GridPane timetableGrid; // Grid kết nối từ FXML
    @FXML
    private Label monthYearLabel;   // Header hiển thị "Tháng 3 - 2026"
    private final int START_HOUR = 6; // Bắt đầu từ 06:00 như trong ảnh
    private final int END_HOUR = 22;  // Kết thúc lúc 22:00

    private void renderCalendar() {
        timetableGrid.getChildren().clear();
        timetableGrid.getRowConstraints().clear();
        timetableGrid.getColumnConstraints().clear();
        timetableGrid.getStyleClass().add("calendar-grid");

        // Ép chiều rộng: Cột giờ (nhỏ) + 7 cột ngày (bằng nhau)
        ColumnConstraints timeCol = new ColumnConstraints(60); // Rộng 60px
        timetableGrid.getColumnConstraints().add(timeCol);
        for (int i = 0; i < 7; i++) {
            ColumnConstraints dayCol = new ColumnConstraints();
            dayCol.setPercentWidth(100.0 / 7);
            timetableGrid.getColumnConstraints().add(dayCol);
        }

        // 1. Cập nhật Header text (Ví dụ: Week: Mar 9 - Mar 15)
        DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("MMM d");
        monthYearLabel.setText("Week: " + currentWeekStart.format(headerFormatter) + " - " + currentWeekStart.plusDays(6).format(headerFormatter));

        // 2. Vẽ Hàng Tiêu đề (Ngày trong tuần)
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);

            VBox headerBox = new VBox(2);
            headerBox.getStyleClass().add("day-header");

            Label lblDay = new Label(date.getDayOfWeek().name().substring(0, 1).toUpperCase() + date.getDayOfWeek().name().substring(1).toLowerCase());
            lblDay.getStyleClass().add("day-name");

            Label lblDate = new Label(date.format(headerFormatter));
            lblDate.getStyleClass().add("day-date");

            headerBox.getChildren().addAll(lblDay, lblDate);
            timetableGrid.add(headerBox, i + 1, 0); // Hàng 0, Cột i+1
        }

        // 3. Vẽ Cột Thời gian và Lưới nền (Empty cells)
        int rowCount = END_HOUR - START_HOUR + 1;
        for (int row = 1; row <= rowCount; row++) {
            int currentHour = START_HOUR + row - 1;

            // Cố định chiều cao mỗi hàng (ví dụ 80px để card có không gian hiển thị)
            RowConstraints rowConst = new RowConstraints();
            rowConst.setMinHeight(80);
            timetableGrid.getRowConstraints().add(rowConst);

            // Vẽ nhãn thời gian bên trái
            Label timeLabel = new Label(String.format("%02d:00", currentHour));
            timeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            timeLabel.getStyleClass().add("time-cell");
            timetableGrid.add(timeLabel, 0, row);

            // Vẽ các ô trống có dấu "-" cho 7 ngày
            for (int col = 1; col <= 7; col++) {
                Label emptyCell = new Label("-");
                emptyCell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                emptyCell.getStyleClass().add("empty-cell");
                timetableGrid.add(emptyCell, col, row);
            }
        }

        // 4. Map Data lên Lưới (Overlays)
        List<ScheduleSlot> slots = getSlotsForWeek(currentWeekStart);

        for (ScheduleSlot slot : slots) {
            int startHour = slot.getStartTime().getHour();
            int endHour = slot.getEndTime().getHour();

            // Bỏ qua nếu nằm ngoài khung giờ hiển thị
            if (startHour < START_HOUR || startHour > END_HOUR) continue;

            int colIndex = slot.getDate().getDayOfWeek().getValue(); // MONDAY = 1
            int rowIndex = startHour - START_HOUR + 1;
            int durationInHours = Math.max(1, endHour - startHour); // Span bao nhiêu hàng

            // Khởi tạo thẻ sự kiện
            VBox eventCard = new VBox(4);
            eventCard.getStyleClass().add("event-card");

            Label topicLabel = new Label(slot.getTopic());
            topicLabel.getStyleClass().add("event-topic");

            Label subtopicLabel = new Label("Subtopic: " + (slot.getSubTopic() != null ? slot.getSubTopic() : "N/A"));
            subtopicLabel.getStyleClass().add("event-detail");
            subtopicLabel.setWrapText(true);

            Label durationLabel = new Label("Duration: " + durationInHours + "h");
            durationLabel.getStyleClass().add("event-detail");

            eventCard.getChildren().addAll(topicLabel, subtopicLabel, durationLabel);

            // Margin để Card không dính sát vào viền của Grid tạo cảm giác "nổi"
            GridPane.setMargin(eventCard, new Insets(2, 5, 2, 5));

            timetableGrid.add(eventCard, colIndex, rowIndex, 1, durationInHours);
        }
    }
}
