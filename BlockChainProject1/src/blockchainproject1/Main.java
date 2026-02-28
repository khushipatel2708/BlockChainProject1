package BlockChainProject1;
import blockchainproject1.Transaction;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        BlockChainProject1 myBlockchain = new BlockChainProject1();

        // Block 1 Transactions
        List<Transaction> tx1 = new ArrayList<>();
        tx1.add(new Transaction("Alice", "Bob", 100));
        tx1.add(new Transaction("Bob", "Charlie", 50));

        myBlockchain.addBlock(new Block(1, tx1, ""), "Miner-1");

        // Block 2 Transactions
        List<Transaction> tx2 = new ArrayList<>();
        tx2.add(new Transaction("Charlie", "David", 200));
        tx2.add(new Transaction("Alice", "Eve", 300));

        myBlockchain.addBlock(new Block(2, tx2, ""), "Miner-1");

        // Display Blockchain
        System.out.println("\n🔗 Blockchain:");
        myBlockchain.chain.forEach(block -> {
            System.out.println("Block #" + block.index);
            System.out.println("Hash: " + block.hash);
            System.out.println("Previous: " + block.previousHash);
            System.out.println("Transactions: " + block.transactions);
            System.out.println("--------------------------------");
        });

        System.out.println("Blockchain valid? " + myBlockchain.isChainValid());
    }
}
