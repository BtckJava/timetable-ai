package com.ocr.javafx.config;

import com.ocr.javafx.enums.View;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewStateManager {
    private View currentView = View.DASHBOARD;
}
