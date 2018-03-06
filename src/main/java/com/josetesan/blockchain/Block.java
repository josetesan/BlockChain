package com.josetesan.blockchain;

import com.google.gson.GsonBuilder;
import lombok.Value;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Value
public class Block {

    private String hash;
    private String previousHash;
    private Map<String,String> data;
    private long timeStamp; //as number of milliseconds since 1/1/1970.
    private AtomicInteger nonce;

    public Block(Map<String, String> data,String previousHash) {
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.previousHash = previousHash;
        this.hash = calculateHash();
        this.nonce = new AtomicInteger(0);
    }


    public String calculateHash() {

        final String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(data);
        return BlockChainUtils.applySha256(String.format("%s%s%s", previousHash, Long.toString(timeStamp), jsonString));
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        String aHash = this.hash;
        while(!aHash.substring( 0, difficulty).equals(target)) {
            nonce.incrementAndGet();
            aHash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + aHash+" with nonce "+nonce.get());
    }
}
