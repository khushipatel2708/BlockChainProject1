package BlockChainProject1;
import blockchainproject1.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BlockChainProject1 {
    public List<Block> chain;
    public int difficulty = 4;
    private static final double MINING_REWARD = 50.0;
    private static final int MAX_TX_PER_BLOCK = 10;
    private static final String FILE = "blockchain.json";

    public BlockChainProject1() {
        chain = new ArrayList<>();
        chain.add(createGenesisBlock());
         loadFromJson();
        if (chain.isEmpty()) {
            chain.add(createGenesisBlock());
            saveToJson();
        }
    }

    private Block createGenesisBlock() {
        return new Block(0, new ArrayList<>(), "0");
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addBlock(Block newBlock, String minerAddress) {

    if (newBlock.transactions == null || newBlock.transactions.isEmpty()) {
        System.out.println("No transactions to mine.");
        return;
    }

    if (newBlock.transactions.size() > MAX_TX_PER_BLOCK) {
        System.out.println("Too many transactions in one block. Max allowed: " + MAX_TX_PER_BLOCK);
        return;
    }

    newBlock.transactions.add(0, getRewardTransaction(minerAddress));

    newBlock.previousHash = getLatestBlock().hash;

    long startTime = System.currentTimeMillis();
    newBlock.mineBlock(difficulty);
    long endTime = System.currentTimeMillis();

    if (isValidNewBlock(newBlock, getLatestBlock())) {
        chain.add(newBlock);
        adjustDifficulty(endTime - startTime);
        saveToJson();  
        System.out.println("Block added with " + newBlock.transactions.size() + " transactions");
    } else {
        System.out.println("Invalid block rejected");
    }
}


    private void adjustDifficulty(long miningTime) {
        if (miningTime < 2000) difficulty++;
        else if (miningTime > 5000 && difficulty > 1) difficulty--;
    }

    private Transaction getRewardTransaction(String miner) {
        return new Transaction("SYSTEM", miner, MINING_REWARD);
    }

    private boolean isValidNewBlock(Block newBlock, Block previousBlock) {

        if (!newBlock.previousHash.equals(previousBlock.hash))
            return false;

        if (!newBlock.hash.equals(newBlock.calculateHash()))
            return false;

        return true;
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);

            if (!current.hash.equals(current.calculateHash()))
                return false;

            if (!current.previousHash.equals(previous.hash))
                return false;
        }
        return true;
    }
//    private void saveToJson() {
//    try (Writer writer = new FileWriter(FILE)) {
//        Gson gson = new Gson();
//        gson.toJson(chain, writer);
//    } catch (Exception e) {
//        e.printStackTrace();
//    }
//}
    private void saveToJson() {
    try (Writer writer = new FileWriter(FILE)) {
        writer.write("[\n");
        for (int i = 0; i < chain.size(); i++) {
            Block block = chain.get(i);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(block));
            if (i < chain.size() - 1) writer.write(",\n"); // comma between blocks
        }
        writer.write("\n]");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    private void loadFromJson() {
    try (Reader reader = new FileReader(FILE)) {
        Gson gson = new Gson();
        Type chainType = new TypeToken<List<Block>>(){}.getType();
        chain = gson.fromJson(reader, chainType);
    } catch (Exception e) {
        chain = new ArrayList<>();
    }
}

}
