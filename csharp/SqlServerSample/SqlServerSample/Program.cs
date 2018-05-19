using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SqlServerSample
{
    class Program
    {
        static void Main(string[] args)
        {
            try
            {
                // Build connection string
                SqlConnectionStringBuilder builder = new SqlConnectionStringBuilder();
                builder.DataSource = "localhost";
                builder.InitialCatalog = "master";
                builder.IntegratedSecurity = true;

                // Connect to SQL
                Console.Write("Connecting to SQl Server ...");
                using (SqlConnection connection = new SqlConnection(builder.ConnectionString))
                {
                    connection.Open();
                    Console.WriteLine("Done.");

                    // Create a sample database
                    Console.Write("Dropping and creating database 'SampleDB' ... ");
                    String sql = "DROP DATABASE IF EXISTS [SampleDB]; CREATE DATABASE [SampleDB]";
                    using (SqlCommand command = new SqlCommand(sql, connection))
                    {
                        command.ExecuteNonQuery();
                        Console.WriteLine("Done.");
                    }

                    // Create a Table and insert some sample data
                    Console.Write("Creating sample table with data, press any key to continue ... ");
                    Console.ReadKey(true);
                    StringBuilder sb = new StringBuilder();
                    sb.Append("USE SampleDB;");
                    sb.Append("CREATE TABLE Employees(");
                    sb.Append("Id INT IDENTITY(1, 1) NOT NULL PRIMARY KEY,");
                    sb.Append("NAME NVARCHAR(50),");
                    sb.Append("Location NVARCHAR(50));");
                    sb.Append("INSERT INTO Employees (Name, Location) VALUES ");
                    sb.Append("(N'Jared', N'Australia'),");
                    sb.Append("(N'Nikita', N'India'),");
                    sb.Append("(N'Tom', N'Germany');");
                    sql = sb.ToString();
                    using (SqlCommand command = new SqlCommand(sql, connection))
                    {
                        command.ExecuteNonQuery();
                        Console.WriteLine("Done.");
                    }

                    // Insert Demo
                    Console.Write("Inserting a new row into table, press any key to continues ... ");
                    Console.ReadKey(true);
                    sb.Clear();
                    sb.Append("INSERT Employees (Name, Location) ");
                    sb.Append("VALUES (@name, @location);");
                    sql = sb.ToString();
                    using(SqlCommand command = new SqlCommand(sql, connection))
                    {
                        command.Parameters.AddWithValue("@name", "Jake");
                        command.Parameters.AddWithValue("@location", "United States");
                        command.ExecuteNonQuery();
                        Console.WriteLine("Done.");
                    }

                    // Update Demo
                    String userToUpdate = "Nikita";
                    Console.Write("Updating 'Location' for user '" + userToUpdate + "', press any key to continues ... ");
                    Console.ReadKey(true);
                    sb.Clear();
                    sb.Append("UPDATE Employees SET Location = N'United States' WHERE Name = @name");
                    sql = sb.ToString();
                    using(SqlCommand command = new SqlCommand(sql, connection))
                    {
                        command.Parameters.AddWithValue("@name", userToUpdate);
                        int rowsAffected = command.ExecuteNonQuery();
                        Console.WriteLine(rowsAffected + " row(s) updated");
                    }

                    // Delete Demo
                    String userToDelete = "Jared";
                    Console.Write("Deleting user '" + userToDelete + "', press any key to continues ... ");
                    Console.ReadKey(true);
                    sb.Clear();
                    sb.Append("DELETE FROM Employees WHERE Name = @name;");
                    sql = sb.ToString();
                    using(SqlCommand command = new SqlCommand(sql, connection))
                    {
                        command.Parameters.AddWithValue("@name", userToDelete);
                        int rowsAffected = command.ExecuteNonQuery();
                        Console.WriteLine(rowsAffected + " row(s) updated");
                    }

                    // Read Demo
                    Console.WriteLine("Reading data from table, press any key to continues ... ");
                    Console.ReadKey(true);
                    sql = "SELECT Id, Name, Location FROM Employees;";
                    using(SqlCommand command = new SqlCommand(sql, connection))
                    {
                        using(SqlDataReader reader = command.ExecuteReader())
                        {
                            while(reader.Read())
                            {
                                Console.WriteLine("{0} {1} {2}", reader.GetInt32(0), reader.GetString(1), reader.GetString(2));
                            }
                        }
                    }
                }
            }
            catch(SqlException e)
            {
                Console.WriteLine(e.ToString());
            }

            Console.WriteLine("All done. Press any key to finish ...");
            Console.ReadKey(true);
        }
    }
}
