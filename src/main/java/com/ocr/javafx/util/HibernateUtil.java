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

            Class.forName("org.postgresql.Driver");

            Configuration config = new Configuration().configure();

            // Chỉ ghi đè khi .env có giá trị — nếu không, giữ url/user/pass trong hibernate.cfg.xml
            String dbUrl = dotenv.get("DB_URL");
            if (dbUrl != null && !dbUrl.isBlank()) {
                config.setProperty("hibernate.connection.url", dbUrl.trim());
            }
            String dbUser = dotenv.get("DB_USER");
            if (dbUser != null && !dbUser.isBlank()) {
                config.setProperty("hibernate.connection.username", dbUser.trim());
            }
            String dbPassword = dotenv.get("DB_PASSWORD");
            if (dbPassword != null) {
                config.setProperty("hibernate.connection.password", dbPassword);
            }

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