package com.ocr.javafx.backend.repository;

import com.ocr.javafx.backend.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
}
