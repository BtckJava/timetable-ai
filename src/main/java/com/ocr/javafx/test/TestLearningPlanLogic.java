package com.ocr.javafx.test;
import com.ocr.javafx.dto.request.LearningPlanRequest;
import com.ocr.javafx.dto.response.LearningPlanResponse;
import com.ocr.javafx.dto.LearningPlanDTO;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.repository.ScheduleSlotRepository;
import com.ocr.javafx.repository.UserRepository;
import com.ocr.javafx.service.LearningPlanService;
import com.ocr.javafx.service.ScheduleSlotService;

import java.util.Arrays;

public class TestLearningPlanLogic {
    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU TEST LOGIC ===");

        UserRepository userRepo = new UserRepository();
        LearningPlanRepository planRepo = new LearningPlanRepository();
        ScheduleSlotRepository slotRepo = new ScheduleSlotRepository();
        ScheduleSlotService scheduleSlotService = new ScheduleSlotService(slotRepo);
        LearningPlanService planService = new LearningPlanService(planRepo, scheduleSlotService);

        // 2. Lấy (hoặc tạo) một User để test
        // Giả sử trong DB của bạn đã có User ID = 1 (bạn có thể thay bằng email đã đăng ký)
        User testUser = userRepo.findByEmail("test@example.com");

        if (testUser == null) {
            System.out.println("Không tìm thấy User. Hãy tạo một user trước trong DB hoặc qua AuthService.");
            return;
        }
        System.out.println("Đã lấy được User: " + testUser.getEmail());

        // 3. Test Tạo Kế hoạch mới (Create)
        System.out.println("\n--- Test Tạo Kế hoạch ---");
        LearningPlanRequest request = new LearningPlanRequest(
                "Hoàn",
                "Hoàn",
                "Hoàn",
                Arrays.asList("C++", "Dynamic Programming", "Graph Theory"),
                "CP",
                30
        );


        LearningPlanResponse createResponse = planService.createLearningPlan(testUser, request);
        if (createResponse.isSuccess()) {
            System.out.println("Ok" + createResponse.getResponse());
        } else {
            System.out.println("Lỗi tạo: " + createResponse.getResponse());
        }

        // 4. Test Lấy danh sách Kế hoạch (Read)
        System.out.println("\n--- Test Lấy danh sách Kế hoạch ---");
        LearningPlanResponse getResponse = planService.getAllPlans(testUser.getId());

        if (getResponse.isSuccess()) {
            System.out.println("Đã lấy thành công " + getResponse.getData().size() + " kế hoạch.");
            for (LearningPlanDTO dto : getResponse.getData()) {
                System.out.println(" - Kế hoạch: " + dto.getTitle() + " | Kỹ năng: " + dto.getSkills() + " | Còn lại: " + dto.getRemainingDays() + " ngày");
            }
        } else {
            System.out.println("Lỗi lấy danh sách: " + getResponse.getResponse());
        }

        System.out.println("\n=== KẾT THÚC TEST ===");

        // Thoát chương trình (Đảm bảo Hibernate session đóng lại hoàn toàn)
        System.exit(0);
    }
}