package com.mayab.quality.integration;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;


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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mayab.quality.loginunittest.dao.UserMySqlDao;
import com.mayab.quality.loginunittest.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.mayab.quality.loginunittest.dao.IDAOUser;
import com.mayab.quality.loginunittest.service.UserService;

class UserServiceTest extends DBTestCase{
	
	public UserServiceTest() {
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.cj.jdbc.Driver");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://localhost:3306/calidad2024");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "root");
	}
	
	private IDAOUser dao;
	private UserService service;
	


	@BeforeEach
	protected void setup() throws Exception {
	    dao = new UserMySqlDao();
	    service = new UserService(dao);

	    IDatabaseConnection connection = getConnection();
	    try {
	        DatabaseOperation.TRUNCATE_TABLE.execute(connection, getDataSet());
	        DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet());
	    } catch (Exception e) {
	        fail("Error in setup: " + e.getMessage());
	    } finally {
	        connection.close();
	    }
	}

	protected IDataSet getDataSet() throws Exception {
	    return new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/initDB.xml"));
	}
		
	
	@Test
	public void testCreateUser_HappyPath() {
			
	    // Arrange/Initialize
	    User user = new User("hola", "hola@gmail.com", "pass1234");

	    // Act/Exercise
	    User createdUser = service.createUser("hola", "hola@gmail.com", "pass1234");
	    
	       
	    // Assert
	    assertNotNull(createdUser); 
	    assertEquals("hola", createdUser.getName()); 
	    assertEquals("hola@gmail.com", createdUser.getEmail()); 
	    assertEquals("pass1234", createdUser.getPassword()); 
	}
	
	@Test
	public void testFindAllUsers() {

	    // Arrange/Initialize
	    User user1 = new User("User1", "user1@example.com", "pass1234");
	    User user2 = new User("User2", "user2@example.com", "pass5678");

	    service.createUser(user1.getName(), user1.getEmail(), user1.getPassword());
	    service.createUser(user2.getName(), user2.getEmail(), user2.getPassword());

	    // Act/Exercise
	    List<User> findUsers = service.findAllUsers();

	    // Assert
	    assertNotNull(findUsers); 
	    assertTrue(findUsers.size() >= 2); 
	    
	    // Check if the list contains users 
	    boolean user1Found = findUsers.stream().anyMatch(user -> 
	        user.getName().equals("User1") && 
	        user.getEmail().equals("user1@example.com") &&
	        user.getPassword().equals("pass1234")
	    );

	    boolean user2Found = findUsers.stream().anyMatch(user -> 
	        user.getName().equals("User2") && 
	        user.getEmail().equals("user2@example.com") &&
	        user.getPassword().equals("pass5678")
	    );

	    assertEquals(user1Found, true);
	    assertEquals(user2Found, true);
	}
	
	@Test
	public void testCreateUser_DuplicateEmail() {

	    // Arrange/Initialize
	    service.createUser("User1", "duplicate@example.com", "password123");

	    // Act/Exercise
	    User duplicateUser = service.createUser("User2", "duplicate@example.com", "anotherpass");

	    // Assert
	    assertNull(duplicateUser); 
	}
	
	@Test
	public void testCreateUser_ShortPassword() {

	    // Act/Exercise
	    User userWithShortPassword = service.createUser("User1", "shortpass@example.com", "123");

	    // Assert
	    assertNull(userWithShortPassword); 
	}

	@Test
	public void testCreateUser_LongPassword() {

	    // Act/Exercise
	    User userWithLongPassword = service.createUser("User1", "longpass@example.com", "password123password123password123");

	    // Assert
	    assertNull(userWithLongPassword); 
	}

	@Test
	public void testUpdateUser() {

	    // Arrange/Initialize
	    User user = service.createUser("OriginalName", "updatetest@example.com", "originalpass");
	    user.setName("UpdatedName");
	    user.setPassword("newpass123");

	    // Act/Exercise
	    User updatedUser = service.updateUser(user);

	    // Assert
	    assertNotNull(updatedUser); 
	    assertEquals("UpdatedName", updatedUser.getName()); 
	    assertEquals("newpass123", updatedUser.getPassword()); 

	}
	
	@Test
	public void testDeleteUser() {

	    // Arrange/Initialize
	    User user = service.createUser("DeleteMe", "deleteme@example.com", "deletepass");
	    

	    // Act/Exercise
	    boolean isDeleted = service.deleteUser(user.getId());

	    // AssertS
	    assertTrue(isDeleted);
	    User deletedUser = service.findUserByEmail("deleteme@example.com");
	    assertNull(deletedUser); 
	}

	
	@Test
	public void testFindUserByEmail_HappyPath() {

	    // Arrange/Initialize
	    String email = "findMe@example.com";
	    service.createUser("FindMe", email, "findpass");
	    
	    
	    
	    // Act/Exercise
	    User foundUser = service.findUserByEmail(email);

	    // Assert
	    assertNotNull(foundUser);
	    assertEquals("FindMe", foundUser.getName());
	    assertEquals(email, foundUser.getEmail());
	}

	@Test
	public void testFindUserById_HappyPath() {

	    // Arrange/Initialize
		User user = service.createUser("UserById", "userbyid@example.com", "pass1233");
	    
	    
	    // Check if user creation succeeded and ID is set
	    assertNotNull(user);
	    int userId = user.getId();
	    assertNotNull(userId);
	    
	 
	    // Act/Exercise
	    User foundUser = service.findUserById(userId);
	    
	    // Assert
	    assertNotNull(foundUser);
	    assertEquals("UserById", foundUser.getName());
	    assertEquals("userbyid@example.com", foundUser.getEmail());
	}



}
	 
