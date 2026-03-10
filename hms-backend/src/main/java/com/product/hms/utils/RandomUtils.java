package com.product.hms.utils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RandomUtils {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");


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

    /**
     * Generates a booking reference in the format: PREFIX + YYMMDD + 6 random characters
     *
     * @return A unique booking reference string
     */
    public static String generateReservationCode(String prefix) {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        return prefix + datePart + RandomUtils.generateRandomString(6);
    }
}