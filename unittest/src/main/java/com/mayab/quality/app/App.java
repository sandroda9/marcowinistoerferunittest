package com.mayab.quality.app;
import java.sql.*;

public class App {
	
	
	 class JDBCTest{
	     public static void main(String args[]) throws SQLException {
	         Connection con = null;
	         try{
	             Class.forName("com.mysql.cj.jdbc.Driver");
	             String dbURL = "jdbc:mysql://localhost:3306/calidad2024";
	             System.out.println("jdbcurl=" + dbURL);
	             String strUserID = "root";
	             String strPassword = "root";
	             con=DriverManager.getConnection(dbURL,strUserID,strPassword);
	             System.out.println("Connected to the database.");
	             Statement stmt=con.createStatement();
	             System.out.println("Executing query");
	             ResultSet rs=stmt.executeQuery("SELECT 1 FROM DUAL");
	             while(rs.next())
	                 System.out.println(rs.getInt("1"));
	             con.close();
	         }catch(Exception e){ System.out.println(e);}
	         finally {
	             con.close();
	         }
	 }
	 }

}
