package blockchainproject1;
import blockchainproject1.HashUtil;

public class Transaction {
    private String sender;
    private String receiver;
    private double amount;
    private long timestamp;
    private final String txHash;
//    private String txHash;
    
    public Transaction(String sender, String receiver, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.txHash = HashUtil.applySHA256(sender + receiver + amount + timestamp);
    }
    
//     public Transaction() {}
     
     public String getTxHash() {
        return txHash;
    }

    @Override
    public String toString() {
        return sender + " -> " + receiver + " : " + amount + " | " + timestamp;
    }
}
