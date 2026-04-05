package com.ocr.javafx.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");

            if (dbUrl != null) {
                configuration.setProperty("hibernate.connection.url", dbUrl);
            }
            if (dbUser != null) {
                configuration.setProperty("hibernate.connection.username", dbUser);
            }
            if (dbPassword != null) {
                configuration.setProperty("hibernate.connection.password", dbPassword);
            }

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Lỗi khởi tạo SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}