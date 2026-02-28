/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchainproject1;

import java.util.ArrayList;
import java.util.List;
public class MerkleRoot {

    public static String getMerkleRoot(List<Transaction> transactions) {
        List<String> hashes = transactions.stream()
                .map(tx -> HashUtil.applySHA256(tx.toString()))
                .toList();

        while (hashes.size() > 1) {
            List<String> newHashes = new java.util.ArrayList<>();
            for (int i = 0; i < hashes.size(); i += 2) {
                String left = hashes.get(i);
                String right = (i + 1 < hashes.size()) ? hashes.get(i + 1) : left;
                newHashes.add(HashUtil.applySHA256(left + right));
            }
            hashes = newHashes;
        }
        return hashes.isEmpty() ? "" : hashes.get(0);
    }
    public static List<Object> getMerkleTree(List<Transaction> transactions) {
        List<String> hashes = transactions.stream()
                .map(tx -> HashUtil.applySHA256(tx.toString()))
                .toList();

        List<Object> tree = new ArrayList<>();
        for (String h : hashes) tree.add(h); // leaf nodes

        while (tree.size() > 1) {
            List<Object> newTree = new ArrayList<>();
            for (int i = 0; i < tree.size(); i += 2) {
                Object left = tree.get(i);
                Object right = (i + 1 < tree.size()) ? tree.get(i + 1) : left;
                String combinedHash = HashUtil.applySHA256(getHashString(left) + getHashString(right));
                List<Object> parent = new ArrayList<>();
                parent.add(left);
                parent.add(right);
                parent.add(combinedHash); // optional store parent hash
                newTree.add(parent);
            }
            tree = newTree;
        }
        return tree;
    }

    private static String getHashString(Object node) {
        if (node instanceof String) return (String) node;
        else if (node instanceof List<?> list) {
            return (String) list.get(list.size() - 1);
        }
        return "";
    }
}
