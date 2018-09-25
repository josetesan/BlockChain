package com.josetesan.blockchain;

import com.josetesan.blockchain.transaction.Transaction;
import com.josetesan.blockchain.utils.BlockChainUtils;
import com.josetesan.blockchain.wallet.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.Security;

@Slf4j
public class TransactionTest {

    @BeforeClass
    public static void setup() {
        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void testTransactions() {
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        //Test public and private keys
        log.info("Private and public keys:");
        log.info(BlockChainUtils.getStringFromKey(walletA.getPrivateKey()));
        log.info(BlockChainUtils.getStringFromKey(walletA.getPublicKey()));
        //Create a test transaction from WalletA to walletB
        Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
        transaction.generateSignature(walletA.getPrivateKey());
        //Verify the signature works and verify it from the public key
        log.info("Is signature verified");
        Assert.assertTrue(transaction.verifySignature());


    }

}
