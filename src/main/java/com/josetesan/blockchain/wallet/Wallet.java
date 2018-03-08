package com.josetesan.blockchain.wallet;


import lombok.Getter;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

import static com.josetesan.blockchain.Constants.SECURERANDOM_ALGORITHM;
import static com.josetesan.blockchain.Constants.SIGNATURE_ALGORITHM;
import static com.josetesan.blockchain.Constants.SIGNATURE_PROVIDER;
import static com.josetesan.blockchain.Constants.ELIPTIC_CURVE_ALGORITHM;

@Getter
public class Wallet implements Serializable {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Wallet() {
        generateKeyPair();
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(SIGNATURE_ALGORITHM,SIGNATURE_PROVIDER);
            SecureRandom random = SecureRandom.getInstance(SECURERANDOM_ALGORITHM);
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(ELIPTIC_CURVE_ALGORITHM);
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
