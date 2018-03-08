package com.josetesan.blockchain;


import com.josetesan.blockchain.block.Block;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class BlockTest {


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


}
