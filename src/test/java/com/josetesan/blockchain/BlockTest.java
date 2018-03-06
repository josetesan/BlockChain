package com.josetesan.blockchain;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BlockTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(BlockTest.class);

    @Test
    public void testCanCreateBlock() {

        Map<String,String> data = new HashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");

        Block block = new Block(data, "0");
        Assert.assertNotNull(block.getHash());
        Assert.assertEquals("0",block.getPreviousHash());

    }


    @Test
    public void testCanChainBlocks() {

        Map<String,String> data = new HashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");

        Block block1 = new Block(data, "0");
        Block block2 = new Block(data, block1.getHash());
        Block block3 = new Block(data, block2.getHash());

        Assert.assertEquals(block1.getHash(), block2.getPreviousHash());
        Assert.assertEquals(block2.getHash(), block3.getPreviousHash());

    }

    @Test
    public void testCanAddToBlockChain() {
        Map<String,String> data = new HashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");

        BlockChain blockChain = BlockChain.getInstance();

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
        Map<String,String> data = new HashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");

        BlockChain blockChain = BlockChain.getInstance();
        blockChain.clear();

        Block block1 = new Block(data, "0");
        blockChain.addBlock(block1);

        Block block2 = new Block(data, block1.getHash());
        blockChain.addBlock(block2);
        Block block3 = new Block(data, block2.getHash());
        blockChain.addBlock(block3);

        mineBlockChain(blockChain,1);
        mineBlockChain(blockChain,2);
        mineBlockChain(blockChain,3);
        mineBlockChain(blockChain,4);
        mineBlockChain(blockChain,5);


    }

    private void mineBlockChain(BlockChain blockChain, int difficulty) {
        long init = System.currentTimeMillis();
        for (Block block : blockChain) {

            block.mineBlock(difficulty);

        }
        long end = System.currentTimeMillis() - init;
        LOGGER.info("It took {} ms to mine with difficulty {}", end, difficulty);
    }
}
