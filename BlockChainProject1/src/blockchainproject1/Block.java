package BlockChainProject1;
import blockchainproject1.HashUtil;
import blockchainproject1.MerkleRoot;
import blockchainproject1.Transaction;
import java.util.ArrayList;
import java.util.List;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

public class Block {

    public int index;
    public long timestamp;
    public List<Transaction> transactions;
    public String previousHash;
    public String hash;
    public int nonce;
    public String merkleRoot;

//     public Block() {}
     
    public Block(int index, List<Transaction> transactions, String previousHash) {
        this.index = index;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.merkleRoot = MerkleRoot.getMerkleRoot(transactions);
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return HashUtil.applySHA256(
                index +
                previousHash +
                timestamp +
                merkleRoot +
                nonce
        );
    }

    // Proof of Work
    public void mineBlock(int difficulty) {
        String target = "0".repeat(difficulty);

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("✅ Block mined: " + hash);
    }
    
//    public Object getMerkleTree() {
//    return MerkleRoot.getMerkleTree(transactions);
//}
    public DefaultMutableTreeNode getMerkleTreeNode() {
    // Leaf nodes
    List<DefaultMutableTreeNode> nodes = transactions.stream()
        .map(tx -> new DefaultMutableTreeNode(tx.getTxHash() + " | " + tx.toString()))
        .toList();

    while (nodes.size() > 1) {
        List<DefaultMutableTreeNode> parents = new java.util.ArrayList<>();
        for (int i = 0; i < nodes.size(); i += 2) {
            DefaultMutableTreeNode left = nodes.get(i);
            DefaultMutableTreeNode right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : left;
            String combinedHash = HashUtil.applySHA256(left.getUserObject().toString() + right.getUserObject().toString());
            DefaultMutableTreeNode parent = new DefaultMutableTreeNode("Parent: " + combinedHash);
            parent.add(left);
            parent.add(right);
            parents.add(parent);
        }
        nodes = parents;
    }

    return nodes.isEmpty() ? new DefaultMutableTreeNode("Empty") : nodes.get(0);
}

    private DefaultMutableTreeNode buildMerkleTree(List<Transaction> txs) {
        List<DefaultMutableTreeNode> nodes = new ArrayList<>();
        for (Transaction tx : txs) {
            nodes.add(new DefaultMutableTreeNode(tx.getTxHash() + " | " + tx.toString()));
        }

        while (nodes.size() > 1) {
            List<DefaultMutableTreeNode> parentNodes = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i += 2) {
                DefaultMutableTreeNode left = nodes.get(i);
                DefaultMutableTreeNode right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : left;
                String combinedHash = HashUtil.applySHA256(
    left.getUserObject().toString() + right.getUserObject().toString()
);
                DefaultMutableTreeNode parent = new DefaultMutableTreeNode("Parent: " + combinedHash);
                parent.add(left);
                parent.add(right);
                parentNodes.add(parent);
            }
            nodes = parentNodes;
        }

        return nodes.isEmpty() ? new DefaultMutableTreeNode("Empty") : nodes.get(0);
    }
}

