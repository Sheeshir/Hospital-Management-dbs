import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
// import java.util.ArrayList;

public class WardPage extends JFrame {
    int docNumber = 1;
    private JPanel contentPane;
    // private JLabel selectedWardLabel;
    // private String selectedWard;
    private static String patientID;
    // private int[] lastSelectedBed = {-1}; // Initialize as -1
    // private String[] lastSelectedWard = {"None"};


    public WardPage(String patientID) throws SQLException{
        WardPage.patientID = patientID;
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
        WardPage.patientID = patientID;

        setTitle("Ward Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 400);
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Set the background image
                ImageIcon imageIcon = new ImageIcon("ward_background.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 500, 20));
        contentPane.setOpaque(false);
        setContentPane(contentPane);

        // Add title label
        JLabel titleLabel = new JLabel("Ward Availability", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font
        titleLabel.setForeground(Color.BLACK); // Set text color
        titleLabel.setOpaque(true); // Ensure label is opaque
        contentPane.add(titleLabel, BorderLayout.NORTH);

        try (Statement stmt =  connection.createStatement() ){
            String dropView = "DROP VIEW IF EXISTS DoctorAppointmentView";
            stmt.executeUpdate(dropView);
            String createView = "CREATE VIEW DoctorAppointmentView AS select d.docName as docName, d.specialisation as "+
            "specialisation, a.wardID as wardID from appointment a join doctor d on d.employeeID = a.doctorID where a.wardID is not NULL;";
            stmt.executeUpdate(createView);

            String totWards = "select count(*) as totalWards from ward";
            ResultSet totWardsSet = stmt.executeQuery(totWards);

            int totalCount = 0;
            if (totWardsSet.next()){
                totalCount = totWardsSet.getInt("totalWards");
                System.out.println("first Ward is " + totalCount);
            }

            String avWards = "select count(*) as avWards from DoctorAppointmentView";
            ResultSet avWardsSet = stmt.executeQuery(avWards);

            int avCount = 0;
            if (avWardsSet.next()){
                avCount = avWardsSet.getInt("avWards");
                System.out.println("first Ward is " + avCount);
            }

            JLabel totalDisplay = new JLabel("Total Wards: " + totalCount + " and Free Wards: " + (totalCount - avCount));
            totalDisplay.setBounds(40, 50, 100, 50);
            contentPane.add(totalDisplay);

            // String getDetails = "select count(*) , specialisation from DoctorAppointmentView group by specialisation";
            String getAllDetails = "select docName,specialisation, wardID from DoctorAppointmentView ";
            ResultSet viewResultSet = stmt.executeQuery(getAllDetails);

            String[] docNames = new String[avCount];
            String[] specialisation = new String[avCount];
            int[] wardIDList = new int[avCount];

            int resultCount = 0;
            while (viewResultSet.next()) {
                docNames[resultCount] = viewResultSet.getString("docName");
                specialisation[resultCount] = viewResultSet.getString("specialisation");
                wardIDList[resultCount] = Integer.parseInt(viewResultSet.getString("wardID"));
                resultCount++;
            }

            // Example data - replace with actual data retrieval logic
            JPanel headingJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));
            JLabel docNameHead = new JLabel("Doctor Name");
            JLabel specNameHead = new JLabel("Specialisation");
            JLabel wardNameHead = new JLabel("Ward ID");
            headingJPanel.add(docNameHead);
            headingJPanel.add(specNameHead);
            headingJPanel.add(wardNameHead);
            contentPane.add(headingJPanel);

            JPanel infoPanel = new JPanel(new GridLayout(avCount, 3, 75, 10));
            for (int i = 0; i < avCount; i++){
                JLabel docNameLabel = new JLabel(docNames[i]);
                JLabel specialisationLabel = new JLabel(specialisation[i]);
                JLabel wardLabel = new JLabel(String.valueOf(wardIDList[i]));
                infoPanel.add(docNameLabel);
                infoPanel.add(specialisationLabel);
                infoPanel.add(wardLabel);
                contentPane.add(infoPanel);
            }
            // String docName = "Dr. Smith";
            // String specialisations = "Cardiology";
            // String wardIDs = "Ward 101";
            
            // // Create and configure the docName label
            // JLabel docNameLabel = new JLabel("Doctor Name: " + docName);
            // docNameLabel.setBounds(20, 50, 300, 30); // Adjust position and size as needed
            // // infoPanel.add(docNameLabel, BorderLayout.WEST);
            // infoPanel.add(docNameLabel);
            
            // // Create and configure the specialisation label
            // JLabel specialisationLabel = new JLabel("Specialisation: " + specialisations);
            // specialisationLabel.setBounds(20, 90, 300, 30); // Adjust position and size as needed
            // // infoPanel.add(specialisationLabel, BorderLayout.WEST);
            // infoPanel.add(specialisationLabel);
            
            // // Create and configure the wardID label
            // JLabel wardIDLabel = new JLabel("Ward ID: " + wardIDs);
            // wardIDLabel.setBounds(20, 130, 300, 30); // Adjust position and size as needed
            // infoPanel.add(wardIDLabel, BorderLayout.WEST);
            // contentPane.add(infoPanel, BorderLayout.NORTH);

        } catch (Exception e) {
            System.out.println(e);
        }

        // Create a panel for the back button
        JPanel backButtonPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        // backButtonPanel.add(backButton, BorderLayout.EAST);
        backButtonPanel.add(backButton);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                // Open the login page
                try {
                    dispose();
                    new LoginFrame().setVisible(true);
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
                WardPage frame = new WardPage(patientID);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}