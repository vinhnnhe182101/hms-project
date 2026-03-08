package com.product.hms.utils;

import java.security.SecureRandom;

public class RandomUtils {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a random alphanumeric string of the specified length
     *
     * @param length The length of the random string to generate
     * @return A random alphanumeric string
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(SECURE_RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}