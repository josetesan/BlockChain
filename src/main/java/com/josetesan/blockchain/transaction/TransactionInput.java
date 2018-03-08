package com.josetesan.blockchain.transaction;

import lombok.Value;

@Value
public class TransactionInput {

    private String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    private TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
        this.UTXO = null;
    }
}
