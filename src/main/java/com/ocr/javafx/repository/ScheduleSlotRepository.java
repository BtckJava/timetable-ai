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

    public List<ScheduleSlot> findByUserIdOrderByDateAndStart(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

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
            session.save(slot);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(ScheduleSlot slot) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Sử dụng merge để "đính" lại object vào session trước khi xóa
            // Nếu bạn dùng Hibernate 6, có thể thay session.delete bằng session.remove
            session.delete(session.contains(slot) ? slot : session.merge(slot));

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
    public double sumTotalHoursByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Lấy tất cả các slot của user thông qua LearningPlan
            String hql = "SELECT s.startTime, s.endTime FROM ScheduleSlot s WHERE s.learningPlan.user.id = :userId";
            List<Object[]> results = session.createQuery(hql).setParameter("userId", userId).getResultList();

            double totalMinutes = 0;
            for (Object[] row : results) {
                java.time.LocalTime start = (java.time.LocalTime) row[0];
                java.time.LocalTime end = (java.time.LocalTime) row[1];
                if (start != null && end != null) {
                    totalMinutes += java.time.Duration.between(start, end).toMinutes();
                }
            }
            return totalMinutes / 60.0; // Đổi ra giờ
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public List<java.time.LocalDate> findDistinctDatesByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Lấy các ngày không trùng lặp, sắp xếp từ mới nhất đến cũ nhất
            String hql = "SELECT DISTINCT s.date FROM ScheduleSlot s " +
                    "WHERE s.learningPlan.user.id = :userId " +
                    "ORDER BY s.date DESC";

            return session.createQuery(hql, java.time.LocalDate.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
}