package com.josetesan.blockchain.transaction;

import com.josetesan.blockchain.utils.BlockChainUtils;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class Transaction {

    private static AtomicLong  sequence = new AtomicLong(0L); // a rough count of how many transactions have been generated.

    private String transactionId = null; // this is also the hash of the transaction.
    private PublicKey sender; // senders address/public key.
    private PublicKey recipient; // Recipients address/public key.
    private float value;
    private byte[] signature=null; // this is to prevent anybody else from spending funds in our wallet.
    private ArrayList<TransactionInput> inputs;
    private ArrayList<TransactionOutput> outputs;

    // Constructor:
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
        this.outputs = new ArrayList<>();
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calculateHash() {
        sequence.incrementAndGet(); //increase the sequence to avoid 2 identical transactions having the same hash
        final String data = String.format("%s%s%s%s",
                BlockChainUtils.getStringFromKey(sender),
                BlockChainUtils.getStringFromKey(recipient),
                Float.toString(value),
                sequence.get()
        );
        return BlockChainUtils.applySha256(data);
    }

    //Signs all the data we dont wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {
        String data = String.format("%s%s%s", BlockChainUtils.getStringFromKey(sender), BlockChainUtils.getStringFromKey(recipient), Float.toString(value));
        signature = BlockChainUtils.applyECDSASig(privateKey,data);
    }
    //Verifies the data we signed hasnt been tampered with
    public boolean verifySignature() {
        String data = String.format("%s%s%s", BlockChainUtils.getStringFromKey(sender), BlockChainUtils.getStringFromKey(recipient), Float.toString(value));
        return BlockChainUtils.verifyECDSASig(sender, data, signature);
    }
}