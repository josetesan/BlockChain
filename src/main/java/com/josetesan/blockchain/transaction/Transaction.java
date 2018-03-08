package com.josetesan.blockchain.transaction;

import com.josetesan.blockchain.BlockChain;
import com.josetesan.blockchain.utils.BlockChainUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import static com.josetesan.blockchain.Constants.MINIMUM_TRANSACTION;

@Data
@Slf4j
public class Transaction {

    private static AtomicLong  sequence = new AtomicLong(0L); // a rough count of how many transactions have been generated.

    private String transactionId; // this is also the hash of the transaction.
    private PublicKey sender; // senders address/public key.
    private PublicKey recipient; // Recipients address/public key.
    private float value;
    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.
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

    //Returns true if new transaction could be created.
    public boolean processTransaction() {

        if(!verifySignature()) {
            log.error("#Transaction Signature failed to verify");
            return false;
        }

        //gather transaction inputs (Make sure they are unspent):
        for(TransactionInput i : inputs) {
            i.setUTXO(BlockChain.getInstance(false).getUTXOs().get(i.getTransactionOutputId()));
        }

        //check if transaction is valid:
        if(getInputsValue() < MINIMUM_TRANSACTION) {
            log.error("#Transaction Inputs to small: {}" , getInputsValue());
            return false;
        }

        //generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
        transactionId = calculateHash();
        outputs.add(new TransactionOutput( this.recipient, value,transactionId)); //send value to recipient
        outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender

        //add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            BlockChain.getInstance(false).getUTXOs().put(o.getId() , o);
        }

        //remove transaction inputs from UTXO lists as spent:
        for(TransactionInput i : inputs) {
            if(i.getUTXO() == null) continue; //if Transaction can't be found skip it
            BlockChain.getInstance(false).getUTXOs().remove(i.getUTXO().getId());
        }

        return true;
    }

    //returns sum of inputs(UTXOs) values
    private float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.getUTXO() == null) continue; //if Transaction can't be found skip it
            total += i.getUTXO().getValue();
        }
        return total;
    }

    //returns sum of outputs:
    private float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.getValue();
        }
        return total;
    }
}