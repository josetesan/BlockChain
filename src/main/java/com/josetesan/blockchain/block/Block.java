package com.josetesan.blockchain.block;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.josetesan.blockchain.utils.BlockChainUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Slf4j
public class Block {

    private String hash;
    private String previousHash;
    private Map<String,String> data;
    private long timeStamp; //as number of milliseconds since 1/1/1970.
    private AtomicLong nonce;
    private static final Integer VERSION = 0x0001;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Block(Map<String, String> data,String previousHash) {
        this.data = data;
        this.timeStamp = new Date().getTime();
        this.previousHash = previousHash;
        this.nonce = new AtomicLong(0L);
        this.hash = calculateHash();
    }


    public String calculateHash() {

        final String jsonString = GSON.toJson(data);
        return BlockChainUtils.applySha256(String.format("%s%s%s%s", previousHash, Long.toString(timeStamp), this.nonce.get(), jsonString));
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while(!this.hash.substring( 0, difficulty).equals(target)) {
            nonce.incrementAndGet();
            this.hash = calculateHash();
        }
        log.debug("Block Mined !! ChopoCoin created {}  with nonce {}", this.hash, nonce.get());
    }
}
