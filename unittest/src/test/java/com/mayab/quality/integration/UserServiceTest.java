package com.mayab.quality.integration;

import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import com.mayab.quality.loginunittest.dao.UserMySqlDao;
import com.mayab.quality.loginunittest.service.UserService;
import com.mayab.quality.loginunittest.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.util.List;

import static org.dbunit.Assertion.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserServiceTest extends DBTestCase {

    private UserService service;

    public UserServiceTest() {
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.cj.jdbc.Driver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://localhost:3306/calidad2024");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "root");
    }

    @BeforeEach
    protected void setup() throws Exception {
        service = new UserService(new UserMySqlDao());
        IDatabaseConnection connection = getConnection();
        try {
            // Clear the table
            connection.getConnection().createStatement().execute("TRUNCATE TABLE usuarios;");

            // Set database configuration for MySQL compatibility
            connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());

            // Insert data from create.xml
            DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet());

            System.out.println("Database initialized with data from create.xml.");
        } catch (Exception e) {
            fail("Error in setup: " + e.getMessage());
        } finally {
            connection.close();
        }
    }

    // Load data from create.xml
    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/create.xml"));
    }


    @Test
    public void testCreateUser_HappyPath() throws Exception {
        // Act
        service.createUser("John Doe", "johndoe@example.com", "password123");

        // Assert with DBUnit
        IDataSet databaseDataSet = getConnection().createDataSet();
        ITable actualTable = databaseDataSet.getTable("usuarios");

        // Use create.xml for expected state
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/create.xml"));
        ITable expectedTable = expectedDataSet.getTable("usuarios");

        // Compare actual and expected tables
        Assertion.assertEquals(expectedTable, actualTable);
    }


    @Test
    public void testCreateUser_DuplicateEmail() throws Exception {
        // Arrange: Use the initial state from create.xml
        IDatabaseConnection connection = getConnection();

        // Debug: Verify initial database state
        ITable initialTable = connection.createDataSet().getTable("usuarios");
        System.out.println("Initial database state:");
        for (int i = 0; i < initialTable.getRowCount(); i++) {
            System.out.println("User " + i + ": "
                    + "ID=" + initialTable.getValue(i, "id")
                    + ", Name=" + initialTable.getValue(i, "name")
                    + ", Email=" + initialTable.getValue(i, "email")
                    + ", Password=" + initialTable.getValue(i, "password"));
        }

        // Act: Attempt to create a duplicate user with the same email
        User duplicateUser = service.createUser("Another User", "johndoe@example.com", "password456");

        // Assert: Ensure the duplicate user creation fails and returns null
        assertNull("Duplicate email user creation should return null.", duplicateUser);

        // Assert: Verify database state remains unchanged
        ITable actualTable = connection.createDataSet().getTable("usuarios");
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/create.xml"));
        ITable expectedTable = expectedDataSet.getTable("usuarios");

        // Compare actual and expected tables
        Assertion.assertEquals(expectedTable, actualTable);

        connection.close();
    }



    @Test
    public void testUpdateUser() throws Exception {
        // Arrange: Ensure the database is initialized with create.xml
        IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/create.xml"));
        IDatabaseConnection connection = getConnection();
        DatabaseOperation.CLEAN_INSERT.execute(connection, initialDataSet);

        // Retrieve the existing user from the database (assume the user in create.xml has ID 1)
        User userToUpdate = new User(getName(), getName(), getName());
        userToUpdate.setId(1); // Set the ID of the existing user
        userToUpdate.setName("Updated Name");
        userToUpdate.setPassword("newpassword");

        // Act: Call the updateUser method to update the user
        User updatedUser = service.updateUser(userToUpdate);

        // Assert: Ensure the update was successful
        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("newpassword", updatedUser.getPassword());

        // Query the database to validate the updated state
        ITable actualTable = connection.createQueryTable("result",
                "SELECT * FROM usuarios WHERE id = 1");

        // Assert: Verify the user in the database has the updated fields
        assertEquals(1, actualTable.getRowCount()); // Ensure only one user is found
        assertEquals("Updated Name", actualTable.getValue(0, "name"));
        assertEquals("newpassword", actualTable.getValue(0, "password"));
        assertEquals("johndoe@example.com", actualTable.getValue(0, "email")); // Email should remain unchanged

        connection.close();
    }



    @Test
    public void testDeleteUser() throws Exception {
        // Arrange: Ensure the database is initialized with create.xml
        IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/create.xml"));
        IDatabaseConnection connection = getConnection();
        DatabaseOperation.CLEAN_INSERT.execute(connection, initialDataSet);

        // Act: Delete the user with ID 1
        boolean isDeleted = service.deleteUser(1); // Assuming user ID 1 exists

        // Assert: Ensure the delete operation was successful
        assertTrue("The user should have been deleted successfully.", isDeleted);

        // Query the database to validate the user no longer exists
        QueryDataSet actualTable = new QueryDataSet(connection);
        actualTable.addTable("deletedTMP", "SELECT * FROM usuarios WHERE id = 1");

        // Assert: Verify the user is no longer in the database
        assertEquals("The user should have been deleted.", 0, actualTable.getTable("deletedTMP").getRowCount());

        connection.close();
    }



    @Test
    public void testFindAllUsers() throws Exception {
        // Act: Retrieve all users using the service method
        List<User> allUsers = service.findAllUsers();

        // Assert: Ensure the result is not null and matches the database content
        assertNotNull("The list of users should not be null.", allUsers);

        // Validate that the database state matches the expected dataset
        IDatabaseConnection connection = getConnection();
        ITable actualTable = connection.createQueryTable("usuarios",
                "SELECT * FROM usuarios");

        // Compare the database state with the list returned by the service
        for (int i = 0; i < allUsers.size(); i++) {
            User user = allUsers.get(i);

            // Verify the user fields match the corresponding row in the database
            assertEquals("Name should match", actualTable.getValue(i, "name"), user.getName());
            assertEquals("Email should match", actualTable.getValue(i, "email"), user.getEmail());
            assertEquals("Password should match", actualTable.getValue(i, "password"), user.getPassword());
        }

        connection.close();
    }


    @Test
    public void testFindUserByEmail() throws Exception {
        // Act: Search for a user by email using the service
        String emailToSearch = "johndoe@example.com"; // Email from create.xml
        User user = service.findUserByEmail(emailToSearch);

        
        // Query the database directly to validate the result
        IDatabaseConnection connection = getConnection();
        ITable actualTable = connection.createQueryTable("usuarios",
                "SELECT * FROM usuarios WHERE email = '" + emailToSearch + "'");

        // Assert: Ensure only one user is found
        assertEquals("There should be exactly one user with this email.", 1, actualTable.getRowCount());

        // Validate the returned user fields against the database record
        assertEquals("Name should match", "John Doe", user.getName());
        assertEquals("Email should match", emailToSearch, user.getEmail());
        assertEquals("Password should match", "password123", user.getPassword());

        connection.close();
    }



    @Test
    public void testFindUserById() throws Exception {
        // Act: Search for a user by ID
        int userIdToSearch = 1; // Assuming user ID 1 exists
        User user = service.findUserById(userIdToSearch);

        // Assert: Ensure the user is found
        assertNotNull("User should not be null.", user);

        // Query the database directly to validate the result
        IDatabaseConnection connection = getConnection();
        ITable actualTable = connection.createQueryTable("usuarios",
                "SELECT * FROM usuarios WHERE id = " + userIdToSearch);

        // Assert: Ensure only one user is found
        assertEquals("There should be exactly one user with this ID.", 1, actualTable.getRowCount());

        // Validate the returned user fields against the database record
        assertEquals("Name should match", "John Doe", user.getName());
        assertEquals("Email should match", "johndoe@example.com", user.getEmail());
        assertEquals("Password should match", "password123", user.getPassword());

        connection.close();
    }

}
