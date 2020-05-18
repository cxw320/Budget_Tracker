package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class LoadExpenses {
	
	public void loadFile() {
	
	System.out.println("Enter the file path where your offline expense file is saved:"+
					   "\n(For now, copy this path into the console: 'src/main/ExpenseRaw'"+
			           "\n(COPY PATH HERE):");
					
	Scanner input = new Scanner(System.in); 
	
	String path = input.nextLine();
	
	//file path where to find our offline file
//	public static String filePath = "C:\\Users\\n0275693\\eclipse-workspace\\PersonalBudgetTracker_v6\\src\\main\\ExpenseRaw";	
	
	String filePath = "src/main/ExpenseRaw";
	

	//This Array List will hold all rows of expense records from the CSV
	ArrayList<Transaction> FullCSV = new ArrayList<Transaction>();

	//this method is part of the Thread class and will execute the code block below as a thread execution

	
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileReader(path));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} //reads the filePath
		
		String line; //string to store the CSV line
		
		while (scanner.hasNextLine()) {
			
			line = scanner.nextLine(); // get the line
			
			String[] results = line.trim().split(","); //split it on comma
			
			//Each of the strings from the row are used to create a CSVItem object
			
			double amount = Double.valueOf(results[0]);
					
			String vendor = results[1];
			
			String category = results[2];

			//This list will hold each of the CSVItems in each row

			Transaction trans = new Transaction(amount, vendor, category);
			
			//The CSVRow list gets stored to an ArrayList called FullCSV. This will be used later to iterate through rows
			FullCSV.add(trans);		
			}	

		//Connecting to database "ExpenseDb"
		String connectionUrl = 
				"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
				+ "database=ExpenseDb;"
			    + "user=MyUser;"
				+ "password=ABC1234!;"
			    + "encrypt=true;"
				+ "trustServerCertificate=true;"
			    + "loginTimeout=30;";
	
		//Inserting values for each expense transaction's vendor, expense amount and expense category
		String expense_sql = "INSERT INTO Expense_Transaction (Expense_id, Expense_vendor, Expense_amount) Values (?,?,?)";
		String category_sql = "INSERT INTO Expense_Category(Expense_id, Expense_category) Values (?,?)";
		
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				PreparedStatement expenseStatement = connection.prepareStatement(expense_sql);
				PreparedStatement categoryStatement = connection.prepareStatement(category_sql);) {
		
		//iterates through Array Object "FullCSV" to pass parameters for expense vendor, amount and category
		for (int i = 0; i < FullCSV.size(); i++) {	
			
			expenseStatement.setInt(1,i+1); expenseStatement.setString(2,(String) FullCSV.get(i).getVendor()); expenseStatement.setDouble(3, (Double) FullCSV.get(i).getAmount());
			expenseStatement.executeUpdate();
			
			categoryStatement.setInt(1,i+1); categoryStatement.setString(2,(String) FullCSV.get(i).getCategory());
			categoryStatement.executeUpdate();
		}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
		
}
