package Cryptography;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.regex.Pattern;


public class Cryptography {
    private static final String DELIMITER = ":";
    private static final String CREDENTIALS_FILE = "credentials";
    private static final String ENCRYPTION_ALGORITHM = "SHA-512";
    private static final int ENCRPYTION_INDEX = 0;
    private static final int SALT_INDEX = 1;

    public static Session authenticateUser(String username, String password) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            return reader.lines()
                        .map(line -> line.split(Pattern.quote(DELIMITER)))
                        .anyMatch(fields -> IntegrityMatch(username, password, fields))
                        ? new Session(username)
                        : null;
        }
    }

    public static void addUser(String username, String password) throws IOException, NoSuchAlgorithmException {
        if (doesUserExist(username, password)) {
            return;
        }

        String salt = new BigInteger(1, getRandomBytes(16)).toString(16);

        String usernameAndPasswordHash = hash(String.format(
            "%s%s%s",
            username,
            DELIMITER,
            hash(password, salt, ENCRYPTION_ALGORITHM)
        ), salt, ENCRYPTION_ALGORITHM);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE, true))) {
            writer.append(String.format(
                "%s%s%s\n",
                usernameAndPasswordHash,
                DELIMITER,
                salt
            ));
        }
    }

    public static String hash(String stringToHash, String salt, String encryptionAlgorithm) throws NoSuchAlgorithmException {
        byte[] bSalt = salt.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance(encryptionAlgorithm);
        md.update(bSalt);
        byte[] hash = md.digest(stringToHash.getBytes(StandardCharsets.UTF_8));

        return new BigInteger(1, hash).toString(16); // Ensure positive value
    }

    private static byte[] getRandomBytes(int size) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[size];
        SecureRandom.getInstanceStrong().nextBytes(bytes);
        
        return bytes;
    }

    private static boolean doesUserExist(String username, String password) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            return reader.lines()
                         .map(line -> line.split(Pattern.quote(DELIMITER)))
                         .anyMatch(fields -> IntegrityMatch(username, password, fields));
        }
    }

    private static boolean IntegrityMatch(String username, String password, String[] fields) {
        try {
            String usernameAndPasswordHash = hash(String.format(
                "%s%s%s",
                username,
                DELIMITER,
                hash(password, fields[SALT_INDEX], ENCRYPTION_ALGORITHM)
            ), fields[SALT_INDEX], ENCRYPTION_ALGORITHM);

            return fields[ENCRPYTION_INDEX].equals(usernameAndPasswordHash);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}