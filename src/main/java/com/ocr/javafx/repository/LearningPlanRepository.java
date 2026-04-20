package com.ocr.javafx.repository;

import com.ocr.javafx.entity.LearningPlan;
import com.ocr.javafx.util.HibernateUtil;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import org.hibernate.query.Query;

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

            int updatedEntities = session.createMutationQuery(
                            "DELETE FROM LearningPlan lp WHERE lp.id = :id")
                    .setParameter("id", planId)
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public long countByUserIdAndStatus(Long userId, String status) {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//
//        String hql = "SELECT COUNT(lp) FROM LearningPlan lp WHERE lp.user.id = :userId AND lp.status = :status";
//
//        Query<Long> query = session.createQuery(hql, Long.class);
//        query.setParameter("userId", userId);
//        query.setParameter("status", status);
//
//        Long result = query.uniqueResult();
//
//        session.close();
//
//        return result != null ? result : 0;

        // *** M.O.C.K.***
        return switch (status) {
            case "COMPLETED" -> 8;
            case "IN_PROGRESS" -> 3;
            default -> 0;
        };
    }

    public long countByUserId(Long userId) {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//
//        String hql = "SELECT COUNT(lp) FROM LearningPlan lp WHERE lp.user.id = :userId";
//
//        Query<Long> query = session.createQuery(hql, Long.class);
//        query.setParameter("userId", userId);
//
//        Long result = query.uniqueResult();
//
//        session.close();
//
//        return result != null ? result : 0;

        return 11;
    }
}