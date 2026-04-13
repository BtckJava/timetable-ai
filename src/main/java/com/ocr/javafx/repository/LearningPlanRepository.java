package com.ocr.javafx.repository;

import com.ocr.javafx.entity.LearningPlan;
import com.ocr.javafx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class LearningPlanRepository {

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