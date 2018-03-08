package com.josetesan.blockchain.transaction;

import com.josetesan.blockchain.utils.BlockChainUtils;
import lombok.Value;

import java.security.PublicKey;

@Value
public class TransactionOutput {

    private String id;
    private PublicKey recipient; //also known as the new owner of these coins.
    private float value; //the amount of coins they own
    private String parentTransactionId; //the id of the transaction this output was created in

    //Constructor
    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        String data = String.format("%s%s%s", BlockChainUtils.getStringFromKey(recipient), Float.toString(value), parentTransactionId);
        this.id = BlockChainUtils.applySha256(data);
    }

    //Check if coin belongs to you
    public boolean isMine(PublicKey publicKey) {
        return (publicKey.equals(recipient));
    }
}
