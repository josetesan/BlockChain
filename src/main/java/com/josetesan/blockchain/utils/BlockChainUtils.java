package com.josetesan.blockchain.utils;

import com.josetesan.blockchain.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.josetesan.blockchain.Constants.ALGORITHM;
import static com.josetesan.blockchain.Constants.SIGNATURE_ALGORITHM;
import static com.josetesan.blockchain.Constants.SIGNATURE_PROVIDER;

@Slf4j
public class BlockChainUtils {


    //Applies Sha256 to a string and returns the result.
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
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
            log.error("Could not parse input {}",input,e);
            throw new RuntimeException(e);
        }
    }


    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    //Applies ECDSA Signature and returns the result ( as bytes ).
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = null;
        try {
            dsa = Signature.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER);
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            output = dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    //Verifies a String signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_PROVIDER);
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Tacks in array of transactions and returns a merkle root.
    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();
        List<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }
        List<String> treeLayer = previousTreeLayer;
        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }


}
