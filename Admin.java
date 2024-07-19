import javax.swing.*;
import java.awt.*;
// import java.awt.event.FocusAdapter;
// import java.awt.event.FocusEvent;
// import javax.swing.*;
// import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
import java.sql.SQLException;
// import java.sql.Statement;

public class Admin extends JFrame {
    private JPanel contentPane;
    static String adminID;

    public Admin(String adminID) throws SQLException {
        Admin.adminID = adminID;
        setTitle("Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600)); // Adjusted for additional fields
        setLocationRelativeTo(null); // Center the window

        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Adjust path to your background image
                g.drawImage(new ImageIcon(getClass().getResource("/admin_background.jpg")).getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
                //g.drawImage(new ImageIcon("HOSPITALMANAGEMENT/src/admin_background.jpg").getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

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
        if(connection != null){
            System.out.println("Connected to the database");
        }

        // Navigation bar panel
        JPanel navBarPanel = new JPanel(new BorderLayout());
        
        JButton signOutButton = new JButton("Sign Out");
        signOutButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            signOut();
        }
    });
        signOutButton.setOpaque(true);
        signOutButton.setBorderPainted(false);
        signOutButton.setBackground(Color.RED);
        signOutButton.setForeground(Color.WHITE);
        navBarPanel.setOpaque(false); // Make navBarPanel transparent
        
        navBarPanel.add(signOutButton, BorderLayout.EAST);
        contentPane.add(navBarPanel, BorderLayout.NORTH);

        // Heading label
        JLabel headingLabel = new JLabel("      ADMIN");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size and style
        headingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        navBarPanel.add(headingLabel, BorderLayout.CENTER);

        // Main content panel with BoxLayout for vertical alignment
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false); // Make mainContentPanel transparent
        JScrollPane scrollPane = new JScrollPane(mainContentPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        contentPane.add(scrollPane, BorderLayout.CENTER);

//         // Adding components to the main content panel
//         addComponentWithLabel(mainContentPanel, "Add Doctor:", 6); 
//         addComponentWithLabel(mainContentPanel, "Delete Doctor:", 0);

         // Add the four buttons to the main content panel
         addButton(mainContentPanel, "Employee");
        addButton(mainContentPanel, "Appointments");
        addButton(mainContentPanel, "Payments");
        addButton(mainContentPanel, "Wards");
        addButton(mainContentPanel, "Doctors");

    pack(); // Adjusts window size to fit components
}

