package Database;

import jdk.jfr.Name;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    public Connection EstablishConnection() throws ClassNotFoundException, SQLException {
        String dbName= "TErrands";
        String userName = "root";
        String password = "";

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/"+dbName,userName,password);

        return connection;

    }

}
