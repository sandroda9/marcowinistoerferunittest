package com.mayab.quality.unittest.service;

import com.mayab.quality.loginunittest.dao.IDAOUser;
import com.mayab.quality.loginunittest.model.User;
import com.mayab.quality.loginunittest.service.UserService;

// import mockito
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

// Import hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.hamcrest.Matchers.isEmptyOrNullString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserServiceTest {
	
	private UserService service;
	private IDAOUser dao;
	private HashMap<Integer, User> db;
	

	@BeforeEach
	public void setUp() throws Exception {
		dao = mock(IDAOUser.class);
		service = new UserService(dao);
		db = new HashMap<Integer, User> ();
		
	}

	@Test
	public void testCreateUser_HappyPath() {
		
	    // Arrange/Initialize
	    String name = "John Doe";
	    String email = "john@example.com";
	    String password = "securePass123";
	    User mockUser = null;

	    // Fake behavior
	    when(dao.findUserByEmail(email)).thenReturn(mockUser);
	    when(dao.save(any(User.class))).thenReturn(1);

	    // Act/Exercise
	    User createdUser = service.createUser(name, email, password);

	    // Assert
	    assertNotNull(createdUser);
	    assertEquals(name, createdUser.getName());
	    assertEquals(email, createdUser.getEmail());
	    assertEquals(1, createdUser.getId());
	}
	
	@Test
	public void testCreateUser_DuplicatedEmail() {
		
	    // Arrange/Initialize
	    String name = "John Doe";
	    String email = "john@example.com";
	    String password = "securePass123";

	    User mockUser = new User(name, email, password);

	    // Fake behavior
	    when(dao.findUserByEmail(email)).thenReturn(mockUser);

	    // Act/Exercise
	    User createdUser = service.createUser(name, email, password);

	    // Assert
	    assertThat(createdUser, is(nullValue()));
	}


	@Test
	public void testDeleteUser() {
		
	    // Arrange/Initialize
	    int userId = 1;

	    // Fake behavior
	    when(dao.deleteById(userId)).thenReturn(true);

	    // Act/Exercise
	    boolean isDeleted = service.deleteUser(userId);

	    // Assert
	    assertThat(isDeleted, is(true));
	}
	
	
	@Test
	public void whenPasswordIsShortTest() {

		// Arrange/Initialize
		String shortPassword = "123";
		String name = "user1";
		String email = "user1@gmail.com";
		User user = null;

		// Fake behavior
		when(dao.findUserByEmail(anyString())).thenReturn(user);
		when(dao.save(any(User.class))).thenReturn(1);

		// Act/Exercise
		user = service.createUser(name, email, shortPassword);

		// Assert
		assertThat(user, is(nullValue())); 
	}
	
	@Test 
    public void whenPasswordIsLongTest() {

        // Arrange/Initialize
        String longPassword = "123456789";
        String name = "user1";
        String email = "user1@gmail.com";
        User user = null;

        // Fake behavior
        when(dao.findUserByEmail(anyString())).thenReturn(user);
        when(dao.save(any(User.class))).thenReturn(1);

        // Act/Exercise
        user = service.createUser(name, email, longPassword);

        // Assert
        assertThat(user, is(not(nullValue()))); 
    }
	
	@Test
	public void testFindUserByEmail_HappyPath() {
		
	    // Arrange/Initialize
	    String email = "jane@example.com";
	    User mockUser = new User("Jane Doe", email, "password123");

	    // Fake behavior
	    when(dao.findUserByEmail(email)).thenReturn(mockUser);

	    // Act/Exercise
	    User foundUser = service.findUserByEmail(email);

	    // Assert
	    assertNotNull(foundUser);
	    assertEquals("Jane Doe", foundUser.getName());
	}
	
	@Test
	public void testFindUserByEmail_NotFound() {
		
	    // Arrange/Initialize
	    String email = "unknown@example.com";

	    // Fake behavior
	    when(dao.findUserByEmail(email)).thenReturn(null);

	    // Act/Exercise
	    User foundUser = service.findUserByEmail(email);

	    // Assert
	    assertThat(foundUser, is(nullValue()));
	}
	
	@Test
	public void testFindAllUsers() {
		
	    // Arrange/Initialize
	    List<User> mockUsers = Arrays.asList(
	        new User("User1", "user1@example.com", "pass1234"),
	        new User("User2", "user2@example.com", "pass5678")
	    );

	    // Fake behavior
	    when(dao.findAll()).thenReturn(mockUsers);

	    // Act/Exercise
	    List<User> users = service.findAllUsers();

	    // Assert
	    assertEquals(2, users.size());
	    assertEquals("User1", users.get(0).getName());
	    assertEquals("User2", users.get(1).getName());
	}

		
	@Test
	public void testUpdateUser() {
		
	    // Arrange/Initialize
	    User oldUser = new User("Old User", "oldemail", "oldPassword");
	    oldUser.setId(1);
	    
	    db.put(1, oldUser);
	    
	    User newUser = new User("New User", "oldemail", "newpassword");
	    newUser.setId(1);

	    // Fake behavior
	    when(dao.findById(1)).thenReturn(oldUser);

	    when(dao.updateUser(any(User.class))).thenAnswer(new Answer<User>() {
	        public User answer(InvocationOnMock invocation) throws Throwable {
	        	
	            User arg = (User) invocation.getArguments()[0];
	            db.replace(arg.getId(), arg); 
	            return db.get(arg.getId());  
	        }
	    });

	    // Act/Exercise
	    User result = service.updateUser(newUser);

	    // Assert
	    assertThat(result.getName(), is("New User"));
	    assertThat(result.getPassword(), is("newpassword"));
	}


	
}
