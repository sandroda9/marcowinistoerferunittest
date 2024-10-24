package com.mayab.quality.loginunittest.model;

public class User {
	private int id;
	private String name;
	private String email;
	private String password;
	private boolean isLogged;
	
		
	public User(String name2, String email2, String password2) {
		// TODO Auto-generated constructor stub
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isLogged() {
		return isLogged;
	}
	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setToken(Object object) {
		// TODO Auto-generated method stub
		
	}

	public Object getToken() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
