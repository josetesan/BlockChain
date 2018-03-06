package com.josetesan.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

public class BlockChainUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(BlockChainUtils.class);


    //Applies Sha256 to a string and returns the result.
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder(); // This will contain hash as hexadecimal
            String hex = null;
            for (byte aHash : hash) {
                hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            LOGGER.error("Could not parse input {}",input,e);
            throw new RuntimeException(e);
        }
    }




}
