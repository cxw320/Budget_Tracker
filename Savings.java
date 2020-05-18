package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/* Postcondition 1: This class Savings is used to prompt user for a saving % goal and calculate what this means for monthly cash savings. 
 * Postcondition 2: The saving percent value is sent to the SQL table "Account_Info".
*/

public class Savings {

	int savingPerc;
	int savingCash;
	int disposableInc;

	public void calcSavings() throws SQLException{
				
		Scanner input = new Scanner(System.in);
		System.out.print("Enter a % of disposable income you want to save (in whole number):(START TYPING HERE) ");
		this.savingPerc = input.nextInt();

		this.savingPerc = savingPerc;
		
		String connectionUrl = 
				"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
				+ "database=ExpenseDb;"
			    + "user=MyUser;"
				+ "password=ABC1234!;"
			    + "encrypt=true;"
				+ "trustServerCertificate=true;"
			    + "loginTimeout=30;";

		String update_saving_perc = "UPDATE Account_Info SET Saving_Percent = (?)";
		
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				PreparedStatement savingsStatement = connection.prepareStatement(update_saving_perc);){
			
				savingsStatement.setInt(1,this.savingPerc);
				savingsStatement.executeUpdate();
				
		}
		
		
			
		}

	public int getSavingCash() {
		return savingCash;
	}
	
}
