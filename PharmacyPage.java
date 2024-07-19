import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PharmacyPage extends JFrame {
    
    private JPanel contentPane;
    private JLabel totalCostLabel;
    private double totalCost = 0.0;
    public PharmacyPage(String userID) throws SQLException {
        System.out.println("User id fro pharma is " + userID);
        setTitle("Pharmacy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 450);
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Adjust the path to your background image as necessary
                g.drawImage(new ImageIcon("HospitalManagement/src/pharmacy_background.jpg").getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        contentPane.setOpaque(false);



        //setting up connection

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String urlDB = "jdbc:mysql://localhost:3306/HospitalManagementSystem";
        String username = "root";
        String password = "root@123";
        Connection connection = DriverManager.getConnection(urlDB, username, password);

        if (connection != null) {
            System.out.println("Connection established");
        }

        int resultCount = 0;
        String[] medNameList = new String[20]; 
        double[] priceList = new double[20];
        int[] limitList = new int[20];
        try (Statement stmt = connection.createStatement()) {
            String medQuery = "select * from Pharmacy";
            ResultSet medResult = stmt.executeQuery(medQuery);

            boolean isFound = false;
            while (medResult.next()) {
                isFound = true;
                medNameList[resultCount] = medResult.getString("medName");
                priceList[resultCount] = Double.parseDouble(medResult.getString("price"));
                limitList[resultCount] = Integer.parseInt(medResult.getString("maxNo"));
                resultCount++;
            }

            if (!isFound) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "No Record Found. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
            System.out.println("Number of results: " + resultCount);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        //beggining of med display
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); // Changed to BoxLayout for vertical arrangement
        contentPane.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setOpaque(false);

        // Create and add 5 medicine lines with increment counters
        final int finalResultCount = resultCount;
        for (int i = 0; i < finalResultCount; i++) {
            int finalI = i; // Declare finalI inside the loop
            JPanel medicinePanel = new JPanel();
            medicinePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            medicinePanel.setOpaque(false);
            JLabel medicineLabel = new JLabel(medNameList[i] + " (Rs. " + priceList[i] + ")");
            JTextField countField = new JTextField("0", 5);
            countField.setEditable(false);

            
            JButton incrementButton = new JButton("+");
            incrementButton.addActionListener(e -> {
                int countIncremented = Integer.parseInt(countField.getText()) + 1;
                countField.setText(String.valueOf(countIncremented));
                updateTotalCost(priceList[finalI], true);
            });

            JButton decrementButton = new JButton("-");
            decrementButton.addActionListener(e -> {
                int count = Integer.parseInt(countField.getText());
                if (count > 0) {
                    countField.setText(String.valueOf(--count));
                    updateTotalCost(priceList[finalI], false);
                }
            });

            medicinePanel.add(medicineLabel);
            medicinePanel.add(incrementButton);
            medicinePanel.add(countField);
            medicinePanel.add(decrementButton);

            centerPanel.add(medicinePanel);
        }
        // Total cost label
        totalCostLabel = new JLabel("Total Cost: Rs. 0.0");
        centerPanel.add(totalCostLabel);

        JButton buyButton = new JButton("Buy");
        buyButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Purchase made!");
            for (int i = 0; i < finalResultCount; i++) {
                JPanel medicinePanel = (JPanel) centerPanel.getComponent(i);
                JTextField countField = (JTextField) medicinePanel.getComponent(2); // Assuming countField is the third component in each medicinePanel
                countField.setText("0");
            }
        });
        contentPane.add(buyButton, BorderLayout.SOUTH);
        //end of med display

        buyButton.addActionListener(e -> {
            try (Statement stmt = connection.createStatement()) {
                double totalPaymentAmount = 0.0; // Initialize total payment amount
                for (int i = 0; i < finalResultCount; i++) {
                    int finalI = i; // Declare finalI inside the loop for lambda expression
                    JPanel medicinePanel = (JPanel) centerPanel.getComponent(i);
                    JTextField countField = (JTextField) medicinePanel.getComponent(2); // Assuming countField is the third component in each medicinePanel
                    int quantity = Integer.parseInt(countField.getText());
                    if (quantity > 0) {
                        String updateQuery = "UPDATE Pharmacy SET maxNo = maxNo - " + quantity + " WHERE medName = '" + medNameList[finalI] + "'";
                        stmt.executeUpdate(updateQuery);
                        totalPaymentAmount += quantity * priceList[finalI]; // Calculate total payment amount
                    }
                }
                // Insert payment record into paymentrecord table
                if (totalPaymentAmount > 0) {
                    String paymentQuery = "INSERT INTO paymentrecord (amount) VALUES (" + totalPaymentAmount + ")";
                    stmt.executeUpdate(paymentQuery);
                    JOptionPane.showMessageDialog(this, "Purchase and payment recorded successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "No purchase made.");
                }
                // Reset all count fields to 0
                for (int i = 0; i < finalResultCount; i++) {
                    JPanel medicinePanel = (JPanel) centerPanel.getComponent(i);
                    JTextField countField = (JTextField) medicinePanel.getComponent(2); // Assuming countField is the third component in each medicinePanel
                    countField.setText("0");
                }
                totalCost = 0.0; // Reset total cost
                updateTotalCost(0, true); // Update total cost display
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Navigation bar panel
        JPanel navBarPanel = new JPanel(new BorderLayout());
        contentPane.add(navBarPanel, BorderLayout.NORTH);

        // Heading label
        JLabel headingLabel = new JLabel("Pharmacy");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size and style
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        navBarPanel.add(headingLabel, BorderLayout.CENTER);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Here, you can define what happens when the "Back" button is clicked.
                // For example, you might want to close this window and show the previous one.
                dispose(); // Close the current window
                // new PreviousPage().setVisible(true); // Open the previous page, adjust as necessary
            }
        });

        // Back button
        
        backButton.addActionListener(e -> {
            try {
                PatientPage patientPage = new PatientPage(userID);
                patientPage.setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            
        });
        navBarPanel.add(backButton, BorderLayout.EAST);

        // Adjustments to ensure the nav bar and heading look good
        navBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void updateTotalCost(double priceList, boolean increment) {
        if (increment) {
            totalCost += priceList;
        } else {
            totalCost -= priceList;
        }
        totalCostLabel.setText("Total Cost: Rs." + String.format("%.2f", totalCost));
    }

    public static void main(String[] args) throws SQLException {
        EventQueue.invokeLater(() -> {
            try {
                PharmacyPage frame = new PharmacyPage("userID"); // Assuming userID is fetched and passed here correctly
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}