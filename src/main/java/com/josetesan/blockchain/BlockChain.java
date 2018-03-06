package com.josetesan.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;


public class BlockChain implements Iterable<Block>{

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockChain.class);

    private static final BlockChain _INSTANCE = new BlockChain();

    private List<Block> data;
    private Integer difficulty;

    private BlockChain() {
        this.data = new LinkedList<>();
        this.difficulty = 3;
    }


    public static BlockChain getInstance() {
        return _INSTANCE;
    }

    public void addBlock(final Block block) {
        data.add(block);
    }


    @Override
    public Iterator<Block> iterator() {
        return data.iterator();
    }

    @Override
    public void forEach(Consumer<? super Block> action) {
        data.forEach(action);

    }

    @Override
    public Spliterator<Block> spliterator() {
        return data.spliterator();
    }


    public void clear() {
        this.data.clear();
    }

    public boolean isValid() {
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        Block currentBlock, previousBlock = null;
        for (int i = 1; i < data.size(); i++) {
            currentBlock = data.get(i);
            previousBlock = data.get(i - 1);
            //compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                LOGGER.info("Current Hashes not equal");
                return false;
            } else
            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                LOGGER.info("Previous Hashes not equal");
                return false;
            } else
            if(!currentBlock.getHash().substring( 0, difficulty).equals(hashTarget)) {
                LOGGER.info("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
