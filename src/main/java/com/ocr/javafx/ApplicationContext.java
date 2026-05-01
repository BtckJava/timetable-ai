package com.ocr.javafx;

import com.ocr.javafx.config.SessionManager;
import com.ocr.javafx.repository.LearningPlanRepository;
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
    private final LearningPlanRepository learningPlanRepository = new LearningPlanRepository();
    private final AuthService authService = new AuthService(userRepository);
    private final SessionManager sessionManager = new SessionManager();
    private final StatsRowService statsRowService = new StatsRowService(scheduleSlotRepository, learningPlanRepository);
    private final ScheduleSlotService scheduleSlotService = new ScheduleSlotService(scheduleSlotRepository);
    private final LearningPlanService learningPlanService = new LearningPlanService(learningPlanRepository, scheduleSlotService);
    private final LearningChartService learningChartService = new LearningChartService(scheduleSlotService);
    private final ProfileService profileService = new ProfileService(userRepository, learningPlanRepository, scheduleSlotRepository, sessionManager);
}
