package com.ocr.javafx.repository;

import com.ocr.javafx.dto.response.LearningHoursResponse;
import com.ocr.javafx.entity.LearningSession;
import com.ocr.javafx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class LearningSessionRepository {
    public List<LearningHoursResponse> getLearningHoursPerDay(Long userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String hql = """
        SELECT new com.ocr.javafx.dto.response.LearningHoursResponse(
            DATE(ls.sessionTime),
            SUM(ls.durationMinutes / 60.0)
        )
        FROM LearningSession ls
        WHERE ls.user.id = :userId
          AND ls.status = 'COMPLETED'
        GROUP BY DATE(ls.sessionTime)
        ORDER BY DATE(ls.sessionTime)
    """;

        Query<LearningHoursResponse> query =
                session.createQuery(hql, LearningHoursResponse.class);

        query.setParameter("userId", userId);

        List<LearningHoursResponse> result = query.getResultList();

        session.close();
        return result;
    }

    public List<LearningSession> findByUserId(Long userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String hql = "FROM LearningSession ls WHERE ls.user.id = :userId ORDER BY ls.sessionTime DESC";

        Query<LearningSession> query = session.createQuery(hql, LearningSession.class);
        query.setParameter("userId", userId);

        List<LearningSession> result = query.getResultList();

        session.close();
        return result;
    }


    public void save(LearningSession session) {
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        hibernateSession.beginTransaction();

        hibernateSession.save(session);

        hibernateSession.getTransaction().commit();
        hibernateSession.close();
    }
}
