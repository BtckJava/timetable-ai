package com.ocr.javafx.enums;

public enum StudyIntensity {
    LIGHT(1, 2, "Nhẹ nhàng (1-2 ca/ngày)"),
    NORMAL(2, 3, "Bình thường (2-3 ca/ngày)"),
    INTENSIVE(3, 5, "Cày cuốc (3-5 ca/ngày)");

    private final int minSlots;
    private final int maxSlots;
    private final String displayValue;

    StudyIntensity(int minSlots, int maxSlots, String displayValue) {
        this.minSlots = minSlots;
        this.maxSlots = maxSlots;
        this.displayValue = displayValue;
    }

    public int getMinSlots() { return minSlots; }
    public int getMaxSlots() { return maxSlots; }

    @Override
    public String toString() { return displayValue; } // Để hiển thị đẹp trên JavaFX ComboBox
}