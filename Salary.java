package main;
import java.sql.DriverManager;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/* Postcondition 1: This class Salary is used to prompt user with their post-tax biweekly paycheck and calculate total monthly salary.
 * Postcondition 2: The monthly salary value is sent to the SQL table "Account_Info".
*/
public class Salary {

	
public int paycheck;
public float monthlySalary;


public float getMonthlySalary() {
	return monthlySalary;
}

public void MonthlySalary() throws SQLException{
	
	this.paycheck = 0;
	this.monthlySalary = 0;
	
	
	Scanner input = new Scanner(System.in);
	System.out.print("Enter the amount on your biweekly paycheck (post-tax):(START TYPING HERE) ");
	this.paycheck = input.nextInt();
	this.monthlySalary = this.paycheck*26/12;
	
	// BELOW IS CODE TO SAVE THE SALARY AMOUNT TO A SQL SERVER
	
	String connectionUrl = 
			"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
			+ "database=ExpenseDb;"
		    + "user=MyUser;"
			+ "password=ABC1234!;"
		    + "encrypt=true;"
			+ "trustServerCertificate=true;"
		    + "loginTimeout=30;";

	String update_salary = "UPDATE Account_Info SET Monthly_Salary = (?)";
	
	try (Connection connection = DriverManager.getConnection(connectionUrl);
			PreparedStatement salaryStatement = connection.prepareStatement(update_salary);){
		
			salaryStatement.setFloat(1,this.monthlySalary);
			salaryStatement.executeUpdate();
			
			}
	
	System.out.println("Monthly post-tax income is:"+ String.format("%.2f", this.monthlySalary));
	
	}
	
}
