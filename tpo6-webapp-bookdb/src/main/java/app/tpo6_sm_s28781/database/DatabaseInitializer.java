package app.tpo6_sm_s28781.database;

import java.sql.*;

public class DatabaseInitializer {

    private static final String DB_URL = "jdbc:derby:ksidb;create=true";

    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final String[] DROP_TABLE_QUERIES = {
            "DROP TABLE POZYCJE",
            "DROP TABLE AUTOR",
            "DROP TABLE WYDAWCA"
    };
    private static final String[] CREATE_TABLE_QUERIES = {
            "CREATE TABLE AUTOR (AUTID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), NAME VARCHAR(255) NOT NULL, PRIMARY KEY(AUTID))",
            "CREATE TABLE WYDAWCA (WYDID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), NAME VARCHAR(255) NOT NULL, PRIMARY KEY(WYDID))",
            "CREATE TABLE POZYCJE (ISBN CHAR(13) NOT NULL, AUTID INTEGER NOT NULL, TYTUL VARCHAR(255) NOT NULL, WYDID INTEGER NOT NULL, ROK INTEGER NOT NULL, CENA REAL, PRIMARY KEY(ISBN), FOREIGN KEY(AUTID) REFERENCES AUTOR(AUTID), FOREIGN KEY(WYDID) REFERENCES WYDAWCA(WYDID))"
    };

    private static final String[] IMPORT_DATA_QUERIES = {
            "INSERT INTO AUTOR (NAME) VALUES ('Autor1'), ('Autor2'), ('Autor3')",
            "INSERT INTO WYDAWCA (NAME) VALUES ('Wydawca1'), ('Wydawca2'), ('Wydawca3')",
            "INSERT INTO POZYCJE (ISBN, AUTID, TYTUL, WYDID, ROK, CENA) VALUES ('9781234567897', 1, 'Tytul1', 1, 2020, 49.99), ('9781234567898', 2, 'Tytul2', 2, 2019, 39.99), ('9781234567899', 3, 'Tytul3', 3, 2021, 59.99)",
    };

    private static final String[] SHOW_TABLE_QUERIES = {
            "SELECT * FROM AUTOR",
            "SELECT * FROM WYDAWCA",
            "SELECT * FROM POZYCJE"
    };

    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
//            dropTables(connection);
            createTables(connection);
            insertData(connection);
            showTableData(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void dropTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String query : DROP_TABLE_QUERIES) {
                statement.executeUpdate(query);
            }
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String query : CREATE_TABLE_QUERIES) {
                statement.executeUpdate(query);
            }
        }
    }

    private static void insertData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String query : IMPORT_DATA_QUERIES) {
                statement.executeUpdate(query);
            }
        }
    }

    private static void showTableData (Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for (String query : SHOW_TABLE_QUERIES) {
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        System.out.print(resultSet.getMetaData().getColumnName(i) + ": ");
                        System.out.print(resultSet.getString(i) + " ");
                    }
                    System.out.println();
                }
            }
        }
    }
}
