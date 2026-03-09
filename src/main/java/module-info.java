module com.ocr.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires org.json;


    opens com.ocr.javafx to javafx.fxml;
    exports com.ocr.javafx;
}