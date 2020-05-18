package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class createTables extends Thread {
	
	/* Postcondition 1: Program will create SQL tables for Account information, expense transactions and expense categories.
	*/	
	public void run() {
	
	String connectionUrl = 
			"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
			+ "database=ExpenseDb;"
		    + "user=MyUser;"
			+ "password=ABC1234!;"
		    + "encrypt=true;"
			+ "trustServerCertificate=true;"
		    + "loginTimeout=30;";
	
	ResultSet resultSet = null;

	//Establishes a session with the above database connection
	//Creates 2 .createStatement object and string of embedded SQL 
	//uses .executeUpdate method to manipulate data, this is for non-query SQL, does not return values
	try (Connection connection = DriverManager.getConnection(connectionUrl);
			Statement statement1 = connection.createStatement();                    
			Statement statement2 = connection.createStatement();
			Statement statement3 = connection.createStatement();){
		
		String sql = 
	            "CREATE TABLE Account_info(" +
				"Account_id INTEGER PRIMARY KEY," +
				"Monthly_Salary FLOAT(2),"+
				"Rent FLOAT(2),"+
				"Utilities FLOAT(2),"+
				"Monthly_Groceries FLOAT(2),"+
				"Saving_percent INTEGER,"+
				"Discretionary_Budget FLOAT(2))";       						
			statement1.executeUpdate(sql);
		
		sql =
	
			"CREATE TABLE Expense_Transaction (" +
			"Expense_id INTEGER PRIMARY KEY," +
			"Expense_vendor VARCHAR(64),"+
			"Expense_amount INTEGER)";
		statement2.executeUpdate(sql);                          
		
		sql = 
            "CREATE TABLE Expense_Category(" +
			"Expense_id INTEGER PRIMARY KEY," +
			"Expense_category VARCHAR(64))";       						
		statement3.executeUpdate(sql);


	}catch (SQLException e) {
		e.printStackTrace();      
	} 
	}
}
