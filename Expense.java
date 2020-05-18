package main;
import java.sql.DriverManager;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/* Postcondition 1: This class Expense interfaces will create Rent, Groceries and Utilities expenses.
 * Postcondition 2: The rent, groceries and utilities expense values are sent to the SQL table "Account_Info".
*/
interface ExpenseInterface{
	
	void display();
}

abstract class Expense {
	
	String name;
	String category;
	float expenseAmount;
	
	public abstract void display() throws SQLException;
	
}

class Rents extends Expense {
	
	public void Rent () {
		this.name = "Rent";
		this.category = "Housing";
		this.expenseAmount = 0;
	}
	
	
	public void display() throws SQLException{
		Scanner input = new Scanner(System.in);
		System.out.print("Enter your Rent amount:(START TYPING HERE) ");
		float expenseAmount = input.nextFloat();
		
		//System.out.println("Rent is "+ expenseAmount);
		
		this.expenseAmount = expenseAmount;
		
		//SAVES IT TO THE ACCOUNT_INFO SQL DATABASE
		
		String connectionUrl = 
				"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
				+ "database=ExpenseDb;"
			    + "user=MyUser;"
				+ "password=ABC1234!;"
			    + "encrypt=true;"
				+ "trustServerCertificate=true;"
			    + "loginTimeout=30;";

		String update_rent = "UPDATE Account_Info SET Rent = (?)";
		
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				PreparedStatement rentStatement = connection.prepareStatement(update_rent);){
			
				rentStatement.setFloat(1,this.expenseAmount);
				rentStatement.executeUpdate();
				
				}
		
		
	}
}

class Groceries extends Expense {
	

	public void Grocery() {
		this.name = "Groceries";
		this.category = "Food";
		this.expenseAmount = 0;
	}
	
	
	public void display() throws SQLException{
		Scanner input = new Scanner(System.in);
		System.out.print("Enter how much you'd like to spend on Groceries per week:(START TYPING HERE) ");
		float expenseAmount = input.nextFloat()*4;

		this.expenseAmount = expenseAmount;
		
		String connectionUrl = 
				"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
				+ "database=ExpenseDb;"
			    + "user=MyUser;"
				+ "password=ABC1234!;"
			    + "encrypt=true;"
				+ "trustServerCertificate=true;"
			    + "loginTimeout=30;";

		String update_groceries = "UPDATE Account_Info SET Monthly_Groceries = (?)";
		
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				PreparedStatement groceriesStatement = connection.prepareStatement(update_groceries);){
			
				groceriesStatement.setFloat(1,this.expenseAmount);
				groceriesStatement.executeUpdate();
				
				}
		
		
	}
}

class Utilities extends Expense {
	

	public void utility() {
		this.name = "Utility";
		this.category = "Housing";
		this.expenseAmount = 0;
	}
	
	
	public void display() throws SQLException{
		Scanner input = new Scanner(System.in);
		System.out.print("Enter how much you spend a month on utilities:(START TYPING HERE) ");
		float expenseAmount = input.nextFloat();
		
		this.expenseAmount =expenseAmount;
		
		String connectionUrl = 
				"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
				+ "database=ExpenseDb;"
			    + "user=MyUser;"
				+ "password=ABC1234!;"
			    + "encrypt=true;"
				+ "trustServerCertificate=true;"
			    + "loginTimeout=30;";

		String update_utilities = "UPDATE Account_Info SET Utilities = (?)";
		
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				PreparedStatement utilitiesStatement = connection.prepareStatement(update_utilities);){
			
				utilitiesStatement.setFloat(1,this.expenseAmount);
				utilitiesStatement.executeUpdate();
				
		}
		
	}
}
