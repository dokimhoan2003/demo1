package com.example.demo1.utils;


import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&*?";
    public static final int PASSWORD_LENGTH = 8;

    public static String generateRandomPassword(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
