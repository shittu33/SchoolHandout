package com.example.abumuhsin.udusmini_library.MysqlUtils;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnection {
    String classs = "com.mysql.jdbc.Driver";

    /* Create database connection url. */
    String mysqlConnUrl_root = "jdbc:mysql://";
//    String mysqlConnUrl_root = "jdbc:mysql://192.168.173.101/";


    public Connection connect(String ip, String databaseName, String mysqlUserName, String mysqlPassword) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection Conn = null;
        String ConUrl = null;
        try {
            Class.forName(classs);
            Conn = DriverManager.getConnection(mysqlConnUrl_root + ip + "/" + databaseName, mysqlUserName, mysqlPassword);
            Conn = DriverManager.getConnection(ConUrl);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Conn;
    }
}
