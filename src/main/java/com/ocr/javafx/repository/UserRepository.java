package com.ocr.javafx.repository;

import com.ocr.javafx.entity.User;
import com.ocr.javafx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class UserRepository {
    public User findByEmail(String email){
        Session session = HibernateUtil.getSessionFactory().openSession();
        String hql = "FROM User WHERE email = :email";
        Query<User> query = session.createQuery(hql, User.class);
        query.setParameter("email", email);
        User user = query.uniqueResult();
        session.close();
        return user;
    }

    public void save(User user){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    public void update(User user){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.update(user);

        session.getTransaction().commit();
        session.close();
    }

    public User findById(Long id){
        Session session = HibernateUtil.getSessionFactory().openSession();
        String hql = "FROM User WHERE id = :id";
        Query<User> query = session.createQuery(hql, User.class);
        query.setParameter("id", id);
        User user = query.uniqueResult();
        session.close();
        return user;
    }

}
