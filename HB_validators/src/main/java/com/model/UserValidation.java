package com.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class UserValidation {

    public static void main(String[] args) {
        // Initialize the ValidatorFactory and Validator
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        // Create SessionFactory
        try (SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory()) {
            // Open session
            try (Session session = sessionFactory.openSession()) {
                // Check for invalid user data
                System.out.println("Checking for invalid user data..");
                System.out.println("-------------------------------------");
                User invalidUser = new User(null, "", "invalidEmail"); // Create a user with invalid data
                validateAndStoreUser(validator, session, invalidUser);

                // Check for valid user data
                System.out.println("Checking for valid user data..");
                System.out.println("-------------------------------------");
                User validUser = new User(5001L, "Aakash", "akashk234@gmail.com"); // Create a user with valid data
                validateAndStoreUser(validator, session, validUser);
            }
        }
    }

    private static void validateAndStoreUser(Validator validator, Session session, User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (violations.isEmpty()) {
            System.out.println("Valid user data provided");
            storeUser(session, user);
        } else {
            System.out.println("Invalid user data found");
            for (ConstraintViolation<User> violation : violations) {
                System.out.println(violation.getMessage());
            }
        }
        System.out.println("---------------------------------");
    }

    private static void storeUser(Session session, User user) {
        session.beginTransaction();
        try {
            session.save(user);
            session.getTransaction().commit();
            System.out.println("User data stored in the database");
        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println("Failed to store user data: " + e.getMessage());
        }
    }
}
