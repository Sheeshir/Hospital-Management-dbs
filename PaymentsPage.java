import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentsPage extends JFrame {
    private static String adminID;

    public PaymentsPage(String adminID) {
        PaymentsPage.adminID = adminID;
        setTitle("Payments Page");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 2, 10, 10));

        List<String> paymentIDs = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();

        fetchPaymentDataFromDatabase(paymentIDs, amounts);

        for (int i = 0; i < paymentIDs.size(); i++) {
            JLabel paymentIDLabel = new JLabel("Payment ID: " + paymentIDs.get(i));
            JLabel amountLabel = new JLabel("Amount: " + amounts.get(i));
            mainPanel.add(paymentIDLabel);
            mainPanel.add(amountLabel);
        }

        double totalPayment = calculateTotalPayment(amounts);
        JLabel totalLabel = new JLabel("Total Payment: " + totalPayment);
        mainPanel.add(totalLabel);

        add(mainPanel, BorderLayout.CENTER);

        // Navigation Bar
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        navBar.add(backButton);
        backButton.addActionListener(e -> {
            try {
                new Admin(adminID).setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        add(navBar, BorderLayout.NORTH);
    }

    private void fetchPaymentDataFromDatabase(List<String> paymentIDs, List<Double> amounts) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospitalManagementSystem",
                    "root", "root@123");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT PaymentID, amount FROM PaymentRecord");

            while (resultSet.next()) {
                paymentIDs.add(resultSet.getString("PaymentID"));
                amounts.add(resultSet.getDouble("amount"));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double calculateTotalPayment(List<Double> amounts) {
        double total = 0;
        for (Double amount : amounts) {
            total += amount;
        }
        return total;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new PaymentsPage(adminID).setVisible(true);
        });
    }
}