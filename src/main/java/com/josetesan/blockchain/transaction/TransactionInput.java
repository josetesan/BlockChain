package com.josetesan.blockchain.transaction;

import lombok.Data;

@Data
public class TransactionInput {

    private String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    private TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
