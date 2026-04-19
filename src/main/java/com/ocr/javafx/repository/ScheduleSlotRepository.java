package com.ocr.javafx.repository;

import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.entity.User;
import com.ocr.javafx.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class ScheduleSlotRepository {

    public List<ScheduleSlot> findByUserIdOrderByDateAndStart(Long userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            String hql = """
                    FROM ScheduleSlot s
                    WHERE s.user.id = :uid
                    ORDER BY s.date ASC, s.startTime ASC
                    """;
            return session.createQuery(hql, ScheduleSlot.class)
                    .setParameter("uid", userId)
                    .getResultList();
        } finally {
            session.close();
        }
    }

    public void save(ScheduleSlot slot) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            if (slot.getUser() != null && slot.getUser().getId() != null) {
                User userRef = session.getReference(User.class, slot.getUser().getId());
                slot.setUser(userRef);
            }
            if (slot.getId() == null) {
                session.persist(slot);
            } else {
                session.merge(slot);
            }
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
