package com.ocr.javafx;

import com.ocr.javafx.config.SessionManager;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.repository.LearningSessionRepository;
import com.ocr.javafx.repository.ScheduleSlotRepository;
import com.ocr.javafx.repository.UserRepository;
import com.ocr.javafx.service.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationContext {
    private final UserRepository userRepository = new UserRepository();
    private final ScheduleSlotRepository scheduleSlotRepository = new ScheduleSlotRepository();
    private final LearningSessionRepository learningSessionRepository = new LearningSessionRepository();
    private final LearningPlanRepository learningPlanRepository = new LearningPlanRepository();
    private final AuthService authService = new AuthService(userRepository);
    private final LearningSessionService learningSessionService = new LearningSessionService(learningSessionRepository);
    private final SessionManager sessionManager = new SessionManager();
    private final StatsRowService statsRowService = new StatsRowService(this.getLearningSessionRepository(), learningPlanRepository);
    private final ScheduleSlotService scheduleSlotService = new ScheduleSlotService(scheduleSlotRepository);
    private final LearningPlanService learningPlanService = new LearningPlanService(learningPlanRepository, scheduleSlotService);
}
