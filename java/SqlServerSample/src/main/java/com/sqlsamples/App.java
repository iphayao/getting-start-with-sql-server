package com.sqlsamples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=master;;user=sa;password=Password123";

        try {
            // Load SQL Server JDBC driver and estiblish connection.
            System.out.println("Connecting to SQL Server ... ");
            try(Connection connection = DriverManager.getConnection(connectionUrl)) { 
                System.out.println("Done.");

                // Create a sample database
                System.out.println("Dropping and creating database 'SampleDB' ... ");
                String sql = "DROP DATABASE IF EXISTS [SampleDB]; CREATE DATABASE [SampleDB]";
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sql);
                    System.out.println("Done.");
                }

                // Create a Table and insert some sample data
                System.out.println("Creating sample tables with data, press ENTER to continue .... ");
                System.in.read();
                sql = new StringBuilder().append("USE SampleDB;").append("CREATE TABLE Employees (")
                        .append(" ID INT IDENTITY(1, 1) NOT NULL PRIMARY KEY, ").append(" NAME NVARCHAR(50), ")
                        .append(" Location NVARCHAR(50) ").append(");")
                        .append("INSERT INTO Employees (Name, Location) VALUES ").append("(N'Jared', N'Australia'), ")
                        .append("(N'Nikita', N'India'), ").append("(N'Tom', N'Germany');").toString();
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sql);
                    System.out.println("Done.");
                }

                // INSERT Demo
                System.out.println("Inserting a new row into table, press ENTER to continue ... ");
                System.in.read();
                sql = new StringBuilder().append("INSERT Employees (Name, Location) ").append("VALUES (?, ?);")
                            .toString();
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, "Jake");
                    statement.setString(2, "United States");
                    int rowsAffeced = statement.executeUpdate();
                    System.out.println(rowsAffeced + " row(s) inserted");
                }

                // UPDATE Demo
                String userToUpdate = "Nikita";
                System.out.println("Updating 'Location' for user '" + userToUpdate +"', press ENTER to continue ... ");
                System.in.read();
                sql = new StringBuilder().append("UPDATE Employees SET Location = ").append("N'United States'").append(" WHERE Name = ?")
                            .toString();
                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, userToUpdate);
                    int rowsAffeced = statement.executeUpdate();
                    System.out.println(rowsAffeced + " row(s) updated");
                }

                // DELETE Demo
                String userToDelete = "Jared";
                System.out.println("Deleting user '" + userToDelete + "', press ENTER to continue ... ");
                System.in.read();
                sql = "DELETE FROM Employees WHERE NAME = ?;";
                try(PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, userToDelete);
                    int rowsAffected = statement.executeUpdate();
                    System.out.println(rowsAffected + " row(s) deleted");
                }

                // READ Demo
                System.out.println("Reading data from table, press ENTER to continues ... ");
                System.in.read();
                sql = "SELECT id, Name, Location FROM Employees";
                try(Statement statement = connection.createStatement()) {
                    ResultSet resultSet = statement.executeQuery(sql);
                    while(resultSet.next()) {
                        System.out.println(resultSet.getInt(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3));
                    }
                }

                connection.close();
                System.out.println("All done.");
            }
        }
        catch(Exception e) {
            System.out.println();
            e.printStackTrace();
        }
    }
    
}