package com.ocr.javafx.service;

import com.ocr.javafx.dto.LearningPlanDTO;
import com.ocr.javafx.dto.request.LearningPlanRequest;
import com.ocr.javafx.dto.response.LearningPlanResponse;
import com.ocr.javafx.entity.LearningPlan;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.enums.LearningPlanStatus;
import com.ocr.javafx.repository.LearningPlanRepository;
import com.ocr.javafx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LearningPlanService {
    private final LearningPlanRepository repository;
    private final ScheduleSlotService slotService;

    public LearningPlanService(LearningPlanRepository repository, ScheduleSlotService slotService) {
        this.repository = repository;
        this.slotService = slotService;
    }

    public LearningPlanResponse createLearningPlan(User user, LearningPlanRequest request) {
        if (request.getTitle() == null) {
            return new LearningPlanResponse(false, "Title can not be empty");
        }
        LearningPlan plan = new LearningPlan();
        plan.setUser(user);
        plan.setTitle(request.getTitle());
        plan.setGoal(request.getGoal());
        plan.setIntensity(request.getIntensity());
        plan.setDomain(request.getDomain());
        plan.setSkills(request.getSkills());
        plan.setCreatedAt(LocalDateTime.now());
        plan.setProgress(0);
        plan.setDurationDays(request.getDurationDays());
        plan.setStatus(LearningPlanStatus.NOT_STARTED);
        repository.save(plan);
        return new LearningPlanResponse(true, "Learning Plan created successfully");
    }

    public LearningPlanResponse getAllPlans(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<LearningPlan> rawPlans = repository.findByUserId(session, userId);
            List<LearningPlanDTO> dtos = rawPlans.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return new LearningPlanResponse(true, "Fetched successfully", dtos);
        } catch (Exception e) {
            e.printStackTrace();
            return new LearningPlanResponse(false, "Lỗi khi tải danh sách: " + e.getMessage());
        }
    }

    public List<LearningPlan> getPlansForDropdown(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return repository.findByUserId(session, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    private LearningPlanDTO convertToDTO(LearningPlan plan) {
        Integer progress = plan.getProgress();
        if (progress == null) {
            progress = 0;
        }
        String startedDate = "N/A";
        LocalDate startDateLocal = null;
        if (plan.getCreatedAt() != null) {
            startDateLocal = plan.getCreatedAt().toLocalDate();
            startedDate = startDateLocal.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        long remainingDays = 0;
        if (startDateLocal != null && plan.getDurationDays() != null) {
            LocalDate endDate = startDateLocal.plusDays(plan.getDurationDays());
            LocalDate today = LocalDate.now();
            remainingDays = ChronoUnit.DAYS.between(today, endDate);
            if (remainingDays < 0) remainingDays = 0;
        }

        List<String> skillsList = new ArrayList<>();
        if (plan.getSkills() != null) {
            skillsList.addAll(plan.getSkills());
        }

        return new LearningPlanDTO(
                plan.getId(),
                plan.getTitle(),
                plan.getDomain(),
                plan.getGoal(),
                progress,
                plan.getDurationDays(),
                plan.getIntensity(),
                skillsList,
                (int) remainingDays,
                startedDate
        );
    }

    public void deletePlanById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            LearningPlan plan = session.get(LearningPlan.class, id);
            if (plan != null) {
                session.remove(plan);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public LearningPlan getPlanDetails(Long planId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return repository.findByIdWithSlots(session, planId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<LearningPlan> getActivePlans(Long userId) {
        return repository.findByUserIdAndStatus(userId, LearningPlanStatus.IN_PROGRESS);
    }

    public int calculateAndUpdateProgress(Long planId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            long total = repository.countTotalSlots(session, planId);
            if (total == 0) {
                repository.updateProgressAndStatus(planId, 0, LearningPlanStatus.NOT_STARTED);
                return 0;
            }
            long completed = repository.countCompletedSlots(session, planId);
            int progress = (int) Math.round(((double) completed / total) * 100);

            LearningPlanStatus status;
            if (progress == 0) {
                status = LearningPlanStatus.NOT_STARTED;
            } else if (progress < 100) {
                status = LearningPlanStatus.IN_PROGRESS;
            } else {
                status = LearningPlanStatus.COMPLETED;
            }
            repository.updateProgressAndStatus(planId, progress, status);
            return progress;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}