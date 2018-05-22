package com.sqlsamples;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App 
{
    String connectionUrl = "jdbc:sqlserver://localhost:1433";
    String userName = "sa";
    String password = "Password123";
    String sampleDatabaseName = "SampleDB";

    // Main entry point
    public static void main( String[] args )
    {
        App app = new App();
        app.runDemo();
    }

    // Helper to run the demo app
	private void runDemo() {
        // Configure Hibernate logging to only log SERVER error
        @SuppressWarnings("unused")
        org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger("org.hibernate");
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);

        System.out.println("** Jave CRUD sample with Hibernate and SQL Server ***\n");
        try {
            // We're creating the Hibernate configuration vid code. 
            // An alternative is to use a 'Hibernate.cfg.xml' file.
            Configuration cfg = createHibernateConfiguration();

            // We're mapping POJO classes to Tables via Hibernate Annotations.
            // An alternative is to use Hibernate mapping xml file.
            cfg.addAnnotatedClass(User.class);
            cfg.addAnnotatedClass(Task.class);

            // Hibernate needs an existing database. Use JDBC to create one for this sample.
            createSampleDatabase();

            // Create the Hibernate SessionFactory and Session.
            // This causes Hibernate to create Tables and Relationships in the database from our Annotated classes.
            try (SessionFactory sessionFactory = cfg.buildSessionFactory();
                Session session = sessionFactory.openSession()) {

                System.out.println("Created database schema form Java classes.\n");
                session.beginTransaction();

                // Create demo: Create a User instance and save it to the database
                User newUser = new User("Anna", "Shrestinian");
                session.save(newUser);
                System.out.println("Created User: " + newUser.toString());

                // Create demo: Create a Task instance and save it to the database
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                Task newTask = new Task("Ship Helsinki", sdf.parse("04-01-2017"));
                session.save(newTask);
                System.out.println("Create Task: " + newTask.toString());

                // Association demo: Assign task to user
                newTask.setUser(newUser);
                session.save(newTask);
                System.out.println("Assinged Task: '" + newTask.getTitle() + "' to user '" + newUser.getFullName() + "'");

                // Read demo: find imcomplete tasks assigned to user 'Anna'
                System.out.println("Incomplete tasks assigned to 'Anna'");
                String hqlQuery = "from Task where isComplete = false and user.firstName = :paramFirstName";
                List<Task> incompleteTasks = session.createQuery(hqlQuery, Task.class)
                                                .setParameter("paramFirstName", "Anna")
                                                .getResultList();
                for(Task theTask : incompleteTasks) {
                    System.out.println(theTask.toString());
                }                           

                // Update demo: change the 'dueDate' of a task
                hqlQuery = "from Task";
                Task taskToUpdate = session.createQuery(hqlQuery, Task.class)
                                        .getResultList()
                                        .get(0); // get the first task
                System.out.println("\nUpdate task: " + taskToUpdate.toString());
                session.save(taskToUpdate);
                System.out.println("dueDate changed: " + taskToUpdate.toString());

                // Delete demo: delete all tasks with a dueDate in 2016
                System.out.println("\nDeleting all tasks with a dueDate in 2016");
                hqlQuery = "from Task where dueDate < :paramDate";
                List<Task> taskToDelete = session.createQuery(hqlQuery, Task.class)
                                            .setParameter("paramDate", sdf.parse("12-31-2016"))
                                            .getResultList();
                for(Task theTask : taskToDelete) {
                    System.out.println("Deleting task: " + theTask.toString());
                    session.delete(theTask);
                }

                // Show tasks after the 'Delete' operation - there should be  0 tasks
                System.out.println("\nTasks after delete:");
                hqlQuery = "from Task";
                List<Task> taskAfterDalete = session.createQuery(hqlQuery, Task.class)
                                                .getResultList();
                if(taskAfterDalete.isEmpty()) {
                    System.out.println("[None]");
                }
                else {
                    for(Task theTask : taskAfterDalete) {
                        System.out.println(theTask.toString());
                    }
                }
            }
            System.out.println("All done.");

        }
        catch(Exception e) {
            System.out.println();
            e.printStackTrace();
        }
	}

    // Hibernate needs an existing database. Use JDBC to create one for this sample
	private void createSampleDatabase() throws SQLException {
        // Load SQL Server JDBC driver and establish connection.
        String url = this.connectionUrl + ";databaseName=master;" + "user=" + this.userName + ";password=" + this.password;
        System.out.println("Connecting to SQL Server ... ");
        try (Connection connection = DriverManager.getConnection(url)) {
            System.out.println("Done.");

            // Create a sample database
            System.out.println("Dropping and creating database: ''" + this.sampleDatabaseName + "' ...");
            String sql = "DROP DATABASE IF EXISTS [" + this.sampleDatabaseName + "]; CREATE DATABASE [" + this.sampleDatabaseName + "]";
            try(Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
                System.out.println("Done.\n");
            }
        }
	}

    // Create Hibernate configuration via code istand of using a 'hibernate.cfg.xml' file.
	private Configuration createHibernateConfiguration() {
        String url = this.connectionUrl + ";databasename=" + this.sampleDatabaseName;
        Configuration cfg = new Configuration()
            .setProperty("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
            .setProperty("hibernate.connection.url", url)
            .setProperty("hibernate.connection.username", this.userName)
            .setProperty("hibernate.connection.password", this.password)
            .setProperty("hibernate.connection.autocommit", "true")
            .setProperty("hibernate.show_sql", "false");

        // Tell Hibernate to use the "SQL Server" dialect when dynamically
        // generate SQL queries.
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        
        // Tell Hibernate to show the generated T-SQL
        cfg.setProperty("hibernate.show_sql", "false");
        
        // This is ok during development, but not recommend in production
        cfg.setProperty("hibernate.hbm2ddl.auto", "update");

		return cfg;
	}
}
