import javax.swing.*;
import java.awt.*;
// import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctorsPage extends JFrame {
    private static String adminID;

    public DoctorsPage(String adminID) {
        DoctorsPage.adminID = adminID;
        setTitle("Doctors Page");
        setSize(700, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 3, 10, 10));

        List<String> employeeIDs = new ArrayList<>();
        List<String> docNames = new ArrayList<>();
        List<Double> salaries = new ArrayList<>();

        fetchDoctorDataFromDatabase(employeeIDs, docNames, salaries);

        for (int i = 0; i < employeeIDs.size(); i++) {
            JLabel employeeIDLabel = new JLabel("Employee ID: " + employeeIDs.get(i));
            JLabel docNameLabel = new JLabel("Doctor Name: " + docNames.get(i));
            JLabel salaryLabel = new JLabel("Salary: " + salaries.get(i));
            mainPanel.add(employeeIDLabel);
            mainPanel.add(docNameLabel);
            mainPanel.add(salaryLabel);
        }

        add(mainPanel, BorderLayout.CENTER);

        // Navigation Bar
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        // backButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         dispose(); // Close the current DoctorsPage
        //         new Admin(adminID).setVisible(true); // Open the Admin page
        //     }
        // });

        backButton.addActionListener(e -> {
            try {
                new Admin(adminID).setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        navBar.add(backButton);
        add(navBar, BorderLayout.NORTH);
    }

    private void fetchDoctorDataFromDatabase(List<String> employeeIDs, List<String> docNames, List<Double> salaries) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/HospitalManagementSystem",
                    "root", "root@123");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT EmployeeID, docName, salary FROM Doctor");

            while (resultSet.next()) {
                employeeIDs.add(resultSet.getString("EmployeeID"));
                docNames.add(resultSet.getString("docName"));
                salaries.add(resultSet.getDouble("salary"));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new DoctorsPage(adminID).setVisible(true);
        });
    }
}