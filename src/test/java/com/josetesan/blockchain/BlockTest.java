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
public class BlockTest {


    @Parameters
    public static Iterable<? extends Object> data() {        return Arrays.asList(1,2,3,4,5);    }

    @Parameter // first data value (0) is default
    public /* NOT private */ int difficulty;


    private Map<String,String> data;

    @Before
    public void setup() {
        data = new LinkedHashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");
    }

    @Test
    public void testCanCreateBlock() {

        Block block = new Block(data, "0");
        Assert.assertNotNull(block.getHash());
        Assert.assertEquals("0",block.getPreviousHash());

    }

    @Test
    public void testCanChainBlocks() {

        Block block1 = new Block(data, "0");
        Block block2 = new Block(data, block1.getHash());
        Block block3 = new Block(data, block2.getHash());

        Assert.assertEquals(block1.getHash(), block2.getPreviousHash());
        Assert.assertEquals(block2.getHash(), block3.getPreviousHash());

    }

    @Test
    public void testABlockChainIsValidWhenMined() {

        BlockChain blockChain = BlockChain.getInstance(true);
        Block block1 = new Block(data, "0");
        blockChain.addBlock(block1);
        Block block2 = new Block(data, block1.getHash());
        blockChain.addBlock(block2);
        Block block3 = new Block(data, block2.getHash());
        blockChain.addBlock(block3);

        Assert.assertEquals(3, blockChain.spliterator().estimateSize());
        Assert.assertTrue(blockChain.isValid());

    }

    @Test

    public void testBenchmarkBlockChainMining() {

        BlockChain blockChain = BlockChain.getInstance(true);

        // 4 blocks as my machine ahs 4 cores

        Block block1 = new Block(data, "0");
        blockChain.addBlock(block1);
        Block block2 = new Block(data, block1.getHash());
        blockChain.addBlock(block2);
        Block block3 = new Block(data, block2.getHash());
        blockChain.addBlock(block3);
        Block block4 = new Block(data, block3.getHash());
        blockChain.addBlock(block4);
        log.info("Mining with {} difficulty",difficulty);
        mineBlockChain(blockChain,difficulty);

    }

    private void mineBlockChain(BlockChain blockChain, int difficulty) {
        long init = System.currentTimeMillis();
        StreamSupport
                .stream(blockChain.spliterator(), true)
                .forEach(b -> b.mineBlock(difficulty));
        long end = System.currentTimeMillis() - init;
        log.info("It took {} ms to mine with difficulty {}", end, difficulty);
    }
}
