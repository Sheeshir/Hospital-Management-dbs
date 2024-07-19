import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
// import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Employee extends JFrame {

    protected static final String patientID = null;
    private JPanel contentPane;
    private static String adminID;

    public Employee(String adminID) throws SQLException {
        Employee.adminID = adminID;

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

        setTitle("Employee Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 400);
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                ImageIcon imageIcon = new ImageIcon("doctor_background.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS)); // Use absolute layout
        setContentPane(contentPane);

        // Create sign out button
        JPanel navJPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 100, 10));
        JButton signOutButton = new JButton("Sign Out");
        signOutButton.setBounds(650, 10, 120, 30);
        signOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                // Open the login page
                try {
                    new LoginFrame().setVisible(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

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
        navJPanel.add(signOutButton);
        navJPanel.add(backButton);
        contentPane.add(navJPanel);

        JLabel addEmployeeLabel = new JLabel("Add Employee:");
        JButton addButton = new JButton("Add");

        JPanel textFieldsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));

        String[] designations = {"Doctor", "Resident", "Admin"};
        String[] departments = {"None", "Cardiology", "Dermatologist", "Psychiatry", "Oncology", "Pediatrics", "Urology", "Psychology", "Neurology"};

        JTextField nameTextField = createPlaceholderTextField("Name");
        nameTextField.setSize(150, 50);
        JTextField ageTextField = createPlaceholderTextField("Age");
        JTextField empIDField = createPlaceholderTextField("EmployeeID");
        JTextField salaryField = createPlaceholderTextField("Salary");
        JComboBox<String> desDropdown = new JComboBox<>(designations);
        JTextField emaField = createPlaceholderTextField("Email");
        JComboBox<String> deptDropdown = new JComboBox<>(departments);
        textFieldsPanel.add(addEmployeeLabel);
        textFieldsPanel.add(nameTextField);
        textFieldsPanel.add(ageTextField);
        textFieldsPanel.add(empIDField);
        textFieldsPanel.add(salaryField);
        textFieldsPanel.add(desDropdown);
        textFieldsPanel.add(deptDropdown);
        textFieldsPanel.add(emaField);
        textFieldsPanel.add(addButton);

        contentPane.add(textFieldsPanel);

        JPanel deleteDoctorPanel = new JPanel();
        deleteDoctorPanel.setLayout(new FlowLayout());
        JLabel deleteEmployeeLabel = new JLabel("Delete Employee:");
        JButton deleteButton = new JButton("Delete");
        addButton.setPreferredSize(new Dimension(80, 30));
        JTextField deleteIDField = createPlaceholderTextField("Emp ID");
        deleteDoctorPanel.add(deleteEmployeeLabel);
        deleteDoctorPanel.add(deleteIDField);
        deleteDoctorPanel.add(deleteButton);
        contentPane.add(deleteDoctorPanel);

        addButton.addActionListener(e -> {
            try (Statement stmt = connection.createStatement()) {

                String name = nameTextField.getText();
                String id = empIDField.getText();
                String des = (String) desDropdown.getSelectedItem();
                String department = (String) deptDropdown.getSelectedItem();
                int salary = Integer.parseInt(salaryField.getText());
                int age = Integer.parseInt(ageTextField.getText());
                String email = emaField.getText();

                String addEmployee = "INSERT INTO employee (name, age, employeeID, salary, designation, email) VALUES ('" + name + "', " + age + ", '" + id + "', " + salary + ", '" + des + "', '" + email + "')";
                stmt.executeUpdate(addEmployee);

                if (des.equals("Doctor")) {
                    String addDoc = "INSERT INTO doctor (specialisation, employeeID, docName, salary) VALUES ('" + department + "', '" + id + "', '"  + name + "', " + salary + ")";
                    stmt.executeUpdate(addDoc);
                } else if (des.equals("Resident")) {
                    String addResident = "INSERT INTO resident (name, employeeID, salary) VALUES ('" + name + "', '" + id + "', " + salary + ")";
                    stmt.executeUpdate(addResident);
                }
                JOptionPane.showMessageDialog(null, "Employee successfully added.");
            }
            catch (Exception err) {
                JOptionPane.showMessageDialog(null, "Invalid entry");
                System.out.println(err);
            }
        });
        
        

        //delete
        deleteButton.addActionListener(e -> {
            try (Statement stmt = connection.createStatement()){
                String triggerDelete = "Drop trigger if exists delete_appointments_trigger";
                stmt.executeUpdate(triggerDelete);
                String createTriggerSQL = "CREATE TRIGGER delete_appointments_trigger AFTER DELETE ON Doctor FOR EACH ROW BEGIN DELETE FROM " + 
                "Appointment WHERE doctorID = OLD.employeeID; END;";
                stmt.executeUpdate(createTriggerSQL);
                String employeeID = deleteIDField.getText();
                String deleteDoc = "delete from doctor where employeeID = '" + employeeID + "' ";
                String deleteEmp = "delete from employee where employeeID = '" + employeeID + "' ";
                stmt.executeUpdate(deleteDoc);
                stmt.executeUpdate(deleteEmp);
                try {
                    JOptionPane.showMessageDialog(null, "Doctor successfully deleted.");
                    new Admin(adminID).setVisible(true);
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(null, "Doctor not found or could not be deleted.");
                    e1.printStackTrace();
                }
                System.out.println("Successfully deleted");
            } catch (Exception errr) {
                System.out.println(errr);
            }
        });

    }

    private JTextField createPlaceholderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder, 10);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
        return textField;
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Employee frame = new Employee(adminID);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}