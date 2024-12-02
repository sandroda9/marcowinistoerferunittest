package com.mayab.quality.loginunittest.service;

import java.util.*;

import com.mayab.quality.loginunittest.dao.IDAOUser;
import com.mayab.quality.loginunittest.model.User;

public class UserService {
	
	private IDAOUser dao;
	
	public UserService(IDAOUser dao) {
		this.dao = dao;
	}
	
	public User createUser(String name, String email, String password) {
	    // Check password length
	    if (password.length() < 8 || password.length() > 16) {
	        return null; // Invalid password length
	    }

	    // Check for existing user with the same email
	    User existingUser = dao.findUserByEmail(email);
	    if (existingUser != null) {
	        return null; // Email already exists, return null
	    }

	    // Create a new user
	    User newUser = new User(name, email, password);
	    int id = dao.save(newUser);

	    // Check if the user was saved successfully
	    if (id > 0) {
	        newUser.setId(id);
	        return newUser; // Return the created user
	    }

	    return null; // Return null if save operation failed
	}


	
	public List<User> findAllUsers(){
		List<User> users = new ArrayList<User>();
		users = dao.findAll();
	
		return users;
	}

	public User findUserByEmail(String email) {
		
		return dao.findUserByEmail(email);
	}

	public User findUserById(int id) {
		
		return dao.findById(id);
	}
    
    public User updateUser(User user) {
    	User userOld = dao.findById(user.getId());
    	System.out.println(user.getName());
    	userOld.setName(user.getName());
    	userOld.setPassword(user.getPassword());
    	return dao.updateUser(user);
    }

    

    public boolean deleteUser(int id) {
    	return dao.deleteById(id);
    }
    
   
}