package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/* Postcondition 1: This class holds methods to  calculate expense summaries by categories as well as calculate variances to budget.
 * Postcondition 2: This class retrieves the most up to date expenses from the SQL table "Account_Info".
*/
public class sumCategories {
	
	public static double grocerySum = 0;
	public static double utilitiesSum = 0;
	public static double disposableSum = 0;
	
	public static double salary = 0;
	public static double rent = 0;
	public static double groceryBudg = 0;
	public static double utilitiesBudg= 0;
	public static double disposableBudg = 0;	

	
	double utilityVariance = 0;
	double dispVariance =0;
	double groceryVariance = 0;
	
	public double getSalary() {
		return salary;
	}
	public double getRent() {
		return rent;
	}
	public double getGroceryBudg() {
		return groceryBudg;
	}
	public double getUtilitiesBudg() {
		return utilitiesBudg;
	}
	public double getDisposableBudg() {
		return disposableBudg;
	}

	
	public static String sumExpenseCategories() throws SQLException {
		
		//Connecting to database "ExpenseDb"
		String connectionUrl = 
				"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
				+ "database=ExpenseDb;"
			    + "user=MyUser;"
				+ "password=ABC1234!;"
			    + "encrypt=true;"
				+ "trustServerCertificate=true;"
			    + "loginTimeout=30;";

		//below joins the expense transaction and category to sum by the 3 different categories of expenses
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				Statement statement1 = connection.createStatement();
				Statement statement2 = connection.createStatement();
				Statement statement3 = connection.createStatement();) {
			
	
			String sql = 
					"select sum(expense_amount) from expense_transaction join expense_category on " + 
					"expense_transaction.expense_id = expense_category.expense_id " + 
					"where expense_category.expense_category= 'Grocery';";
			
	    	ResultSet groceryQuery = statement1.executeQuery(sql);
	    	
	    	while (groceryQuery.next()) {
	    		grocerySum = groceryQuery.getDouble(1);
	    	}
	    	
			sql = 
					"select sum(expense_amount) from expense_transaction join expense_category on " + 
					"expense_transaction.expense_id = expense_category.expense_id " + 
					"where expense_category.expense_category= 'Utilities';";
				
		    	ResultSet utilitiesQuery = statement2.executeQuery(sql);
		    	
		    	while (utilitiesQuery.next()) {
		    		utilitiesSum = utilitiesQuery.getDouble(1);  	
		    	}
		    	
			sql = 
					"select sum(expense_amount) from expense_transaction join expense_category on " + 
					"expense_transaction.expense_id = expense_category.expense_id " + 
					"where expense_category.expense_category= 'Disposable';";
						
				 ResultSet dispQuery = statement3.executeQuery(sql);
				    	
				    while (dispQuery.next()) {
				    	disposableSum = dispQuery.getDouble(1); 
	    		
				    }
			
			}
		
		//Handles a scenario where the user hasn't uploaded any expense data but chose menu item 2 anyways
		if (grocerySum==0 && utilitiesSum==0 && disposableSum==0) {
			return "Oh no! It looks like you have not uploaded any real expenses yet. Please make sure to return to the main menu and select 1 to upload an offline file.";
		}else {
		
		return "Total expenses for groceries last month were "+ String.format("%.2f", grocerySum) + 
			   "\nTotal expenses for utilities last month were " + String.format("%.2f", utilitiesSum) + 
			   "\nTotal expenses for discretionary spending last month were " + String.format("%.2f",disposableSum) + "\n \n";
		}
	}

	

	//CHECK ACTUALVS vs. BUDGET for GROCERY
	public static void prepComparisons() throws SQLException {
		
		
		
		//Connecting to database "ExpenseDb"
		String connectionUrl = 
				"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
				+ "database=ExpenseDb;"
			    + "user=MyUser;"
				+ "password=ABC1234!;"
			    + "encrypt=true;"
				+ "trustServerCertificate=true;"
			    + "loginTimeout=30;";

		//below joins the expense transaction and category to sum by the 3 different categories of expenses
		try (Connection connection = DriverManager.getConnection(connectionUrl);
				Statement statement1 = connection.createStatement();
				Statement statement2 = connection.createStatement();
				Statement statement3 = connection.createStatement();
				Statement statement4 = connection.createStatement();
				Statement statement5 = connection.createStatement();
				) {
			
			String sql = 
					"select monthly_salary from account_info; ";
			
	    	ResultSet salaryQuery = statement1.executeQuery(sql);
	    	
	    	while (salaryQuery.next()) {
	    		salary = salaryQuery.getDouble(1);
	    	}
			
			sql = 
					"select rent from account_info; ";
			
	    	ResultSet rentQuery = statement2.executeQuery(sql);
	    	
	    	while (rentQuery.next()) {
	    		rent = rentQuery.getDouble(1);
	    	}			
			
			
			sql = 
					"select monthly_groceries from account_info; ";
			
	    	ResultSet groceryQuery = statement3.executeQuery(sql);
	    	
	    	while (groceryQuery.next()) {
	    		groceryBudg = groceryQuery.getDouble(1);
	    	}
	    	
			sql = 
					"select utilities from account_info; ";
				
		    	ResultSet utilitiesQuery = statement4.executeQuery(sql);
		    	
		    	while (utilitiesQuery.next()) {
		    		utilitiesBudg = utilitiesQuery.getDouble(1);  	
		    	}
		    	
			sql = 
					"select discretionary_budget from account_info; ";
						
				 ResultSet dispQuery = statement5.executeQuery(sql);
				    	
				    while (dispQuery.next()) {
				    	disposableBudg = dispQuery.getDouble(1); 
				    }
		}
		
	}
	
	public static String groceryComparison() {
		
		double groceryVariance = Math.round(grocerySum - groceryBudg);
		
		if(groceryVariance > 0) {
			return("Great Job! You were under budget for groceries last month by:"+String.format("%.2f", groceryVariance));
		}else {
			return("Be careful! You were over budget for groceries last month by:"+String.format("%.2f", groceryVariance));
		}
		
		
	}
	
	//CHECK ACTUALVS vs. BUDGET for UTILITY	
	public static String utilityComparison() {
		
		double utilityVariance = Math.round(utilitiesSum - utilitiesBudg);

		if(utilityVariance > 0) {
			return("Great Job! You were under budget for utilities last month by:"+String.format("%.2f", utilityVariance));
		}else {
			return("Be careful! You were over budget for utilities last month by:"+String.format("%.2f", utilityVariance));
		}		
	}
	
	//CHECK ACTUALVS vs. BUDGET for DISCRETIONARY BUDGET
	public static String discBudgetComparison() {
		
		double dispVariance =Math.round(disposableSum - disposableBudg);

		if(dispVariance > 0) {
			return("Great Job! You were under budget for discretionary spending last month by:"+String.format("%.2f", dispVariance));
		}else {
			return("Be careful! You were over budget for discretionary spending last month by:"+String.format("%.2f", dispVariance));
		}		
	
	}
	
}

