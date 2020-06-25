package network;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
    public static String SHA_384(String input) {
        MessageDigest sha_384 = null;
        try {
            sha_384 = MessageDigest.getInstance("SHA-384");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] hash = sha_384.digest(input.getBytes());

        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}
