package com.ocr.javafx.repository;

import com.ocr.javafx.entity.ScheduleSlot;
import com.ocr.javafx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ScheduleSlotRepository  {

    public void saveAll(List<ScheduleSlot> slots) throws Exception {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            for (int i = 0; i < slots.size(); i++) {
                ScheduleSlot slot = slots.get(i);
                if (slot.getId() == null) {
                    // Mới tạo: persist để id được gán trực tiếp vào chính object hiện tại.
                    session.persist(slot);
                } else {
                    // Đã có id: cập nhật.
                    ScheduleSlot managed = (ScheduleSlot) session.merge(slot);
                    // Đồng bộ lại id đề phòng object cũ bị detach/khác instance.
                    slot.setId(managed.getId());
                }

                if ((i + 1) % 20 == 0) {
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
            if (slot.getId() == null) {
                session.persist(slot);
            } else {
                ScheduleSlot managed = (ScheduleSlot) session.merge(slot);
                slot.setId(managed.getId());
            }
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

    public int deleteByPlanIdAndWeek(Long planId, LocalDate start, LocalDate end) {
        if (planId == null || start == null || end == null) {
            return 0;
        }
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "delete from ScheduleSlot s where s.plan.id = :planId and s.date between :start and :end";
            int affected = session.createMutationQuery(hql)
                    .setParameter("planId", planId)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .executeUpdate();
            transaction.commit();
            return affected;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return 0;
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