package com.josetesan.blockchain;


import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

@Slf4j
@RunWith(Parameterized.class)
public class BenchmarkBlockTest {


    @Parameters
    public static Iterable<? extends Object> data() {        return Arrays.asList(1,2,3,4,5,6);    }

    @Parameter // first data value (0) is default
    public int difficulty;


    private Map<String,String> data;

    @Before
    public void setup() {
        data = new LinkedHashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");
    }


    @Test
    public void testBenchmarkBlockChainMining() {
        BlockChain blockChain = BlockChain.getInstance(true);

        // 4 blocks as my machine ahs 4 cores

        Block block1 = new Block(data, "0");
        blockChain.addBlockNotMined(block1);
        Block block2 = new Block(data, block1.getHash());
        blockChain.addBlockNotMined(block2);
        Block block3 = new Block(data, block2.getHash());
        blockChain.addBlockNotMined(block3);
        Block block4 = new Block(data, block3.getHash());
        blockChain.addBlockNotMined(block4);
        log.info("Mining with {} difficulty",difficulty);
        mineBlockChain(blockChain,difficulty);

    }

    private void mineBlockChain(BlockChain blockChain, int theDifficulty) {
        long init = System.currentTimeMillis();
        StreamSupport
                .stream(blockChain.spliterator(), true)
                .forEach(b -> b.mineBlock(theDifficulty));
        long end = System.currentTimeMillis() - init;
        log.info("It took {} ms to mine with difficulty {}", end, theDifficulty);
    }
}
