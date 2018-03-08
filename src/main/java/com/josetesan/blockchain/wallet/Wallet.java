package com.josetesan.blockchain.wallet;


import com.josetesan.blockchain.BlockChain;
import com.josetesan.blockchain.transaction.Transaction;
import com.josetesan.blockchain.transaction.TransactionInput;
import com.josetesan.blockchain.transaction.TransactionOutput;
import lombok.Getter;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Map;

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

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: BlockChain.getInstance(false).getUTXOs().entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
                total += UTXO.value ;
            }
        }
        return total;
    }
    //Generates and returns a new transaction from this wallet.
    public Transaction sendFunds(PublicKey _recipient, float value ) {
        if(getBalance() < value) { //gather balance and check funds.
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        //create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }

}
}
