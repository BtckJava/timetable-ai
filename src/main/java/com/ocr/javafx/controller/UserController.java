package com.ocr.javafx.controller;

import com.ocr.javafx.entity.User;
import com.ocr.javafx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserController {
    public void saveUser(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            User user = new User();
            user.setName(name);

            session.persist(user); // Lưu vào DB
            tx.commit();

            System.out.println("User saved with ID: " + user.getName());
        }
    }
}