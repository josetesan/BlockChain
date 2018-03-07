package com.josetesan.blockchain;


import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

@Slf4j
public class BlockTest {


    @Test
    public void testCanCreateBlock() {

        Map<String,String> data = new LinkedHashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");

        Block block = new Block(data, "0");
        Assert.assertNotNull(block.getHash());
        Assert.assertEquals("0",block.getPreviousHash());

    }


    @Test
    public void testCanChainBlocks() {

        Map<String,String> data = new LinkedHashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");

        Block block1 = new Block(data, "0");
        Block block2 = new Block(data, block1.getHash());
        Block block3 = new Block(data, block2.getHash());

        Assert.assertEquals(block1.getHash(), block2.getPreviousHash());
        Assert.assertEquals(block2.getHash(), block3.getPreviousHash());

    }

    @Test
    public void testABlockChainIsNotValidWithoutMining() {
        Map<String,String> data = new LinkedHashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");

        BlockChain blockChain = BlockChain.getInstance(true);
        Block block1 = new Block(data, "0");
        blockChain.addBlock(block1);
        Block block2 = new Block(data, block1.getHash());
        blockChain.addBlock(block2);
        Block block3 = new Block(data, block2.getHash());
        blockChain.addBlock(block3);
        Assert.assertEquals(3, blockChain.spliterator().estimateSize());
        Assert.assertFalse(blockChain.isValid());

    }


    @Test
    public void testABlockChainIsValidWhenMined() {
        Map<String,String> data = new LinkedHashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");

        BlockChain blockChain = BlockChain.getInstance(true);
        Block block1 = new Block(data, "0");
        blockChain.addBlock(block1);
        Block block2 = new Block(data, block1.getHash());
        blockChain.addBlock(block2);
        Block block3 = new Block(data, block2.getHash());
        blockChain.addBlock(block3);
        Assert.assertEquals(3, blockChain.spliterator().estimateSize());
        blockChain.forEach(b -> b.mineBlock(3));
        Assert.assertTrue(blockChain.isValid());

    }

    @Test
    public void testBenchmarkBlockChainMining() {
        Map<String,String> data = new LinkedHashMap<>(2);
        data.put("key1","value1");
        data.put("key2","value2");

        BlockChain blockChain = BlockChain.getInstance(true);

        Block block1 = new Block(data, "0");
        blockChain.addBlock(block1);
        Block block2 = new Block(data, block1.getHash());
        blockChain.addBlock(block2);
        Block block3 = new Block(data, block2.getHash());
        blockChain.addBlock(block3);
        Block block4 = new Block(data, block3.getHash());
        blockChain.addBlock(block4);

        mineBlockChain(blockChain,1);
        mineBlockChain(blockChain,2);
        mineBlockChain(blockChain,3);
        mineBlockChain(blockChain,4);
        mineBlockChain(blockChain,5);
        mineBlockChain(blockChain,6);


    }

    private void mineBlockChain(BlockChain blockChain, int difficulty) {
        long init = System.currentTimeMillis();
        StreamSupport.stream(blockChain.spliterator(), true)
                .forEach(b -> b.mineBlock(difficulty));
        long end = System.currentTimeMillis() - init;
        log.info("It took {} ms to mine with difficulty {}", end, difficulty);
    }
}
