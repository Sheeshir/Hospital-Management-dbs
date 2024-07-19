import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DoctorsFrame extends JFrame {

    protected static final String patientID = null;
    private JPanel contentPane;
    private static String docID;

    public DoctorsFrame(String docID) throws SQLException {
        DoctorsFrame.docID = docID;

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

        setTitle("Doctor Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 400);
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                ImageIcon imageIcon = new ImageIcon("HospitalManagement/src/Doctor_Background.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setLayout(null); // Use absolute layout
        setContentPane(contentPane);

        // Create sign out button
        JButton signOutButton = new JButton("Sign Out");
        signOutButton.setBounds(650, 10, 120, 30);
        contentPane.add(signOutButton);
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

        // Create time slot buttons
        String[] timeSlots = {"5PM - 6PM", "6PM - 7PM", "7PM - 8PM"};
        int yPos = 100; // Initial y-position for time slot buttons

        //get name and designation
        try (Statement stmt = connection.createStatement()){
            String nameQuery = "select * from doctor where employeeID = '" + docID + "'";
            ResultSet namResultSet = stmt.executeQuery(nameQuery);
            String docName = "", spec = "";
            if (namResultSet.next()){
                docName = namResultSet.getString("docName");
                spec = namResultSet.getString("specialisation");
                System.out.println("docName is " + docName);
            }
            JLabel doctorLabel = new JLabel("Welcome " + docName + " of department " + spec);
            doctorLabel.setBounds(50, 50, 300, 50);
            contentPane.add(doctorLabel);
        } catch (Exception e) {
            System.out.println(e);
        }

        
        for (int i = 0; i < 3; i++) {
            String timeSlot = timeSlots[i];
            JButton timeSlotButton = new JButton(timeSlot);
            timeSlotButton.setBounds(50, yPos, 150, 30);
            contentPane.add(timeSlotButton);
            yPos += 40; // Increment y-position for the next button

            //get the slots
            try (Statement stmt = connection.createStatement();) {
                String getWards = "select * from appointment where doctorID = '" + docID + "' and slot = " + (i + 1);
                ResultSet WardResultSet = stmt.executeQuery(getWards);

                int desiredWard = -1;
                if (WardResultSet.next()){
                    desiredWard = Integer.parseInt(WardResultSet.getString("wardID"));
                    System.out.println("des ward " + desiredWard);
                    JLabel wardOption = new JLabel(String.valueOf(desiredWard));
                    wardOption.setBounds(250, yPos - 40, 150, 30);
                    contentPane.add(wardOption);
                }
                else {
                    JLabel wardOption = new JLabel("No Appointments");
                    wardOption.setBounds(250, yPos - 40, 150, 30);
                    contentPane.add(wardOption);
                }
                // System.out.println("des ward " + desiredWard);

            } catch (Exception e) {
                System.out.println(e);
            }
        }

        // Create "Available Wards" button
        JButton availableWardsButton = new JButton("Available Wards");
        availableWardsButton.setBounds(50, 300, 150, 30);
        contentPane.add(availableWardsButton);
        availableWardsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                try {
                    // Open the WardPage
                    new WardPage(patientID).setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                DoctorsFrame frame = new DoctorsFrame(docID);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}