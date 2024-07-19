import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ModifyAppointment extends JFrame {

    private JPanel contentPane;
    private JLabel selectedDoctorLabel;
    private String selectedDoctor;
    private static String patientID;
    private int[] lastSelectedSlot = {-1}; // Initialize as -1
    private String[] lastSelectedDoctor = {"None"};

    public ModifyAppointment(String patientID) throws SQLException {
        ModifyAppointment.patientID = patientID;
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
            connection.setAutoCommit(false); // Start transaction
        }

        //test patietn ID
        System.out.println("Patient ID from appointment page: "+patientID);
        
        setTitle("Appointments");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 400);
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Set the background image
                ImageIcon imageIcon = new ImageIcon("appointment_background.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // Add title label
        JLabel titleLabel = new JLabel("Book Your Appointment Here", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font
        titleLabel.setForeground(Color.BLACK); // Set text color
        titleLabel.setOpaque(true); // Ensure label is opaque
        contentPane.add(titleLabel, BorderLayout.BEFORE_FIRST_LINE);

        // Create a panel to display doctor buttons
        JPanel doctorPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // Set vertical and horizontal gaps
        contentPane.add(doctorPanel, BorderLayout.WEST);

        // Create doctor buttons
        //list of doctors
        ArrayList<String> doctorNames = new ArrayList<>();
        // int selectedSlot = -1;
        try (Statement stmt = connection.createStatement()) {
            String query = "SELECT docName FROM Doctor";
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                doctorNames.add(resultSet.getString("docName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement stmt3 = connection.createStatement()){
            String query = "DELETE FROM Appointment WHERE patientID = '" + patientID + "'";
            stmt3.executeUpdate(query);

            String findWard = "select * from ward where patientID = '" + patientID + "'";
            ResultSet corrResultSet = stmt3.executeQuery(findWard);

            int firstWard = 0;
            if (corrResultSet.next()){
                firstWard = corrResultSet.getInt("wardID");
                System.out.println("first Ward is " + firstWard);
            }
            String bookWard = "Update ward set occupied = true where wardID = '" + firstWard + "'";
            
            stmt3.executeUpdate(bookWard);
        } catch (SQLException e){
            e.printStackTrace();
        }

        // String[] doctors = {"Doctor 1", "Doctor 2", "Doctor 3", "Doctor 4", "Doctor 5"};
        for (String doctor : doctorNames) {
            JPanel doctorButtonPanel = new JPanel(new BorderLayout());
            JButton doctorButton = new JButton(doctor);
            doctorButton.setPreferredSize(new Dimension(150, 30)); // Set button size
            doctorButtonPanel.add(doctorButton, BorderLayout.WEST);

            // Create time slot buttons
            JPanel timeSlotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Align time slots next to each other
            String[] timeSlots = {"5PM - 6PM", "6PM - 7PM", "7PM - 8PM"};
            boolean timeSlotFreeArray[] = new boolean[timeSlots.length];
            for (int i = 0; i < timeSlots.length ;i ++) {
                final int index = i + 1;
                String timeSlot = timeSlots[i];
                JButton timeSlotButton = new JButton(timeSlot);
                timeSlotButton.setOpaque(true);
                timeSlotButton.setBackground(Color.GREEN); // Change background color to green
                timeSlotButton.setForeground(Color.BLACK); // Set text color
                timeSlotButton.setBorderPainted(false);
                timeSlotButton.setPreferredSize(new Dimension(150, 30)); // Set button size

                try (Statement stmt = connection.createStatement()) {
                    //getting id
                    String empIdQuery = "SELECT employeeID FROM Doctor WHERE docName = '" + doctor + "'";
                    ResultSet empIdResult = stmt.executeQuery(empIdQuery);
        
                    String empId = ""; // Initialize empId
                    if (empIdResult.next()) {
                        empId = empIdResult.getString("employeeID");
                    }
                    String query = "select * from Appointment where doctorID = '" + empId + "' and slot = '" + index + "' and patientID != '" + patientID + "'";
                    ResultSet resultSet = stmt.executeQuery(query);
      
                    boolean isFound = false;
                    // int resultCount = 0; 
                    while (resultSet.next()) {
                        isFound = true;
                        System.out.println("Appointment Found ");
                        timeSlotFreeArray[i] = false;
                        timeSlotButton.setBackground(Color.GRAY);
                        break;
                    }
      
                    if (!isFound) {
                        // Redirect to the patient page after successful sign-up
                        timeSlotFreeArray[i] = true;
                        timeSlotButton.setBackground(Color.GREEN);
                    }
                    // System.out.println("Number of results: " + resultCount);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                timeSlotButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Get the selected doctor and time slot
                        if (timeSlotFreeArray[index - 1]){
                            lastSelectedSlot[0] = index;
                            lastSelectedDoctor[0] = doctor;
                            selectedDoctor = doctor;
                            // String selectedTimeSlot = timeSlot;
                            // Display confirmation message
                            // JOptionPane.showMessageDialog(null, "You selected: " + selectedDoctor + " at " + selectedTimeSlot);
                        }
                    }
                });
                timeSlotPanel.add(timeSlotButton);
            }
            doctorButtonPanel.add(timeSlotPanel, BorderLayout.CENTER);
            doctorPanel.add(doctorButtonPanel);
        }

        
        JButton bookAppointmentButton = new JButton("Book Appointment");
        bookAppointmentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle booking appointment action
                if (lastSelectedSlot[0] == -1) {
                    JOptionPane.showMessageDialog(null, "Please select an appointment before booking.");
                } else {
                    // Use the value of lastSelectedSlot here
                    System.out.println("Last selected slot: " + lastSelectedSlot[0]);
                    // Proceed with booking the appointment
                    int index = lastSelectedSlot[0];
                    selectedDoctor = lastSelectedDoctor[0];
                    try (Statement stmt = connection.createStatement()) {
                        String empIdQuery = "SELECT employeeID FROM Doctor WHERE docName = '" + selectedDoctor + "'";
                        ResultSet empIdResult = stmt.executeQuery(empIdQuery);
            
                        String empId = ""; // Initialize empId
                        if (empIdResult.next()) {
                            empId = empIdResult.getString("employeeID");
                        }

                        //getting ward
                        String tempDept = "Oncology";
                        String getWard = "select min(wardID) from ward where occupied = false and wardName = '" + tempDept + "'";
                        ResultSet WardResultSet =  stmt.executeQuery(getWard);
                        System.out.println(WardResultSet);
                        int firstWard = 0;
                        if (WardResultSet.next()){
                            firstWard = WardResultSet.getInt("min(wardID)");
                            System.out.println("The Minimum Ward ID is " + firstWard);
                        }

                        String query = "INSERT INTO Appointment (patientID, doctorID, slot, wardID) VALUES ('" + patientID + "', '" + empId + "', '" + index + "', " + firstWard + ")";
                        stmt.executeUpdate(query);

                        //remove ward from avaiable
                        String bookWard = "Update ward set occupied = true where wardID = '" + firstWard + "'";
                        
                        stmt.executeUpdate(bookWard);

                        JOptionPane.showMessageDialog(null, "You have successfully booked an appointment!");
                        connection.commit(); // Commit transaction
                        PatientPage patientPage = new PatientPage(patientID);
                        patientPage.setVisible(true);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        // contentPane.add(bookAppointmentButton, BorderLayout.CENTER);
        JPanel bookAppointmentPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bookAppointmentPanel.add(bookAppointmentButton);
        contentPane.add(bookAppointmentPanel, BorderLayout.CENTER);

        // Create a label to display selected doctor
        selectedDoctorLabel = new JLabel("Selected Doctor: None");
        contentPane.add(selectedDoctorLabel, BorderLayout.SOUTH);

        // Create a panel for the back button
        JPanel backButtonPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButtonPanel.add(backButton, BorderLayout.EAST);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    connection.rollback(); // Rollback transaction
                    PatientPage patientPage = new PatientPage(patientID);
                    patientPage.setVisible(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
        contentPane.add(backButtonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ModifyAppointment frame = new ModifyAppointment(patientID);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}