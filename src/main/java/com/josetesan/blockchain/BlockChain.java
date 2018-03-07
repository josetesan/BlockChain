package com.josetesan.blockchain;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;


@Slf4j
public class BlockChain implements Iterable<Block>{


    private static final BlockChain _INSTANCE = new BlockChain();

    private List<Block> data;
    private Integer difficulty;
    private static final Integer VERSION = 0x0001;

    private BlockChain() {
        this.data = new LinkedList<>();
        this.difficulty = 3;
    }


    public static BlockChain getInstance(boolean clear) {
        if (clear) {
            _INSTANCE.clear();
        }
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




    public boolean isValid() {
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        Block currentBlock, previousBlock = null;
        for (int i = 1; i < data.size(); i++) {
            currentBlock = data.get(i);
            previousBlock = data.get(i - 1);
            //compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                log.info("Current Hash is not equal");
                return false;
            } else
            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                log.info("Previous Hash is not equal");
                return false;
            } else
            if(!currentBlock.getHash().substring( 0, difficulty).equals(hashTarget)) {
                log.info("Block {} hasn't been mined",i);
                return false;
            }
        }
        return true;
    }

    private void clear() {
        this.data.clear();
    }
}
