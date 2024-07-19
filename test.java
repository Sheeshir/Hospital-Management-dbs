import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
// import java.io.File;
// import java.net.MalformedURLException;
// import java.net.URL;
// import java.net.URLClassLoader;

public class test {
    public static void main(String args[]) throws ClassNotFoundException, SQLException {
        // URL url = new File("/Users/rishee/Downloads/mysql-connector-j-8.0.26/mysql-connector-java-8.0.26.jar").toURI().toURL();
        // URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{url});

        Class.forName("com.mysql.cj.jdbc.Driver");

        String urlDB = "jdbc:mysql://localhost:3306/HospitalManagementSystem";
        String username = "root";
        String password = "root@123";
        Connection connection = DriverManager.getConnection(urlDB, username, password);

        if (connection != null) {
            System.out.println("Connection established");
        }

        Statement stmt = connection.createStatement();
        String query = "select * from feedback";
        ResultSet resultSet = stmt.executeQuery(query);

        boolean isFound = false;
        int resultCount = 0; 
        while (resultSet.next()) {
            isFound = true;
            System.out.println("Data Found = " + resultSet.getString("content"));
            resultCount++;
        }
        if (!isFound) {
            System.out.println("No Record Found");
        }
        System.out.println("Number of results: " + resultCount);

        resultSet.close();

        query = "INSERT INTO feedback (feedbackID, content) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, 2);
        preparedStatement.setString(2, "Bye");
        int rowsInserted = preparedStatement.executeUpdate();
        System.out.println(rowsInserted + " row(s) inserted.");

        String updateQuery = "UPDATE feedback SET feedbackId = feedbackId + 1";
        int rowsUpdated = stmt.executeUpdate(updateQuery);
        System.out.println(rowsUpdated + " row(s) updated.");
        String dropTable = "DROP TABLE IF EXISTS test";
        stmt.executeUpdate(dropTable);
        String createTableQuery = "CREATE TABLE test (" +
                "testId INT," +
                "testContent VARCHAR(255)" +
                ")";
        stmt.executeUpdate(createTableQuery);
        System.out.println("Table 'test' created successfully.");

        stmt.close();
        connection.close();
    }
}