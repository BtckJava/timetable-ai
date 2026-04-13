package com.ocr.javafx;

import com.ocr.javafx.controller.login.LoginController;
import com.ocr.javafx.controller.login.RegisterController;
import com.ocr.javafx.repository.UserRepository;
import com.ocr.javafx.service.AuthService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ApplicationContext applicationContext = new ApplicationContext();

        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource("login/register.fxml")
        );
        Scene scene = new Scene(loader.load(), 600, 400);

        RegisterController controller = loader.getController();
        controller.setApplicationContext(applicationContext);

/*
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("\"D:\\Code\\Java\\BTCK\\image-removebg-preview.png\""))
        );
*/

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("App cua Nhi hihi");
        stage.setScene(scene);
        stage.show();
    }
}

/*
package com.ocr.javafx;

import com.ocr.javafx.controller.UserController;
import com.ocr.javafx.service.OpenRouterAI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ocr/javafx/Timetable.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle("AI Learning Timetable");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws Exception {

        String prompt = """
                You are an AI that generates structured study schedules for a learning timetable application.
                
                            Your task is to generate a detailed study timetable based on the user's learning requirements.
                
                            The output will be stored in a database, so the format must be strictly valid JSON.
                
                            --------------------------------
                            USER REQUIREMENTS
                            --------------------------------
                
                            Domain: JAVA
                            Goal: Become proficient in Java programming for backend development.
                            DurationDays: 30 days
                            SessionsPerDay: 3
                            SessionDurationMinutes: 60-180 minutes
                            Level: Beginner
                            PreferredStudyHours: Morning / Evening
                
                            --------------------------------
                            EXISTING SCHEDULE
                            --------------------------------
                
                            The user may already have some study sessions stored in the database.
                
                            You MUST NOT create sessions that overlap with these existing sessions.
                
                            ExistingSchedule:
                            {EXISTING_SCHEDULE_JSON}
                
                            --------------------------------
                            RULES
                            --------------------------------
                
                            1. Generate a study timetable starting from today's date.
                
                            2. The timetable must cover exactly DurationDays days.
                
                            3. Each day must contain exactly SessionsPerDay study sessions.
                
                            4. Each session must include:
                
                            - date
                            - startTime
                            - endTime
                            - topic
                            - subTopic
                            - resourceUrl
                
                            5. Session duration must be between 60 and 120 minutes.
                
                            6. Sessions must NOT overlap with:
                               - ExistingSchedule
                               - other generated sessions
                
                            7. Topics must progress logically from beginner to advanced according to the domain.
                
                            8. The study plan should gradually build knowledge toward the user's goal.
                
                            9. Learning resources must be high-quality sources such as:
                               - official documentation
                               - well known tutorials
                               - trusted video resources
                
                            10. Use ISO formats:
                
                            date → YYYY-MM-DD \s
                            time → HH:mm
                
                            --------------------------------
                            OUTPUT FORMAT
                            --------------------------------
                
                            Return ONLY pure JSON.
                
                            Do NOT include:
                            - markdown
                            - explanations
                            - comments
                            - text outside JSON
                
                            Required JSON structure:
                
                            {
                              "scheduleSlots": [
                                {
                                  "date": "2026-03-14",
                                  "startTime": "08:00",
                                  "endTime": "09:30",
                                  "topic": "JavaScript Fundamentals",
                                  "subTopic": "Variables and Data Types",
                                  "resourceUrl": "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Grammar_and_types"
                                }
                              ]
                            }
    """;

        String res = OpenRouterAI.ask(prompt);

        System.out.println(res);
    }

}
 */