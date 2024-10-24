package com.mayab.quality.loginunittest.service;

import com.mayab.quality.loginunittest.dao.IDAOUser;
import com.mayab.quality.loginunittest.model.User;

// import mockito
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.*;

// Import hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
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

	@Test // Add the @Test annotation so JUnit 5 recognizes this as a test
	public void whenPasswordIsShortTest() {

		// Arrange/Initialize
		String shortPassword = "123";
		String name = "user1";
		String email = "user1@gmail.com";
		User user = null;

		// Fake behavior for findUserByEmail and save methods
		when(dao.findUserByEmail(anyString())).thenReturn(user);
		when(dao.save(any(User.class))).thenReturn(1);

		// Act/Exercise
		user = service.createUser(name, email, shortPassword);

		// Assert
		assertThat(user, is(nullValue())); // Expecting user to be null if the password is too short
	}
	
	@Test // Test for long password
    public void whenPasswordIsLongTest() {

        // Arrange/Initialize
        String longPassword = "123456789";
        String name = "user1";
        String email = "user1@gmail.com";
        User user = null;

        // Fake behavior for findUserByEmail and save methods
        when(dao.findUserByEmail(anyString())).thenReturn(user);
        when(dao.save(any(User.class))).thenReturn(1);

        // Act/Exercise
        user = service.createUser(name, email, longPassword);

        // Assert
        assertThat(user, is(not(nullValue()))); // Expecting user to be created successfully
    }
	
	@Test //test for email verification
	public void whenEmailIsInvalidThenUserIsNull() {
	    // Arrange/Initialize
	    String invalidEmail = "user1-invalid"; // Invalid email format
	    String password = "123456";
	    String name = "user1";
	    User user = null;

	    // Fake behavior for findUserByEmail and save methods
	    when(dao.findUserByEmail(anyString())).thenReturn(user); // No user found
	    when(dao.save(any(User.class))).thenReturn(0); // Simulating a failure to save

	    // Act/Exercise
	    user = service.createUser(name, invalidEmail, password);

	    // Assert
	    assertThat(user, is(nullValue())); // Expecting user to be null if the email is invalid
	}

		
	@Test
    public void testUpdateUser() {

        User oldUser = new User("Old User","oldemail","oldPassword");
        oldUser.setId(1);
        db.put(1, oldUser);
        User newUser = new User("New User", "oldemail", "newpassword");
        newUser.setId(1);

        when(dao.findById(1)).thenReturn(oldUser);

        when(dao.updateUser(any(User.class))).thenAnswer(new Answer<User>() {
            public User answer(InvocationOnMock invocation) throws Throwable {
                User arg = (User) invocation.getArguments()[0];
                db.replace(arg.getId(), arg);
                return db.get(arg.getId());
            }
        });


        User result = service.updateUser(newUser);
        assertThat(result.getName(), is("New User"));
        assertThat(result.getPassword(), is("newpassword"));
        
	}

	//comment


}
