package com.ocr.javafx.backend.repository;

import com.ocr.javafx.backend.entity.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleSlotRepository extends JpaRepository<ScheduleSlot, Long> {
    List<ScheduleSlot> findByStudyPlanIdOrderByDateAscStartTimeAsc(Long studyPlanId);

    @Query("""
            select s from ScheduleSlot s
            where s.studyPlan.id = :studyPlanId
              and s.date between :startDate and :endDate
            order by s.date asc, s.startTime asc
            """)
    List<ScheduleSlot> findByStudyPlanInDateRange(@Param("studyPlanId") Long studyPlanId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Query("""
            select count(s) from ScheduleSlot s
            where s.studyPlan.id = :studyPlanId
              and s.date = :date
              and s.startTime < :endTime
              and s.endTime > :startTime
            """)
    long countConflicts(@Param("studyPlanId") Long studyPlanId,
                        @Param("date") LocalDate date,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);
}
