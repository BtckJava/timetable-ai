package com.ocr.javafx.repository;

import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class ScheduleSlotRepository  {

    public void saveAll(List<ScheduleSlot> slots) throws Exception {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            for (int i = 0; i < slots.size(); i++) {
                // Lưu ý: Nếu bạn dùng Hibernate 6+ (do thấy bạn import jakarta.*),
                // có thể bạn sẽ cần đổi session.save() thành session.persist() nếu IDE báo lỗi deprecated
                session.save(slots.get(i));
                if (i % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // ĐÃ SỬA LẠI CÂU HQL Ở HÀM NÀY
    public List<ScheduleSlot> findByUserIdOrderByDateAndStart(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // SỬA TẠI ĐÂY:
            // 1. s.user.id thay vì s.userId (vì bạn map object User)
            // 2. s.startTime thay vì s.start (khớp với tên biến trong Entity)
            String hql = "FROM ScheduleSlot s WHERE s.user.id = :userId ORDER BY s.date ASC, s.startTime ASC";

            Query<ScheduleSlot> query = session.createQuery(hql, ScheduleSlot.class);
            query.setParameter("userId", userId);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void save(ScheduleSlot slot) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(slot); // Tương tự, cân nhắc đổi thành persist(slot) nếu dùng Hibernate 6
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

        public List<ScheduleSlot> findByUserId(Long userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        String hql = "FROM ScheduleSlot ls WHERE ls.user.id = :userId ORDER BY (ls.endTime - ls.startTime) DESC";

        Query<ScheduleSlot> query = session.createQuery(hql, ScheduleSlot.class);
        query.setParameter("userId", userId);

        List<ScheduleSlot> result = query.getResultList();

        session.close();
        return result;
    }
}