module com.ocr.javafx {

    requires javafx.controls;
    requires javafx.fxml;

    requires okhttp3;
    requires org.json;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires java.naming;
    requires java.desktop;
    requires jbcrypt;
    requires jdk.httpserver;
    requires io.github.cdimascio.dotenv.java;
    requires org.postgresql.jdbc;
//    requires com.ocr.javafx;
    opens com.ocr.javafx to javafx.fxml;

    // QUAN TRỌNG NHẤT: Cho phép Hibernate truy cập vào các Entity bằng Reflection
    opens com.ocr.javafx.entity to org.hibernate.orm.core;
    opens com.ocr.javafx.controller to javafx.fxml;


    exports com.ocr.javafx;
    opens com.ocr.javafx.controller.login to javafx.fxml;
    opens com.ocr.javafx.controller.components to javafx.fxml;
    opens com.ocr.javafx.controller.base to javafx.fxml;
    opens com.ocr.javafx.controller.timetable to javafx.fxml;
    opens com.ocr.javafx.controller.main to javafx.fxml;
    opens com.ocr.javafx.controller.views to javafx.fxml;

    opens com.ocr.javafx.image to javafx.fxml;
}