package BlockChainProject1;
import blockchainproject1.Transaction;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

public class BlockchainUI extends JFrame {

    private JTextField senderField, receiverField, amountField;
    private JTable txTable;
    private DefaultTableModel txModel;
    private JPanel blockPanel;
    private JLabel statusLabel;

    private BlockChainProject1 blockchain;
    private List<Transaction> pendingTransactions;
    private static final int MAX_TX_PER_BLOCK = 5;

    public BlockchainUI() {
        blockchain = new BlockChainProject1();
        pendingTransactions = new ArrayList<>();

        setTitle("🧱 Blockchain Explorer");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTransactionPanel(), BorderLayout.WEST);
        add(createBlockchainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ---------- UI PANELS ----------

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));

        senderField = new JTextField();
        receiverField = new JTextField();
        amountField = new JTextField();

        JButton addBtn = new JButton("Add Transaction");

        panel.add(new JLabel("Sender"));
        panel.add(new JLabel("Receiver"));
        panel.add(new JLabel("Amount"));
        panel.add(new JLabel(""));

        panel.add(senderField);
        panel.add(receiverField);
        panel.add(amountField);
        panel.add(addBtn);

        addBtn.addActionListener(e -> addTransaction());

        return panel;
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Pending Transactions"));

        txModel = new DefaultTableModel(
                new Object[]{"Sender", "Receiver", "Amount", "Time"}, 0);

        txTable = new JTable(txModel);
        panel.add(new JScrollPane(txTable), BorderLayout.CENTER);

        JButton mineBtn = new JButton("Mine Block");
        mineBtn.addActionListener(e -> mineBlock());

        panel.add(mineBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane createBlockchainPanel() {
    blockPanel = new JPanel();
    blockPanel.setLayout(new BoxLayout(blockPanel, BoxLayout.Y_AXIS));

    JScrollPane scrollPane = new JScrollPane(blockPanel);
    scrollPane.setBorder(BorderFactory.createTitledBorder("Blockchain"));

    return scrollPane;
}

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Chain Status: VALID ✔");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(statusLabel);
        return panel;
    }

    // ---------- LOGIC ----------

    private void addTransaction() {
        try {
             if (pendingTransactions.size() >= MAX_TX_PER_BLOCK) {
            JOptionPane.showMessageDialog(this,
                "Block is full! Mine the block before adding more transactions.");
            return;
        }
            String sender = senderField.getText();
            String receiver = receiverField.getText();
            double amount = Double.parseDouble(amountField.getText());

            Transaction tx = new Transaction(sender, receiver, amount);
            pendingTransactions.add(tx);

            txModel.addRow(new Object[]{
                    sender, receiver, amount, tx.toString().split("\\|")[1]
            });

            senderField.setText("");
            receiverField.setText("");
            amountField.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input");
        }
    }

    private void mineBlock() {
        if (pendingTransactions.isEmpty()) return;

        Block block = new Block(
                blockchain.chain.size(),
                new ArrayList<>(pendingTransactions),
                blockchain.getLatestBlock().hash
        );

       blockchain.addBlock(block, "UI-Miner");

        pendingTransactions.clear();
        txModel.setRowCount(0);

        refreshBlockchainView();
    }

    private void refreshBlockchainView() {
        blockPanel.removeAll();

        for (Block block : blockchain.chain) {
            blockPanel.add(createBlockCard(block));
        }

        statusLabel.setText(
                blockchain.isChainValid() ? "Chain Status: VALID ✔" : "Chain Status: INVALID ❌"
        );

        blockPanel.revalidate();
        blockPanel.repaint();
    }

    private JPanel createBlockCard(Block block) {
    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    // --- Header ---
    JLabel header = new JLabel("Block #" + block.index);
    header.setFont(new Font("Arial", Font.BOLD, 16));
    card.add(header);

    // --- Block Info ---
    JTextArea info = new JTextArea(
            "Hash:\n" + block.hash +
            "\n\nPrevious Hash:\n" + block.previousHash
    );
    info.setEditable(false);
    info.setLineWrap(true);
    info.setWrapStyleWord(true);
    card.add(info);

    // --- Transaction Table ---
    JTable txTable = new JTable(new DefaultTableModel(
            new Object[]{"Sender", "Receiver", "Amount"}, 0
    ));
    DefaultTableModel model = (DefaultTableModel) txTable.getModel();
    for (Transaction tx : block.transactions) {
        String[] parts = tx.toString().split(" ");
        model.addRow(new Object[]{parts[0], parts[2], parts[4]});
    }
    JScrollPane txScroll = new JScrollPane(txTable);
    txScroll.setPreferredSize(new Dimension(400, 150));
    txScroll.setBorder(BorderFactory.createTitledBorder("Transactions"));
    card.add(txScroll);

    // --- Merkle Tree ---
    DefaultMutableTreeNode rootNode = block.getMerkleTreeNode();
    JTree merkleTree = new JTree(rootNode);
    JScrollPane treeScroll = new JScrollPane(merkleTree);
    treeScroll.setPreferredSize(new Dimension(400, 150));
    treeScroll.setBorder(BorderFactory.createTitledBorder("Merkle Tree"));
    card.add(treeScroll);

    return card;
}


    // ---------- MAIN ----------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BlockchainUI::new);
    }
    
    
}
