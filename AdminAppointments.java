import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminAppointments extends JFrame {
    private static String adminID;

    public AdminAppointments(String adminID) {
        AdminAppointments.adminID = adminID;
        setTitle("List of All Appointments");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        model.addColumn("Appointment ID");
        model.addColumn("Doctor ID");
        model.addColumn("Patient ID");
        model.addColumn("Appointment Date");
        model.addColumn("Ward ID");
        model.addColumn("Slot");

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospitalManagementSystem",
                    "root", "root@123");
            if (conn != null) {
                System.out.println("Connected to the database");
            }
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Appointment");

            while (rs.next()) {
                model.addRow(new Object[] { rs.getString("appID"), rs.getString("doctorID"), rs.getString("patientID"),
                        rs.getString("appDate"), rs.getString("wardID"), rs.getString("slot") });
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JButton backButton = new JButton("Back");
        backButton.setBounds(650, 10, 120, 30);
        backButton.addActionListener(e -> {
            try {
                dispose();
                new Admin(adminID).setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        mainPanel.add(backButton, BorderLayout.NORTH);

        add(mainPanel);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new AdminAppointments(adminID).setVisible(true);
        });
    }
}