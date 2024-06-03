

import java.sql.*;

public class DerbyExample {
    public static void main(String[] args) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:derby:mydb;create=true");

        Statement statement = connection.createStatement();

//        statement.executeUpdate("DROP TABLE STUDENT");
        statement.executeUpdate("CREATE TABLE STUDENT (ID INT PRIMARY KEY, ESKA VARCHAR(20))");

        statement.executeUpdate("INSERT INTO STUDENT VALUES (1, 's555')");
        statement.executeUpdate("INSERT INTO STUDENT VALUES (2, 's666')");

        ResultSet resultSet = statement.executeQuery("SELECT * FROM STUDENT");

        while (resultSet.next())
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
                System.out.println(resultSet.getString(i));
    }
}
