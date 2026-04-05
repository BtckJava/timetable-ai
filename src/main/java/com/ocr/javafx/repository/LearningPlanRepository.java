package com.ocr.javafx.repository;

import com.ocr.javafx.entity.LearningPlan;
import com.ocr.javafx.util.HibernateUtil;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class LearningPlanRepository {

    public List<LearningPlan> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT lp FROM LearningPlan lp LEFT JOIN FETCH lp.skills WHERE lp.user.id = :userId";            TypedQuery<LearningPlan> query = session.createQuery(hql, LearningPlan.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public void save(LearningPlan plan) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(plan);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteById(Long planId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            LearningPlan plan = session.get(LearningPlan.class, planId);
            if (plan != null) {
                session.delete(plan);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}