private void addButton(JPanel panel, String buttonText) {
    JButton button = new JButton(buttonText);
    button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if ("Appointments".equals(buttonText)) {
                Admin.this.dispose(); // Close the Admin window
                new AdminAppointments(adminID).setVisible(true); // Open the AdminAppointments page
            } else if ("Payments".equals(buttonText)) {
                Admin.this.dispose(); // Close the Admin window
                new PaymentsPage(adminID).setVisible(true); // Open the PaymentsPage
            } else if ("Doctors".equals(buttonText)) {
                Admin.this.dispose(); // Close the Admin window
                new DoctorsPage(adminID).setVisible(true); // Open the DoctorsPage
            } else if ("Wards".equals(buttonText)) {
                Admin.this.dispose(); // Close the Admin window
                try {
                    new WardPage(adminID).setVisible(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } // Open the DoctorsPage
            } else if ("Employee".equals(buttonText)) {
                Admin.this.dispose(); // Close the Admin window
                try {
                    new Employee(adminID).setVisible(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } // Open the DoctorsPage
            }
        }
    });
    JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    flowPanel.setOpaque(false); // Make flowPanel transparent
    flowPanel.add(button);
    panel.add(flowPanel);
}
    //test2
    // private void addComponentWithLabel(JPanel panel, String labelText, int additionalFields) {
        
    //     JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    //     flowPanel.setOpaque(false); // Make flowPanel transparent
    //     JLabel label = new JLabel(labelText);
    //     JTextField textField = createPlaceholderTextField("Main Field");
    //     //JButton actionButton = new JButton(labelText.split(" ")[0]); // Renamed the first button
    //     //flowPanel.add(actionButton);// Uses first word of label as button text
    //     flowPanel.add(label);
    //     flowPanel.add(textField);
    //     for (int i = 0; i < additionalFields; i++) {
    //         JTextField additionalField = createPlaceholderTextField("Field " + (i + 1));
    //         flowPanel.add(additionalField);
    //     }
    //     panel.add(flowPanel);

    //     JButton deleteButton = new JButton("Modify"); // Renamed the second button
    //     deleteButton.addActionListener(new ActionListener() {
    //     @Override
    //     public void actionPerformed(ActionEvent e) {
    //         String doctorId = textField.getText(); // Assuming textField is your input field for doctor's ID
    //         deleteDoctor(doctorId);
    //     }
    //     });

    //     //test3
        
    // flowPanel.add(deleteButton);
    // if ("Add Doctor:".equals(labelText)) {
    //     JLabel instructionLabel = new JLabel("<html><div style='color: red; font-size: 10px;'>(Enter the fields as: ID, Name, Salary, Specialization, Age, Email, Designation)</div></html>");
    //     flowPanel.add(instructionLabel);
    // }
    // if ("Delete Doctor:".equals(labelText)) {
    //     JLabel instructionLabel = new JLabel("<html><div style='color: red; font-size: 10px;'>(Enter the field employeeID)</div></html>");
    //     flowPanel.add(instructionLabel);
    // }
    // if ("Add Medicine:".equals(labelText)) {
    //     JLabel instructionLabel = new JLabel("<html><div style='color: red; font-size: 10px;'>(Enter the fields as: medID,medName,price,quantity)</div></html>");
    //     flowPanel.add(instructionLabel);
    // }
    // if ("Delete Medicine:".equals(labelText)) {
    //     JLabel instructionLabel = new JLabel("<html><div style='color: red; font-size: 10px;'>(Enter the field medID)</div></html>");
    //     flowPanel.add(instructionLabel);
    // }
    // }

    // private JTextField createPlaceholderTextField(String placeholder) {
    //     JTextField textField = new JTextField(placeholder, 10);
    //     textField.setForeground(Color.GRAY);
    //     textField.addFocusListener(new FocusAdapter() {
    //         @Override
    //         public void focusGained(FocusEvent e) {
    //             if (textField.getText().equals(placeholder)) {
    //                 textField.setText("");
    //                 textField.setForeground(Color.BLACK);
    //             }
    //         }

    //         @Override
    //         public void focusLost(FocusEvent e) {
    //             if (textField.getText().isEmpty()) {
    //                 textField.setForeground(Color.GRAY);
    //                 textField.setText(placeholder);
    //             }
    //         }
    //     });
    //     return textField;
    // }

    // private void deleteDoctor(String doctorId) {
    // String urlDB = "jdbc:mysql://localhost:3306/HospitalManagementSystem";
    // String username = "root";
    // String password = "root@123";
    // String query = "DELETE FROM doctor WHERE employeeID = ?"; // Assuming your table is named 'doctors' and has an 'id' column

    // try (Connection connection = DriverManager.getConnection(urlDB, username, password);
    //      PreparedStatement statement = connection.prepareStatement(query)) {
        
    //     statement.setString(1, doctorId); // Set the ID in the query
    //     int rowsDeleted = statement.executeUpdate();
        
    //     if (rowsDeleted > 0) {
    //         System.out.println("A doctor was deleted successfully!");
    //         } else {
    //         System.out.println("No doctor found with the provided ID.");
    //         }
    //     } catch (SQLException e) {
    //     e.printStackTrace();
    //     System.out.println("Deletion failed.");
    //     }
    // }

    // private void addDoctor(String specialization, String employeeID, String docName, double salary) {
    //     String urlDB = "jdbc:mysql://localhost:3306/HospitalManagementSystem";
    //     String username = "root";
    //     String password = "root@123";
    //     String query = "INSERT INTO doctor (specialization, employeeID, docName, salary) VALUES (?, ?, ?, ?)";
    
    //     try (Connection connection = DriverManager.getConnection(urlDB, username, password);
    //          PreparedStatement statement = connection.prepareStatement(query)) {
            
    //         statement.setString(1, specialization);
    //         statement.setString(2, employeeID);
    //         statement.setString(3, docName);
    //         statement.setDouble(4, salary);
    
    //         int rowsInserted = statement.executeUpdate();
            
    //         if (rowsInserted > 0) {
    //             System.out.println("A new doctor was added successfully!");
    //         } else {
    //             System.out.println("Failed to add a new doctor.");
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         System.out.println("Addition failed.");
    //     }
    // }

    private void signOut() {
        this.dispose(); // Close the current Admin window
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame loginFrame = new LoginFrame(); // Assuming your login frame class is named Login
                    loginFrame.setVisible(true); // Show the login frame
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            try {
                Admin frame = new Admin(adminID);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
