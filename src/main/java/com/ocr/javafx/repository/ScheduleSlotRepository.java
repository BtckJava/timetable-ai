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
                // ĐÃ SỬA: Dùng merge() thay vì save()
                // - Nếu slot chưa có ID (mới tạo) -> Insert
                // - Nếu slot đã có ID (kéo thả đổi giờ) -> Update
                session.merge(slots.get(i));

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
            throw e; // Nên throw lại để UI còn biết mà báo lỗi
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
            // ĐÃ SỬA: Dùng merge() thay vì save()
            session.merge(slot);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(ScheduleSlot slot) {
        if (slot == null || slot.getId() == null) {
            return;
        }
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ScheduleSlot managed = session.get(ScheduleSlot.class, slot.getId());
            if (managed != null) {
                // Với Hibernate 6+, dùng remove() thay vì delete() nếu IDE báo deprecated,
                // nhưng delete() vẫn chạy tốt.
                session.remove(managed);
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