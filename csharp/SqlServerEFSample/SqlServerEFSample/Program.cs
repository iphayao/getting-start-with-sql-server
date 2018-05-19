using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SqlServerEFSample
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
                builder.InitialCatalog = "EFSample";
                builder.IntegratedSecurity = true;  // Windows Authentication

                using(EFSampleContext context = new EFSampleContext(builder.ConnectionString))
                {
                    Console.WriteLine("Created database schema from C# classes.");

                    // Create Deomo: Create a User instance and save it to the database
                    User newUser = new User { FirstName = "Anna", LastName = "Shrestinian" };
                    context.Users.Add(newUser);
                    context.SaveChanges();
                    Console.WriteLine("\nCreated User: " + newUser.ToString());

                    // Create Demo: Create a Task instance and save it to the database
                    Task newTask = new Task { Title = "Ship Helsinki", IsComplete = false, DueDate = DateTime.Parse("04-01-2017") };
                    context.Tasks.Add(newTask);
                    context.SaveChanges();
                    Console.WriteLine("\nCreated Task: " + newTask.ToString());

                    // Assosicate Demo: Assign task to user
                    newTask.AssignedTo = newUser;
                    context.SaveChanges();
                    Console.WriteLine("\nAssigned Task: '" + newTask.Title + "' to user '" + newUser.GetFullName() + "'");

                    Console.WriteLine("\nIncompleted tasks assinged to 'Anna'");
                    var query = from t in context.Tasks
                                where t.IsComplete == false &&
                                t.AssignedTo.FirstName.Equals("Anna")
                                select t;
                    foreach(var t in query)
                    {
                        Console.WriteLine(t.ToString());
                    }

                    // Update Demo: Change the 'dueDate' of a task
                    Task taskToUpdate = context.Tasks.First(); // get the first task
                    Console.WriteLine("\nUpdating task: " + taskToUpdate.ToString());
                    taskToUpdate.DueDate = DateTime.Parse("06-30-2016");
                    context.SaveChanges();
                    Console.WriteLine("dueDate changed: " + taskToUpdate.ToString());

                    // Delete Demo: delete all task with a dueDate in 2016
                    Console.WriteLine("\nDeleting all task with a dueDate in 2016");
                    DateTime dueDate2016 = DateTime.Parse("12-31-2016");
                    query = from t in context.Tasks
                            where t.DueDate < dueDate2016
                            select t;
                    foreach(Task t in query)
                    {
                        Console.WriteLine("Deleting task: " + t.ToString());
                        context.Tasks.Remove(t);
                    }
                    context.SaveChanges();

                    // Show tasks after the 'Delete' operation - there should be 0 tasks
                    Console.WriteLine("\nTask after delete:");
                    List<Task> tasksAfterDelete = (from t in context.Tasks select t).ToList<Task>();
                    if(tasksAfterDelete.Count == 0)
                    {
                        Console.WriteLine("[None]");
                    }
                    else
                    {
                        foreach(Task t in query)
                        {
                            Console.WriteLine(t.ToString());
                        }
                    }

                }
            }
            catch(SqlException e)
            {
                Console.WriteLine(e.ToString());
            }

            Console.WriteLine("All done. Press any key to finish ... ");
            Console.ReadKey(true);
        }
    }
}
