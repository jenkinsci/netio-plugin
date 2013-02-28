package com.tngtech.internal.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashHelper {
    public String hashString(String stringToHash, String algorithm) {
        try {
            return doHashString(stringToHash, algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String doHashString(String stringToHash, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] digestedBytes = digest.digest(stringToHash.getBytes());

        StringBuilder stringBuilder = new StringBuilder();
        for (byte digestedByte : digestedBytes) {
            stringBuilder.append(Integer.toString((digestedByte & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuilder.toString();
    }
}
