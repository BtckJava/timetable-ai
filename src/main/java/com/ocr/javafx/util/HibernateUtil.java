package com.ocr.javafx.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            System.out.println("DB_URL = " + dotenv.get("DB_URL"));

            Class.forName("org.postgresql.Driver");

            Configuration config = new Configuration().configure();

            config.setProperty("hibernate.connection.url", dotenv.get("DB_URL"));
            config.setProperty("hibernate.connection.username", dotenv.get("DB_USER"));
            config.setProperty("hibernate.connection.password", dotenv.get("DB_PASSWORD"));

            return config.buildSessionFactory();

        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}