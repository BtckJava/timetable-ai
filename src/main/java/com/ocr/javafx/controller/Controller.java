package com.ocr.javafx.controller;

import com.ocr.javafx.service.OpenRouterAI;
import javafx.fxml.FXML;

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
}
