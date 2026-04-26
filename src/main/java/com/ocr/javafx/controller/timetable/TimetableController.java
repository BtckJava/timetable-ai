package com.ocr.javafx.controller.timetable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocr.javafx.ApplicationContext;
import com.ocr.javafx.entity.LearningPlan;
import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.service.LearningPlanService;
import com.ocr.javafx.service.OpenRouterAI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TimetableController {

    private static final DateTimeFormatter HEADER_FMT = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");
    private static final int START_HOUR = 6;
    private static final int END_HOUR = 22;

    /** Cột giờ + 7 cột ngày — đủ rộng để ScrollPane hiện thanh cuộn ngang khi cửa sổ hẹp */
    private static final double COL_TIME_WIDTH = 64;
    private static final double COL_DAY_MIN_WIDTH = 118;

    @FXML
    private GridPane timetableGrid;

    @FXML
    private ScrollPane timetableScroll;

    @FXML
    private Label monthYearLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private ComboBox<LearningPlan> planCombo;

    @FXML
    private Button btnGenerateAi;

    @FXML
    private Button btnSaveDb;

    @FXML
    private Button btnAddManual;

    private final ObservableList<ScheduleSlot> slots = FXCollections.observableArrayList();
    private final LearningPlanRepository planRepository = new LearningPlanRepository();
    private final List<ScheduleSlot> deletedSlots = new ArrayList<>();

    private ApplicationContext applicationContext;
    private LocalDate currentWeekStart;
    private ScheduleSlot draggingSlot;
    private String draggingToken;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        User user = applicationContext.getSessionManager().getCurrentUser();
        if (user != null && user.getId() != null) {
            LearningPlanService planService = applicationContext.getLearningPlanService();
            planCombo.getItems().setAll(planService.getPlansForDropdown(user.getId()));
            slots.setAll(applicationContext.getScheduleSlotRepository()
                    .findByUserIdOrderByDateAndStart(user.getId()));
        } else {
            planCombo.getItems().clear();
            slots.clear();
        }
        LocalDate today = LocalDate.now();
        currentWeekStart = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        renderTimetable();
    }

    @FXML
    public void initialize() {
        currentWeekStart = LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        planCombo.setCellFactory(lv -> new LearningPlanListCell());
        planCombo.setButtonCell(new LearningPlanListCell());
        slots.addListener((ListChangeListener.Change<? extends ScheduleSlot> c) -> renderTimetable());
    }

    @FXML
    private void onBackWeekClicked() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        renderTimetable();
    }

    @FXML
    private void onNextWeekClicked() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        renderTimetable();
    }

    @FXML
    private void onGenerateAi() {
        if (applicationContext == null) {
            return;
        }
        User user = applicationContext.getSessionManager().getCurrentUser();
        if (user == null || user.getId() == null) {
            alert(Alert.AlertType.WARNING, "Đăng nhập", "Cần đăng nhập để tạo lịch.");
            return;
        }
        LearningPlan selectedPlan = planCombo.getSelectionModel().getSelectedItem();
        if (selectedPlan == null) {
            alert(Alert.AlertType.WARNING, "Thiếu Learning Plan",
                    "Vui lòng chọn một Learning Plan trước khi tạo lịch bằng AI.");
            return;
        }

        final LocalDate weekStart = currentWeekStart;
        final LocalDate weekEnd = currentWeekStart.plusDays(selectedPlan.getDurationDays() - 1);
        final List<ScheduleSlot> busySnapshot = snapshotBusySlotsForWeek(weekStart, weekEnd);
        final String aiPrompt = buildAiPrompt(selectedPlan, weekStart, weekEnd, busySnapshot);

        setBusy(true);
        final User userRef = user;
        final LearningPlan planRef = selectedPlan;

        Task<List<ScheduleSlot>> task = new Task<>() {
            @Override
            protected List<ScheduleSlot> call() throws Exception {
                String raw = OpenRouterAI.ask(aiPrompt);
                List<ScheduleSlot> parsed = parseAiResponseToSlots(raw, weekStart, weekEnd);
                for (ScheduleSlot s : parsed) {
                    s.setUser(userRef);
                    s.setPlan(planRef);
                    s.setCompleted(false);
                }
                return parsed;
            }
        };
        task.setOnSucceeded(ev -> {
            try {
                List<ScheduleSlot> created = task.getValue();
                if (created == null || created.isEmpty()) {
                    alert(Alert.AlertType.INFORMATION, "AI", "AI không trả về slot nào trong phản hồi.");
                    return;
                }
                List<ScheduleSlot> toAdd = new ArrayList<>();
                for (ScheduleSlot s : created) {
                    if (!overlapsAnyExisting(s, Collections.emptyList(), -1)) {
                        toAdd.add(s);
                    }
                }
                if (toAdd.isEmpty()) {
                    alert(Alert.AlertType.INFORMATION, "AI",
                            "Các slot AI trả về đều trùng với lịch hiện có; không thêm slot mới.");
                } else {
                    slots.addAll(toAdd);
                }
            } finally {
                setBusy(false);
                renderTimetable();
            }
        });
        task.setOnFailed(ev -> {
            setBusy(false);
            Throwable ex = task.getException();
            if (ex != null) {
                ex.printStackTrace();
            }
            Throwable root = ex;
            if (ex != null && ex.getCause() != null) {
                root = ex.getCause();
            }
            String msg = root != null && root.getMessage() != null ? root.getMessage() : "Lỗi không xác định.";
            if (root instanceof JsonProcessingException) {
                alert(Alert.AlertType.ERROR, "AI – JSON", "Không đọc được JSON từ AI: " + msg);
            } else {
                alert(Alert.AlertType.ERROR, "AI", "Không tạo được lịch: " + msg);
            }
            renderTimetable();
        });
        Thread t = new Thread(task, "timetable-openrouter-ai");
        t.setDaemon(true);
        t.start();
    }

    private static String planDisplayName(LearningPlan plan) {
        if (plan.getTitle() != null && !plan.getTitle().isBlank()) {
            return plan.getTitle().trim();
        }
        if (plan.getGoal() != null && !plan.getGoal().isBlank()) {
            String g = plan.getGoal().trim();
            return g.length() > 120 ? g.substring(0, 117) + "…" : g;
        }
        return "Plan #" + plan.getId();
    }

    private List<ScheduleSlot> snapshotBusySlotsForWeek(LocalDate weekStart, LocalDate weekEnd) {
        return slots.stream()
                .filter(s -> s.getDate() != null
                        && !s.getDate().isBefore(weekStart)
                        && !s.getDate().isAfter(weekEnd))
                .filter(s -> s.getStartTime() != null && s.getEndTime() != null)
                .collect(Collectors.toList());
    }

    private String buildAiPrompt(LearningPlan plan, LocalDate weekStart, LocalDate weekEnd,
                                 List<ScheduleSlot> busyInWeek) {
        String planName = planDisplayName(plan);
        String busyText;
        if (busyInWeek.isEmpty()) {
            busyText = "(Không có khung giờ bận trong tuần này.)";
        } else {
            busyText = busyInWeek.stream()
                    .map(s -> s.getDate() + " " + s.getStartTime() + "–" + s.getEndTime()
                            + (s.getTopic() != null && !s.getTopic().isBlank() ? " (" + s.getTopic() + ")" : ""))
                    .collect(Collectors.joining("; "));
        }

        return "Tôi muốn học theo Learning Plan: " + planName + ".\n"
                + "Hãy sinh lịch học trong tuần này từ ngày " + weekStart + " đến " + weekEnd
                + " (cả hai ngày inclusive).\n"
                + "Tuyệt đối tránh các khung giờ đã bận sau: " + busyText + "\n\n"
                + "Yêu cầu đầu ra:\n"
                + "- Chỉ trả về MỘT mảng JSON thuần (không markdown, không giải thích, không code fence).\n"
                + "- Mỗi phần tử là object: {\"date\":\"yyyy-MM-dd\",\"startTime\":\"HH:mm\",\"endTime\":\"HH:mm\","
                + "\"topic\":\"Tên bài học\",\"subTopic\":\"(tuỳ chọn)\"}\n"
                + "- Giờ trong ngày hợp lý (ví dụ 08:00–10:00), không chồng lấn trong chính mảng bạn trả về.\n"
                + "- Chỉ dùng ngày nằm trong khoảng tuần đã nêu.\n";
    }

    /**
     * Tách mảng JSON từ nội dung message (có thể bọc trong ```json ... ```).
     */
    private static String extractJsonArrayPayload(String raw) {
        if (raw == null) {
            return "[]";
        }
        String t = raw.trim();
        int fence = t.indexOf("```");
        if (fence >= 0) {
            int lineAfter = t.indexOf('\n', fence);
            int start = lineAfter >= 0 ? lineAfter + 1 : fence + 3;
            int endFence = t.indexOf("```", start);
            if (endFence > start) {
                t = t.substring(start, endFence).trim();
            }
        }
        int lb = t.indexOf('[');
        int rb = t.lastIndexOf(']');
        if (lb >= 0 && rb > lb) {
            return t.substring(lb, rb + 1);
        }
        return t;
    }

    private static List<ScheduleSlot> parseAiResponseToSlots(String raw, LocalDate weekStart, LocalDate weekEnd)
            throws JsonProcessingException {
        if (raw != null && raw.trim().startsWith("API Error:")) {
            throw new IllegalStateException(raw.trim());
        }
        String json = extractJsonArrayPayload(raw);
        ObjectMapper mapper = new ObjectMapper();
        List<AiSlotJson> rows = mapper.readValue(json, new TypeReference<List<AiSlotJson>>() {
        });
        List<ScheduleSlot> out = new ArrayList<>();
        for (AiSlotJson row : rows) {
            if (row == null || row.date == null || row.startTime == null || row.endTime == null || row.topic == null) {
                continue;
            }
            try {
                LocalDate d = LocalDate.parse(row.date.trim());
                if (d.isBefore(weekStart) || d.isAfter(weekEnd)) {
                    continue;
                }
                LocalTime st = parseTimeFlexible(row.startTime.trim());
                LocalTime en = parseTimeFlexible(row.endTime.trim());
                if (!en.isAfter(st)) {
                    continue;
                }
                ScheduleSlot s = new ScheduleSlot();
                s.setDate(d);
                s.setStartTime(st);
                s.setEndTime(en);
                s.setTopic(row.topic.trim());
                s.setSubTopic(row.subTopic != null && !row.subTopic.isBlank() ? row.subTopic.trim() : "");
                out.add(s);
            } catch (Exception ignored) {
                // bỏ qua dòng không parse được
            }
        }
        return out;
    }

    private static LocalTime parseTimeFlexible(String s) {
        try {
            return LocalTime.parse(s);
        } catch (Exception e1) {
            try {
                return LocalTime.parse(s, DateTimeFormatter.ofPattern("H:mm"));
            } catch (Exception e2) {
                return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiSlotJson {
        @JsonProperty("date")
        String date;
        @JsonProperty("startTime")
        String startTime;
        @JsonProperty("endTime")
        String endTime;
        @JsonProperty("topic")
        String topic;
        @JsonProperty("subTopic")
        String subTopic;
    }

    @FXML
    private void onSaveToDb() {
        if (applicationContext == null) {
            return;
        }
        try {
            applicationContext.getScheduleSlotRepository().saveAll(new ArrayList<>(slots));
            flushDeletedSlotsToDatabase();
            java.util.Set<Long> planIdsToUpdate = slots.stream()
                    .map(s -> s.getPlan() != null ? s.getPlan().getId() : null)
                    .filter(java.util.Objects::nonNull)
                    .collect(java.util.stream.Collectors.toSet());
            for (Long planId : planIdsToUpdate) {
                applicationContext.getLearningPlanService().calculateAndUpdateProgress(planId);
            }
            alert(Alert.AlertType.INFORMATION, "Đã lưu", "Đã lưu " + slots.size() + " slot xuống database.");
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Lỗi lưu", e.getMessage() != null ? e.getMessage() : "Không lưu được.");
        }
    }

    @FXML
    private void onAddManual() {
        if (applicationContext == null) {
            return;
        }
        User user = applicationContext.getSessionManager().getCurrentUser();
        if (user == null || user.getId() == null) {
            alert(Alert.AlertType.WARNING, "Đăng nhập", "Cần đăng nhập.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm slot thủ công");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField startField = new TextField("9:00");
        TextField endField = new TextField("10:30");
        TextField topicField = new TextField();
        TextField subTopicField = new TextField();

        VBox form = new VBox(10,
                labeled("Ngày", datePicker),
                labeled("Bắt đầu (H:mm)", startField),
                labeled("Kết thúc (H:mm)", endField),
                labeled("Chủ đề", topicField),
                labeled("Nội dung", subTopicField));
        form.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(form);

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.addEventFilter(ActionEvent.ACTION, ev -> {
            if (!validateManualForm(datePicker, startField, endField, topicField)) {
                ev.consume();
                alert(Alert.AlertType.WARNING, "Dữ liệu", "Kiểm tra ngày, giờ (H:mm) và chủ đề.");
            }
        });

        Optional<ButtonType> r = dialog.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.OK) {
            return;
        }

        LocalDate date = datePicker.getValue();
        LocalTime start = LocalTime.parse(startField.getText().trim(), TIME_FMT);
        LocalTime end = LocalTime.parse(endField.getText().trim(), TIME_FMT);
        String topic = topicField.getText().trim();
        String sub = subTopicField.getText() != null ? subTopicField.getText().trim() : "";

        ScheduleSlot s = new ScheduleSlot();
        s.setDate(date);
        s.setStartTime(start);
        s.setEndTime(end);
        s.setTopic(topic);
        s.setSubTopic(sub);
        s.setCompleted(false);
        s.setUser(user);
        s.setPlan(planCombo.getSelectionModel().getSelectedItem());
        slots.add(s);
    }

    private static boolean validateManualForm(DatePicker datePicker, TextField startField,
                                              TextField endField, TextField topicField) {
        if (datePicker.getValue() == null) {
            return false;
        }
        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(startField.getText().trim(), TIME_FMT);
            end = LocalTime.parse(endField.getText().trim(), TIME_FMT);
        } catch (DateTimeParseException e) {
            return false;
        }
        if (!end.isAfter(start)) {
            return false;
        }
        String topic = topicField.getText() != null ? topicField.getText().trim() : "";
        return !topic.isEmpty();
    }

    private static HBox labeled(String title, javafx.scene.Node node) {
        Label l = new Label(title);
        l.setMinWidth(100);
        HBox row = new HBox(10, l, node);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(node, Priority.ALWAYS);
        return row;
    }

    /**
     * Vẽ lưới tuần hiện tại và đặt các khối {@link ScheduleSlot} (trong tuần) lên {@link #timetableGrid}.
     */
    public void renderTimetable() {
        if (timetableGrid == null || monthYearLabel == null) {
            return;
        }
        timetableGrid.getChildren().clear();
        timetableGrid.getRowConstraints().clear();
        timetableGrid.getColumnConstraints().clear();
        timetableGrid.getStyleClass().remove("calendar-grid");
        timetableGrid.getStyleClass().add("calendar-grid");

        ColumnConstraints timeCol = new ColumnConstraints(COL_TIME_WIDTH, COL_TIME_WIDTH, COL_TIME_WIDTH);
        timeCol.setHgrow(Priority.NEVER);
        timetableGrid.getColumnConstraints().add(timeCol);
        for (int i = 0; i < 7; i++) {
            ColumnConstraints dayCol = new ColumnConstraints(COL_DAY_MIN_WIDTH);
            dayCol.setMinWidth(COL_DAY_MIN_WIDTH);
            dayCol.setHgrow(Priority.ALWAYS);
            timetableGrid.getColumnConstraints().add(dayCol);
        }
        timetableGrid.setMinWidth(COL_TIME_WIDTH + 7 * COL_DAY_MIN_WIDTH);
        timetableGrid.setPrefWidth(Region.USE_COMPUTED_SIZE);
        timetableGrid.setMaxWidth(Double.MAX_VALUE);
        timetableGrid.setMaxHeight(Double.MAX_VALUE);

        RowConstraints headerRow = new RowConstraints(56, 64, 80);
        timetableGrid.getRowConstraints().add(headerRow);

        LocalDate weekEnd = currentWeekStart.plusDays(6);
        monthYearLabel.setText("Tuần: " + currentWeekStart.format(HEADER_FMT) + " – " + weekEnd.format(HEADER_FMT));

        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            VBox headerBox = new VBox(2);
            headerBox.getStyleClass().add("day-header");
            String dayName = date.getDayOfWeek().name();
            Label lblDay = new Label(dayName.charAt(0) + dayName.substring(1).toLowerCase());
            lblDay.getStyleClass().add("day-name");
            Label lblDate = new Label(date.format(DateTimeFormatter.ofPattern("EEE d/M")));
            lblDate.getStyleClass().add("day-date");
            headerBox.getChildren().addAll(lblDay, lblDate);
            timetableGrid.add(headerBox, i + 1, 0);
        }

        int rowCount = END_HOUR - START_HOUR + 1;
        for (int row = 1; row <= rowCount; row++) {
            int hour = START_HOUR + row - 1;
            RowConstraints rowConst = new RowConstraints();
            rowConst.setMinHeight(48);
            rowConst.setPrefHeight(72);
            rowConst.setMaxHeight(Double.MAX_VALUE);
            rowConst.setVgrow(Priority.ALWAYS);
            timetableGrid.getRowConstraints().add(rowConst);

            Label timeLabel = new Label(String.format("%02d:00", hour));
            timeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            timeLabel.getStyleClass().add("time-cell");
            timetableGrid.add(timeLabel, 0, row);

            for (int col = 1; col <= 7; col++) {
                Label emptyCell = new Label("-");
                emptyCell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                emptyCell.getStyleClass().add("empty-cell");
                emptyCell.setUserData(new DropCell(col, hour));
                wireDropTarget(emptyCell);
                timetableGrid.add(emptyCell, col, row);
            }
        }

        LocalDate start = currentWeekStart;
        LocalDate end = currentWeekStart.plusDays(6);
        for (ScheduleSlot slot : slots) {
            if (slot.getDate() == null || slot.getStartTime() == null || slot.getEndTime() == null) {
                continue;
            }
            if (slot.getDate().isBefore(start) || slot.getDate().isAfter(end)) {
                continue;
            }
            int startHour = slot.getStartTime().getHour();
            if (startHour < START_HOUR || startHour > END_HOUR) {
                continue;
            }
            int colIndex = slot.getDate().getDayOfWeek().getValue();
            int rowIndex = startHour - START_HOUR + 1;
            long minutes = Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
            int spanHours = (int) Math.max(1, Math.ceil(minutes / 60.0));
            if (rowIndex + spanHours - 1 > rowCount) {
                spanHours = Math.max(1, rowCount - rowIndex + 1);
            }

            VBox card = buildSlotCard(slot);
            GridPane.setMargin(card, new Insets(2, 5, 2, 5));
            timetableGrid.add(card, colIndex, rowIndex, 1, spanHours);
        }
    }

    private VBox buildSlotCard(ScheduleSlot slot) {
        VBox card = new VBox(4);
        card.getStyleClass().add("event-card");
        card.setUserData(slot);
        if (slot.isCompleted()) {
            card.setStyle("-fx-background-color: #dcfce7; -fx-border-color: #86efac; -fx-opacity: 0.95;");
        }

        Label topicLabel = new Label(slot.getTopic() != null ? slot.getTopic() : "");
        topicLabel.getStyleClass().add("event-topic");
        Label sub = new Label(slot.getSubTopic() != null ? slot.getSubTopic() : "");
        sub.getStyleClass().add("event-detail");
        sub.setWrapText(true);
        Label time = new Label(slot.getStartTime() + " – " + slot.getEndTime());
        time.getStyleClass().add("event-detail");

        Button deleteBtn = new Button("X");
        deleteBtn.setFocusTraversable(false);
        deleteBtn.setStyle(
                "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 10px; "
                        + "-fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 2 6; -fx-cursor: hand;");
        deleteBtn.setOnMouseClicked(ev -> ev.consume());
        deleteBtn.setOnAction(ev -> {
            ev.consume();
            if (slot.getId() != null && !deletedSlots.contains(slot)) {
                deletedSlots.add(slot);
            }
            slots.remove(slot);
            renderTimetable();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topRow = new HBox(6, topicLabel, spacer, deleteBtn);
        topRow.setAlignment(Pos.TOP_LEFT);
        card.getChildren().addAll(topRow, sub, time);

        card.setOnMouseClicked(ev -> {
            if (ev.isStillSincePress()) {
                openSlotDetailsDialog(slot);
            }
        });

        card.setOnDragDetected(ev -> {
            if (!ev.isPrimaryButtonDown()) {
                return;
            }
            draggingSlot = slot;
            draggingToken = UUID.randomUUID().toString();
            Dragboard db = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent cc = new ClipboardContent();
            cc.putString(draggingToken);
            db.setContent(cc);
            ev.consume();
        });
        card.setOnDragDone(ev -> {
            draggingSlot = null;
            draggingToken = null;
            ev.consume();
        });
        return card;
    }

    private void wireDropTarget(Label cell) {
        cell.setOnDragOver(ev -> {
            if (draggingSlot == null || draggingToken == null || !ev.getDragboard().hasString()) {
                return;
            }
            String token = ev.getDragboard().getString();
            if (!Objects.equals(token, draggingToken)) {
                return;
            }
            ev.acceptTransferModes(TransferMode.MOVE);
            DropCell dc = (DropCell) cell.getUserData();
            if (dc == null) {
                return;
            }
            ScheduleSlot moving = draggingSlot;
            LocalDate newDate = currentWeekStart.plusDays(dc.col() - 1L);
            LocalTime newStart = LocalTime.of(dc.hour(), moving.getStartTime().getMinute());
            LocalTime newEnd = newStart.plus(Duration.between(moving.getStartTime(), moving.getEndTime()));
            boolean conflict = conflictsWithOthers(moving, newDate, newStart, newEnd);
            if (conflict) {
                cell.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2; -fx-background-color: rgba(254,226,226,0.5);");
            } else {
                cell.setStyle("");
            }
            ev.consume();
        });
        cell.setOnDragExited(ev -> cell.setStyle(""));
        cell.setOnDragDropped(ev -> {
            Dragboard db = ev.getDragboard();
            if (draggingSlot == null || draggingToken == null || !db.hasString()) {
                ev.setDropCompleted(false);
                ev.consume();
                return;
            }
            if (!Objects.equals(db.getString(), draggingToken)) {
                ev.setDropCompleted(false);
                return;
            }
            DropCell dc = (DropCell) cell.getUserData();
            if (dc == null) {
                ev.setDropCompleted(false);
                ev.consume();
                return;
            }
            ScheduleSlot moving = draggingSlot;
            LocalDate newDate = currentWeekStart.plusDays(dc.col() - 1L);
            LocalTime newStart = LocalTime.of(dc.hour(), moving.getStartTime().getMinute());
            LocalTime newEnd = newStart.plus(Duration.between(moving.getStartTime(), moving.getEndTime()));

            // Thả vào đúng vị trí cũ => không làm gì
            if (Objects.equals(moving.getDate(), newDate)
                    && Objects.equals(moving.getStartTime(), newStart)
                    && Objects.equals(moving.getEndTime(), newEnd)) {
                ev.setDropCompleted(true);
                ev.consume();
                return;
            }

            if (conflictsWithOthers(moving, newDate, newStart, newEnd)) {
                alert(Alert.AlertType.WARNING, "Trùng lịch", "Không thể thả vào ô này vì trùng thời gian với slot khác.");
                ev.setDropCompleted(false);
                ev.consume();
                return;
            }
            moving.setDate(newDate);
            moving.setStartTime(newStart);
            moving.setEndTime(newEnd);
            ev.setDropCompleted(true);
            ev.consume();
            draggingSlot = null;
            draggingToken = null;
            renderTimetable();
        });
    }

    private boolean conflictsWithOthers(ScheduleSlot self, LocalDate date, LocalTime start, LocalTime end) {
        for (ScheduleSlot o : slots) {
            if (o == self) {
                continue;
            }
            if (o.getDate() == null || o.getStartTime() == null || o.getEndTime() == null) {
                continue;
            }
            if (!Objects.equals(o.getDate(), date)) {
                continue;
            }
            if (start.isBefore(o.getEndTime()) && o.getStartTime().isBefore(end)) {
                return true;
            }
        }
        return false;
    }

    private void openSlotDetailsDialog(ScheduleSlot slot) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết ScheduleSlot");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        String planName = "(Không có)";
        if (slot.getPlan() != null) {
            planName = planDisplayName(slot.getPlan());
        }

        CheckBox completedCheck = new CheckBox("Đã hoàn thành");
        completedCheck.setSelected(slot.isCompleted());

        VBox content = new VBox(
                8,
                new Label("Date: " + (slot.getDate() != null ? slot.getDate() : "")),
                new Label("Start Time: " + (slot.getStartTime() != null ? slot.getStartTime() : "")),
                new Label("End Time: " + (slot.getEndTime() != null ? slot.getEndTime() : "")),
                new Label("Topic: " + (slot.getTopic() != null ? slot.getTopic() : "")),
                new Label("SubTopic: " + (slot.getSubTopic() != null ? slot.getSubTopic() : "")),
                new Label("LearningPlan: " + planName),
                completedCheck
        );
        content.setPadding(new Insets(12));
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            slot.setCompleted(completedCheck.isSelected());
            renderTimetable();
        }
    }

    private void flushDeletedSlotsToDatabase() throws Exception {
        if (deletedSlots.isEmpty()) {
            return;
        }
        for (ScheduleSlot slot : new ArrayList<>(deletedSlots)) {
            applicationContext.getScheduleSlotRepository().delete(slot);
        }
        deletedSlots.clear();
    }

    /**
     * Trùng với bất kỳ slot nào trong {@link #slots} (bỏ qua chỉ số ignoreIndex nếu &gt;= 0) hoặc trong batch {@code extra}.
     */
    private boolean overlapsAnyExisting(ScheduleSlot candidate, List<ScheduleSlot> extra, int ignoreIndex) {
        for (int i = 0; i < slots.size(); i++) {
            if (i == ignoreIndex) {
                continue;
            }
            if (overlaps(candidate, slots.get(i))) {
                return true;
            }
        }
        for (ScheduleSlot o : extra) {
            if (overlaps(candidate, o)) {
                return true;
            }
        }
        return false;
    }

    private static boolean overlaps(ScheduleSlot a, ScheduleSlot b) {
        if (a.getDate() == null || b.getDate() == null
                || a.getStartTime() == null || a.getEndTime() == null
                || b.getStartTime() == null || b.getEndTime() == null) {
            return false;
        }
        if (!Objects.equals(a.getDate(), b.getDate())) {
            return false;
        }
        return a.getStartTime().isBefore(b.getEndTime()) && b.getStartTime().isBefore(a.getEndTime());
    }

    private void setBusy(boolean busy) {
        Platform.runLater(() -> {
            loadingIndicator.setVisible(busy);
            loadingIndicator.setManaged(busy);
            btnGenerateAi.setDisable(busy);
            btnSaveDb.setDisable(busy);
            btnAddManual.setDisable(busy);
            planCombo.setDisable(busy);
        });
    }

    private void alert(Alert.AlertType type, String title, String message) {
        if (Platform.isFxApplicationThread()) {
            doAlert(type, title, message);
        } else {
            Platform.runLater(() -> doAlert(type, title, message));
        }
    }

    private static void doAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private record DropCell(int col, int hour) {
    }

    private static final class LearningPlanListCell extends ListCell<LearningPlan> {
        @Override
        protected void updateItem(LearningPlan item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                String g = item.getGoal();
                String t = item.getTitle();
                if (g != null && !g.isBlank()) {
                    setText(g.length() > 48 ? g.substring(0, 45) + "…" : g);
                } else if (t != null && !t.isBlank()) {
                    setText(t);
                } else {
                    setText("Plan #" + item.getId());
                }
            }
        }
    }
}
