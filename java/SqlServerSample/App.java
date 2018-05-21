package com.sqlsamples;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * App
 */
public class App {

    public static void main(String[] args) {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=master;integratedSecurity=true";

        try {
            // Load SQL Server JDBC driver and estiblish connection.
            System.out.println("Connecting to SQL Server ... ");
            try(Connection connection = DriverManager.getConnection(connectionUrl)) { 
                System.out.println("Done.");
            }
        }
        catch(Exception e) {
            System.out.println();
            e.printStackTrace();
        }
    }
    
}