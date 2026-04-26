package com.ocr.javafx.repository;

import com.ocr.javafx.entity.LearningPlan;
import com.ocr.javafx.enums.LearningPlanStatus;
import com.ocr.javafx.util.HibernateUtil;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import org.hibernate.query.Query;

public class LearningPlanRepository {

    public List<LearningPlan> findByUserId(Session session, Long userId) {
        String hql = "SELECT DISTINCT lp FROM LearningPlan lp " +
                "LEFT JOIN FETCH lp.skills " +
                "WHERE lp.user.id = :userId";
        return session.createQuery(hql, LearningPlan.class)
                .setParameter("userId", userId)
                .getResultList();
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

    public LearningPlan findByIdWithSlots(Session session, Long planId) {
        session.createQuery("SELECT lp FROM LearningPlan lp LEFT JOIN FETCH lp.skills WHERE lp.id = :id", LearningPlan.class)
                .setParameter("id", planId)
                .uniqueResult();
        String hql = "SELECT lp FROM LearningPlan lp LEFT JOIN FETCH lp.slots WHERE lp.id = :id";
        return session.createQuery(hql, LearningPlan.class)
                .setParameter("id", planId)
                .uniqueResult();
    }

    public long countByUserIdAndStatus(Long userId, LearningPlanStatus status) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String hql = "SELECT COUNT(lp) FROM LearningPlan lp WHERE lp.user.id = :userId AND lp.status = :status";

        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("userId", userId);
        query.setParameter("status", status);

        Long result = query.uniqueResult();

        session.close();

        return result != null ? result : 0;

    }

    public long countByUserId(Long userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String hql = "SELECT COUNT(lp) FROM LearningPlan lp WHERE lp.user.id = :userId";

        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("userId", userId);

        Long result = query.uniqueResult();

        session.close();

        return result != null ? result : 0;
    }

    public List<LearningPlan> findByUserIdAndStatus(Long userId, LearningPlanStatus status) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = """
            SELECT DISTINCT lp
            FROM LearningPlan lp
            LEFT JOIN FETCH lp.skills
            WHERE lp.user.id = :userId AND lp.status = :status
        """;

            TypedQuery<LearningPlan> query = session.createQuery(hql, LearningPlan.class);
            query.setParameter("userId", userId);
            query.setParameter("status", status);

            return query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    public long countTotalSlots(Session session, Long planId) {
        String hql = "SELECT COUNT(s) FROM ScheduleSlot s WHERE s.plan.id = :planId";
        return session.createQuery(hql, Long.class)
                .setParameter("planId", planId)
                .uniqueResult();
    }

    public long countCompletedSlots(Session session, Long planId) {
        String hql = "SELECT COUNT(s) FROM ScheduleSlot s WHERE s.plan.id = :planId AND s.completed = true";
        return session.createQuery(hql, Long.class)
                .setParameter("planId", planId)
                .uniqueResult();
    }

    public void updateProgressAndStatus(Long planId, int progress, LearningPlanStatus status) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            LearningPlan plan = session.get(LearningPlan.class, planId);
            if (plan != null) {
                plan.setProgress(progress);
                plan.setStatus(status);
                session.update(plan);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}