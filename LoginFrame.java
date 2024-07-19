import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginFrame extends JFrame {
    private JPanel contentPane;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JButton signUpButton;
    // private JButton pharmacyButton;
    // private JButton appointmentButton; // Added button for appointments

    public LoginFrame() throws SQLException {

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

        //making frame
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 500);
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon(getClass().getResource("/login_background.jpg")).getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
            }
        };
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

         // Top panel that will contain both the heading and the nav bar
         JPanel topPanel = new JPanel();
         topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
         contentPane.add(topPanel, BorderLayout.NORTH);
 
         // Heading panel
         JPanel headingPanel = new JPanel();
         headingPanel.setLayout(new GridBagLayout()); // Update layout to GridBagLayout
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.anchor = GridBagConstraints.CENTER; // Set anchor to center for vertical alignment
         gbc.insets = new Insets(0, 0, 0, 0); // Adjust insets as needed
         JLabel headingLabel = new JLabel("Pilani General Hospital");
         headingLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size and style
         headingPanel.add(headingLabel, gbc); // Add headingLabel with GridBagConstraints
         topPanel.add(headingPanel);
 
         // Nav bar panel
         JPanel navBarPanel = new JPanel();
         navBarPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        // pharmacyButton = new JButton("Pharmacy");
        // navBarPanel.add(pharmacyButton);
        // // Appointment button added according to instructions
        // appointmentButton = new JButton("Appointments");
        // appointmentButton.addActionListener(e -> {
        //     this.setVisible(false); // Hide LoginFrame
        //     SwingUtilities.invokeLater(() -> {
        //         try {
        //             AppointmentPage appointmentPage = new AppointmentPage(); 
        //             appointmentPage.setVisible(true);
        //         } catch (Exception e1) {
        //             e1.printStackTrace();
        //         }
        //     });
        // });

        // navBarPanel.add(appointmentButton);
        topPanel.add(navBarPanel);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false); // Make it transparent to show the background image
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 50, 10, 50);

        // Adding labels for username and password
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        signInButton = new JButton("Sign In"); // Initialize signInButton
        signUpButton = new JButton("Sign Up"); // Initialize signUpButton 

        signInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String enteredUsername = usernameField.getText();
                final String enteredPassword = new String(passwordField.getPassword());
        
                try (Statement stmt = connection.createStatement()) {
                    String query = "SELECT * FROM loginCredentials WHERE userID = '" + enteredUsername + "' AND password = '" + enteredPassword + "'";
                    ResultSet resultSet = stmt.executeQuery(query);
        
                    if (resultSet.next()) {
                        // Authentication successful
                        System.out.println("Login Successful");
        
                        // Dispose of the login frame
                        dispose();
        
                        // Depending on the user type, open the corresponding frame
                        if (enteredUsername.startsWith("p")) {
                            EventQueue.invokeLater(() -> {
                                try {
                                    PatientPage patientPage = new PatientPage(enteredUsername);
                                    patientPage.setVisible(true);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            });
                        } else if (enteredUsername.startsWith("a")) {
                            EventQueue.invokeLater(() -> {
                                try {
                                    Admin adminPage = new Admin(enteredUsername);
                                    adminPage.setVisible(true);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            });
                        } else if (enteredUsername.startsWith("d")) {
                            DoctorsFrame docPage = new DoctorsFrame(enteredUsername);
                            docPage.setVisible(true);
                        } else {
                                    JOptionPane.showMessageDialog(null, "Invalid Username");
                                }
                    } else {
                        // Authentication failed
                        JOptionPane.showMessageDialog(null, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        //sign Up button
        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String enteredUsername = usernameField.getText(); // Assign the entered username to the class-level variable
                final String enteredPassword = new String(passwordField.getPassword());
                System.out.println("Username: " + enteredUsername);
                System.out.println("Password: " + enteredPassword);
        
                try (Statement stmt = connection.createStatement()) {
                    String query = "select * from loginCredentials where userID = '" + enteredUsername + "'";
                    ResultSet resultSet = stmt.executeQuery(query);
      
                    boolean isFound = false;
                    // int resultCount = 0; 
                    while (resultSet.next()) {
                        isFound = true;
                        System.out.println("Data Found = " + resultSet.getString("userID"));
                        // resultCount++;
                        JOptionPane.showMessageDialog(null, "Username already exists. Please choose another username.", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                    }
      
                    if (!isFound) {
                        // Redirect to the patient page after successful sign-up
                        System.out.println("Moving to patient page");
                        String insertLogin = "INSERT INTO loginCredentials (userId, password) VALUES ('" + enteredUsername + "' , '" + enteredPassword + "')";
                        stmt.executeUpdate(insertLogin);
                        final String finalEnteredUsername = new String(usernameField.getText());
                        LoginFrame.this.setVisible(false); // Hide LoginFrame
                        SwingUtilities.invokeLater(() -> {
                            try {
                                if (enteredUsername.startsWith("p")) {
                                    PatientPage patientPage = new PatientPage(finalEnteredUsername);
                                    patientPage.setVisible(true);
                                } else if (enteredUsername.startsWith("a")) {
                                    Admin adminPage = new Admin(finalEnteredUsername);
                                    adminPage.setVisible(true);
                                } else if (enteredUsername.startsWith("d")) {
                                    DoctorsFrame docPage = new DoctorsFrame(finalEnteredUsername);
                                    docPage.setVisible(true);
                                }
                                else {
                                    JOptionPane.showMessageDialog(null, "Invalid Username");
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        });
                    }
                    // System.out.println("Number of results: " + resultCount);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Set minimum sizes for the text fields
        usernameField.setMinimumSize(new Dimension(150, 20));
        passwordField.setMinimumSize(new Dimension(150, 20));

        centerPanel.add(usernameLabel, gbc);
        centerPanel.add(usernameField, gbc);
        centerPanel.add(passwordLabel, gbc);
        centerPanel.add(passwordField, gbc);

        gbc.fill = GridBagConstraints.NONE; // Prevents buttons from stretching
        centerPanel.add(signInButton, gbc);
        centerPanel.add(signUpButton, gbc);

        // Additional label under the "Sign Up" button
        JLabel signUpHintLabel = new JLabel("(Please click on this button if you do not have an account)");
        signUpHintLabel.setForeground(Color.RED); // Set text color to red
        signUpHintLabel.setFont(new Font("Arial", Font.PLAIN, 10)); // Set font size to small
        gbc.insets = new Insets(0, 50, 10, 50); // Adjust top inset to bring the label closer to the "Sign Up" button
        centerPanel.add(signUpHintLabel, gbc);

        // Footer panel with "Group 32"
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel footerLabel = new JLabel("Group 32");
        footerPanel.add(footerLabel);
        contentPane.add(footerPanel, BorderLayout.SOUTH);

        // pharmacyButton.addActionListener(e -> {
        //     this.setVisible(false); // Hide LoginFrame
        //     SwingUtilities.invokeLater(() -> {
        //         try {
        //             final String finalEnteredUsername = new String(usernameField.getText());
        //             System.out.println("User id fro login is " + finalEnteredUsername);
        //             final String finalEnteredPassword = new String(passwordField.getPassword()); // Declare finalEnteredPassword
        //             System.out.println("User id fro login is " + finalEnteredPassword);
        //             PharmacyPage pharmacyPage = new PharmacyPage(this, finalEnteredUsername); // Pass finalEnteredPassword
        //             pharmacyPage.setVisible(true);
        //         } catch (Exception e1) {
        //             e1.printStackTrace();
        //         } // Create and show PharmacyPage
        //     });
        //     });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}