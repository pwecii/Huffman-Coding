package MachineP3;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class HuffmanVisualizer extends JFrame {
    private JTextArea inputArea, outputArea;
    private JButton encodeButton, clearButton, decodeButton;
    private HuffmanNode root;
    private Map<Character, String> huffmanCodes;
    private HuffmanTreePanel treePanel;
    private String encodedText = "";
    //private Image frameBackgroundImage;

    public HuffmanVisualizer() {
    	
    	//try {
            //frameBackgroundImage = ImageIO.read(new File("C:/Users/DELL/OneDrive/Documents/DAA/DAA midterm Proj/Pictures/bgMP3.jpg"));
      //  } catch (IOException e) {
         //   System.err.println("Failed to load frame background.");
        //    e.printStackTrace();
      //  }
        
        setTitle("Huffman Coding");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());
        
        // Custom background panel for the entire frame
       // JPanel backgroundPanel = new JPanel(new BorderLayout(10, 10)) {
           // @Override
        //    protected void paintComponent(Graphics g) {
       //         super.paintComponent(g);
         //       if (frameBackgroundImage != null) {
       //             g.drawImage(frameBackgroundImage, 0, 0, getWidth(), getHeight(), this);
         //       }
       //     }
        //};
       // setContentPane(backgroundPanel);

        // Input & Output Areas
        inputArea = new JTextArea(5, 20);
        outputArea = new JTextArea(5, 20);
        outputArea.setEditable(false);
        
       

        // Buttons
        
        String encodebuttonImagePath = "C:/Users/DELL/OneDrive/Documents/DAA/DAA midterm Proj/Pictures/BuildButton.png";
        encodeButton = createPixelButton ("Encode",encodebuttonImagePath);
        String decodebuttonImagePath = "C:/Users/DELL/OneDrive/Documents/DAA/DAA midterm Proj/Pictures/DecodeButton.png";
        decodeButton =  createPixelButton ("Decode", decodebuttonImagePath);
        String clearbuttonImagePath = "C:/Users/DELL/OneDrive/Documents/DAA/DAA midterm Proj/Pictures/ClearButton.png";
        clearButton = createPixelButton("Clear", clearbuttonImagePath);

        // Top Panel: Input + Buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(encodeButton);
        buttonPanel.add(decodeButton);
        buttonPanel.add(clearButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Bottom Panel: Output
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Center Panel: Tree
        treePanel = new HuffmanTreePanel();
        add(topPanel, BorderLayout.NORTH);
        add(treePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Encode Button
        encodeButton.addActionListener(e -> {
            String text = inputArea.getText().trim();
            if (!text.isEmpty()) {
                root = buildHuffmanTree(text);
                huffmanCodes = generateCodes(root);
                encodedText = encodeText(text, huffmanCodes);
                outputArea.setText("Binary: " + encodedText); // show only encoded text
                treePanel.setTree(root);
                repaint();
            }
        });

        // Decode Button
        decodeButton.addActionListener(e -> {
            if (root != null && !encodedText.isEmpty()) {
                String decoded = decodeText(encodedText, root);
                outputArea.setText("Decoded: " + decoded);
            } else {
                outputArea.setText("Nothing to decode. Encode first!");
            }
        });

        // Clear Button
        clearButton.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
            root = null;
            encodedText = "";
            treePanel.setTree(null);
            repaint();
        });

        setVisible(true);
    }
    private JButton createPixelButton(String text, String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(150, 50, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);

        JButton button = new JButton(text, scaledIcon);
        button.setFont(new Font("Monospaced", Font.BOLD, 12));
        button.setForeground(Color.BLACK);

        // These control positioning of image and text
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER); // text on top of image
        button.setVerticalAlignment(SwingConstants.TOP); // nudge entire thing upward

        // Remove borders + background to show image only
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        // Adjust size and padding (this moves the whole thing visually)
        button.setPreferredSize(new Dimension(150, 50));
        button.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0)); // <--- moves everything down a bit

        return button;
    }


    // Node class
    static class HuffmanNode {
        char ch;
        int freq;
        HuffmanNode left, right;

        public HuffmanNode(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        public HuffmanNode(int freq, HuffmanNode left, HuffmanNode right) {
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }
    }

    // Build Tree
    private HuffmanNode buildHuffmanTree(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char ch : text.toCharArray()) {
            freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
        }

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            pq.add(new HuffmanNode(left.freq + right.freq, left, right));
        }

        return pq.poll();
    }

    // Generate Codes
    private Map<Character, String> generateCodes(HuffmanNode root) {
        Map<Character, String> map = new HashMap<>();
        generateCodesHelper(root, "", map);
        return map;
    }

    private void generateCodesHelper(HuffmanNode node, String code, Map<Character, String> map) {
        if (node.isLeaf()) {
            map.put(node.ch, code);
            return;
        }
        generateCodesHelper(node.left, code + "0", map);
        generateCodesHelper(node.right, code + "1", map);
    }

    // Encode
    private String encodeText(String text, Map<Character, String> codes) {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            sb.append(codes.get(ch));
        }
        return sb.toString();
    }

    // Decode
    private String decodeText(String binary, HuffmanNode root) {
        StringBuilder sb = new StringBuilder();
        HuffmanNode current = root;
        for (char bit : binary.toCharArray()) {
            current = bit == '0' ? current.left : current.right;
            if (current.isLeaf()) {
                sb.append(current.ch);
                current = root;
            }
        }
        return sb.toString();
    }

    // Tree Drawing Panel
    static class HuffmanTreePanel extends JPanel {
        private HuffmanNode tree;

        public void setTree(HuffmanNode root) {
            this.tree = root;
            repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (tree != null) {
                drawTree(g, tree, getWidth() / 2, 40, getWidth() / 4);
            }
        }

        private void drawTree(Graphics g, HuffmanNode node, int x, int y, int xOffset) {
            g.setColor(Color.BLACK);

            if (node.left != null) {
                g.drawLine(x, y, x - xOffset, y + 60);
                g.drawString("0", x - xOffset / 2, y + 40);
                drawTree(g, node.left, x - xOffset, y + 60, xOffset / 2);
            }

            if (node.right != null) {
                g.drawLine(x, y, x + xOffset, y + 60);
                g.drawString("1", x + xOffset / 2, y + 40);
                drawTree(g, node.right, x + xOffset, y + 60, xOffset / 2);
            }

            String label = node.isLeaf() ? node.freq + " [" + node.ch + "]" : String.valueOf(node.freq);
            g.setColor(new Color(200, 240, 255));
            g.fillRoundRect(x - 20, y - 15, 50, 30, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRoundRect(x - 20, y - 15, 50, 30, 10, 10);
            g.drawString(label, x - 15, y + 5);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HuffmanVisualizer::new);
    }
}




