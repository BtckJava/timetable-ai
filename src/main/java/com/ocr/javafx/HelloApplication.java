package com.ocr.javafx;

import com.ocr.javafx.service.OpenRouterAI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {

        String prompt = """
    Hãy tạo thời khóa biểu học Python trong 1 tuần.

    Yêu cầu:
    - Từ Thứ Hai đến Chủ Nhật
    - Mỗi ngày có 4 khung giờ học
    - Các khung giờ:
      08:00-09:30
      10:00-11:30
      14:00-15:30
      19:00-20:30

    Mỗi buổi học phải có:
    - time
    - activity

    Chỉ trả về JSON hợp lệ, không thêm giải thích.

    {
     "monday":[{"time":"","activity":""}],
     "tuesday":[{"time":"","activity":""}],
     "wednesday":[{"time":"","activity":""}],
     "thursday":[{"time":"","activity":""}],
     "friday":[{"time":"","activity":""}],
     "saturday":[{"time":"","activity":""}],
     "sunday":[{"time":"","activity":""}]
    }
    """;

        String res = OpenRouterAI.ask(prompt);

        System.out.println(res);
    }
}