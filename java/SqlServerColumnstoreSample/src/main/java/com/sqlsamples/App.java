package com.sqlsamples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println("*** SQL Server Columnstore demo **");

        // Update username and password below
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=master;user=sa;password=Password123";

        // Load SQL Server JDBC driver and estiblish connection.
        try {
            System.out.println("Connection to SQL Server ... ");
            try(Connection connection = DriverManager.getConnection(connectionUrl)) {
                // Create an example database
                System.out.println("Dropping and creating database 'SampleDB' ... ");
                String sql = "DROP DATABASE IF EXISTS [SampleDB]; CREATE DATABASE [SampleDB]";

                // Create an example database
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sql);
                    System.out.println("Done");                    
                }

                // Insert 5 million rows into table 'Table_with_5M_rows'
                System.out.println("Inserting 5 million into table 'Table_with_5M_rows'. This take ~1 minutes, please wait ... ");

                sql = new StringBuilder().append("USE SampleDB; ")
                            .append("WITH a AS (SELECT * FROM (VALUES(1),(2),(3),(4),(5),(6),(7),(8),(9),(10)) AS a(a))")
                            .append("SELECT TOP(5000000)").append("ROW_NUMBER() OVER (ORDER BY a.a) AS OrderItemId ")
                            .append(",a.a + b.a + c.a + d.a + e.a + f.a + g.a + h.a AS OrderId ")
                            .append(",a.a * 10 AS Price ")
                            .append(",CONCAT(a.a, N' ', b.a, N' ', c.a, N' ', d.a, N' ', e.a, N' ', f.a, N' ', g.a, N' ', h.a) AS ProductName ")
                            .append("INTO Table_with_5M_rows ")
                            .append("FROM a, a AS b, a AS c, a AS d, a AS e, a AS f, a AS g, a AS h;").toString();
                try(Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sql);
                    System.out.println("Done.");
                }

                // Execute SQL query without a columnstore index
                long elapsedTimeWithoutIndex = SumPrince(connection);
                System.out.println("Query time WITHOUT columnstore index: " + elapsedTimeWithoutIndex + "ms");

                System.out.print("Adding a columnstore to table 'Table_with_5M_rows'");
                sql = "CREATE CLUSTERED COLUMNSTORE INDEX columnstoreindex ON Table_with_5M_rows;";
                try(Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sql);
                    System.out.println("Done.");
                }

                // Execute the same SQL query agin after the columnstore index is added
                long elapsedTimeWithIndex = SumPrince(connection);
                System.out.println("Query time WITH columnstore index: " + elapsedTimeWithIndex + "ms");

                // Calulate performance gain from adding columnstore index
                System.out.println("Performance improvement with columnstore index: " + elapsedTimeWithoutIndex/elapsedTimeWithIndex + "x!");
                connection.close();

            }
        }
        catch (Exception e) {
            System.out.println("");
            e.printStackTrace();
        }
    }

	private static long SumPrince(Connection connection) {
        String sql = "SELECT SUM(Price) FROM Table_with_5M_rows";
        long startTime = System.currentTimeMillis();
        try(Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while(resultSet.next()) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                return elapsedTime;
            }
        }
        catch (Exception e) {
            System.out.println("");
            e.printStackTrace();
        }
		return 0;
	}
}
