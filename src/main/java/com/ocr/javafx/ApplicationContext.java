package com.ocr.javafx;

import com.ocr.javafx.config.SessionManager;
import com.ocr.javafx.repository.LearningSessionRepository;
import com.ocr.javafx.repository.UserRepository;
import com.ocr.javafx.service.AuthService;
import com.ocr.javafx.service.LearningSessionService;
import com.ocr.javafx.service.StatsRowService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationContext {
    private final UserRepository userRepository = new UserRepository();
    private final LearningSessionRepository learningSessionRepository = new LearningSessionRepository();
    private final AuthService authService = new AuthService(userRepository);
    private final LearningSessionService learningSessionService = new LearningSessionService(learningSessionRepository);
    private final SessionManager sessionManager = new SessionManager();
    private final StatsRowService statsRowService = new StatsRowService(this.getLearningSessionRepository(), new com.ocr.javafx.repository.LearningPlanRepository());
}
