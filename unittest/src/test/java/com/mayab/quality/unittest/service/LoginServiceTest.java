package com.mayab.quality.unittest.service;
import com.mayab.quality.loginunittest.dao.IDAOUser;
import com.mayab.quality.loginunittest.model.User;
import com.mayab.quality.loginunittest.service.LoginService;

//import mockito
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;


//Import hamcrest
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.hamcrest.Matchers.isEmptyOrNullString;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

    private User mockUser;
    private IDAOUser mockDao;
    private LoginService loginService;

    @BeforeEach
    void setUp() throws Exception {
    	
        mockUser = mock(User.class);
        mockDao = mock(IDAOUser.class);
        loginService = new LoginService(mockDao);
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        String email = "james_denby";
        String password = "password123";

        // Mock the behavior of user and dao
        when(mockUser.getPassword()).thenReturn(password);
        when(mockDao.findByUserName(email)).thenReturn(mockUser);

        // Act
        boolean loginResult = loginService.login(email, password);
        
        
        // Assert
        assertTrue(loginResult, "Login should be successful with correct credentials.");
    }

    @Test
    void testLoginFailure() {
        // Arrange
        String username = "james_denby";
        String password = "wrongPassword";

        // Mock the behavior of user and dao
        when(mockUser.getPassword()).thenReturn("password123");
        when(mockDao.findByUserName(username)).thenReturn(mockUser);

        // Act
        boolean loginResult = loginService.login(username, password);

        // Assert
        assertFalse(loginResult, "Login should fail with incorrect credentials.");
    }
}
