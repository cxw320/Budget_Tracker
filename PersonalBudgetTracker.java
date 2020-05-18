package main;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.math.RoundingMode;
import java.text.DecimalFormat;


public abstract class PersonalBudgetTracker {


	public static void main(String[] args) throws SQLException, InterruptedException, ExecutionException {
		
	/* Postcondition 1: SQL tables will be created for the first time. 
	 * Known Issue: If this main method is run multiple times, there will be a SQL error for creating another table named the same.
	 * 				In a real-world implementation of this app, I would move the create table code outside the main method in a different program.
	 * 				But for the purposes of the TA instructor being able to run this code, I included the create statements inside here (so he can grade my HW :D)
	 * 
	 * Postcondition 2:The create table thread will join another thread that checks if there is an existing Account record.
	 * 				   This "checkAccountExists" thread's purpose is to see if the user has inputs from a prior session.
	 * 				   In this case, there will be no prior existing inputs, and the program will force the user to provide some inputs (salary, rent, etc.)
	 */
		createTables createTables = new createTables();
		FutureTask<Integer> future = new FutureTask<Integer>(new checkAccountInfo());
		Thread checkAccountExists = new Thread(future);
		createTables.run();
		createTables.join();
		checkAccountExists.start();
		int accountCheck = future.get();
		
		
	/*PostCondition 3: This boolean will be turned "false" when the program wants to exit the while loop. 
	 * 				   The while loop is used to give the user an option to return to the "Main Menu"
	 */
		boolean inputError = true;
		
		while(inputError==true) {
			
			Scanner input = new Scanner(System.in);
			System.out.print("Hello! Welcome to the Personal Budget Tracker 2020!");

			
			if (accountCheck==0) {
				System.out.println("\n\nIt looks like you haven't created an Account Yet. Would you like to create one?"+
								   "\n Yes (Type 1)"+
								   "\n No  (Type 2)"+
								   "\n START TYPING HERE:");
				int select1 = input.nextInt();
				
				if (select1==1) {
					System.out.println("Great! Let's get started."+
									   "\nPlease answer the below questions to help us calculate your monthly Discretionary Budget."+
									   "\n----------------------------------------------------------------------------------\n");
					
				//Once the program identifies no historical account information for the user, the below SQL inserts a new blank row into table "Account_Info for this current user"
					String connectionUrl = 
							"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
							+ "database=ExpenseDb;"
						    + "user=MyUser;"
							+ "password=ABC1234!;"
						    + "encrypt=true;"
							+ "trustServerCertificate=true;"
						    + "loginTimeout=30;";

					String insert_account = "INSERT INTO Account_info (account_id, monthly_salary,rent, utilities, monthly_groceries,saving_percent,discretionary_budget) Values (?,?,?,?,?,?,?)";
					
					try (Connection connection = DriverManager.getConnection(connectionUrl);
							PreparedStatement insertStatement = connection.prepareStatement(insert_account);){

						insertStatement.setInt(1,1);insertStatement.setFloat(2,0); insertStatement.setFloat(3, 0);insertStatement.setFloat(4, 0);insertStatement.setFloat(5,0);
						insertStatement.setInt(6, 0);insertStatement.setFloat(7, 0);
						insertStatement.executeUpdate();
					}
					
			/*PostCondition 4: User will be prompted to provide salary, expenses and saving % goal.
			 * 				   The program will update the Account_info SQL table with these inputs and store for later use.
			 */
			
					Salary s = new Salary();
					s.MonthlySalary();

					//each of these objects below are extensions of Abstract class "Expense"
					Expense r = new Rents();
					r.display();
					
					Expense u = new Utilities();
					u.display();
					
					Expense g = new Groceries();
					g.display();
					
					//calculates budget for disposable income
					float disposableInc = (s.monthlySalary - (r.expenseAmount + g.expenseAmount + u.expenseAmount));
					
					Savings sav = new Savings();
			        
					//calculates a savings amount based on a goal % input by user
					sav.calcSavings();
					float budget = disposableInc - (disposableInc * sav.savingPerc / 100) ;
					
				
					String discBudg = "You have a discretionary budget of " + String.format("%.2f",budget) + " to spend on shopping, eating-out and entertainment!"
							+ "\nYou may now continue to the main menu."
							+ "\n";
					
					//SQL code to update discretionary budget in the SQL table
					String update_disc_budget = "UPDATE Account_Info SET Discretionary_budget = (?)";
					
					try (Connection connection = DriverManager.getConnection(connectionUrl);
							PreparedStatement updateDiscBudget = connection.prepareStatement(update_disc_budget);){
						
							updateDiscBudget.setFloat(1,budget);
							updateDiscBudget.executeUpdate();
							
							}
					
					System.out.print(discBudg);
					//Escape the while Loop
					inputError=false;
				} else {
					//Allows user the option not to provide account info and this exits the program
					inputError=false;
				}

				
			}else{
				//This code runs if the user already has account information. The program will take the user directly to Main Menu
				inputError=false;
			}
		}
		
/*PostCondition 5: The below while loop encloses the Main Menu options.
 * 				   When Users are finished with various tasks, they have the option to return to the Main Menu or exit the application.
 * 				   InputError2 boolean is used to exit the while loop or return the user back.
 */		
		boolean inputError2 = true;
		
		while(inputError2==true) {
			
			Scanner input2 = new Scanner(System.in);
			System.out.println("\n----------------------------MAIN MENU------------------------------"+
							   "\nWelcome to the main menu. How many I help you today?"+
							   "\n\n1) I would like to upload an offline expense file into my expense tracker.(Type 1)" + 
							   "\n2) I would like to compare my budgets to my actual expenses.(Type 2)" + 
							   "\n3) I would like to change my Account information.(Type 3)" + 
							   "\n4) I am ready to exit.(Type 4)");

		try {
			
			int selection2 = input2.nextInt();
			
/*PostCondition 6: If the user selects 1, the program will ask the user for a file path.
 * 				   The program reads that path, finds the text file to upload, parses the strings, stores them as attributes of an object "Transaction".
 * 				   These objects are then inserted into the SQL table "Expense_Transactions".
 * 				   Their categories are stored in a separate SQL table "Expense_Categories".
 */					
	
			if(selection2==1) {
				
				LoadExpenses loadExpenseFile = new LoadExpenses();
				loadExpenseFile.loadFile();
				
				
				System.out.println("Your file is now loading. Would you like to return to the main menu?"+
							       "\n1) Yes (Type 1)"+
								   "\n2) No (Type 2)");
				Scanner input3 = new Scanner(System.in);
				int selection3 = input3.nextInt();
				if (selection3==1) {
					inputError2=true;
				}else {
					inputError2=false;
				}
				
/*PostCondition 7: If the user selects 2, the program will return a summary of their expenses by category.
 * 				   If the summaries are all 0, its possible the user did not upload expenses yet. The program will request the user to return to main menu to do so.
 *PostCondition 8: The program will also provide a variance summary of actual expenses to proposed budgets.
 */
				
			}else if (selection2==2) {
				
				sumCategories compareBudget = new sumCategories();
				System.out.println(compareBudget.sumExpenseCategories());
				compareBudget.prepComparisons();
				
				System.out.println(compareBudget.groceryComparison());
				System.out.println(compareBudget.utilityComparison());
				System.out.println(compareBudget.discBudgetComparison());

/*PostCondition 9: The program asks if the user would like to export the summary into a file.
 * 				   If the user selects yes, the program writes the objects to the "Expense_Report" text file.
 *                 If the user selects no, the program gives the option to return to main menu or exit the program.
 */
									
				System.out.println("\nWould you like to export this report into an offline file?"+
								   "\n1) Yes (Type 1)"+
								   "\n2) No (Type 2)");
				Scanner input4 = new Scanner(System.in);
				int selection4 = input4.nextInt();
				
				if(selection4==1) {
					
					exportFile f = new exportFile();
					f.OpenFile();
					f.addReport(compareBudget.sumExpenseCategories(), compareBudget.groceryComparison(),compareBudget.utilityComparison(),compareBudget.discBudgetComparison());
			    	f.closeFile();
				
					System.out.println("Your report is now exported here (/src/main/Expense_Report). Would you like to return to the main menu?"+
						       "\n1) Yes (Type 1)"+
							   "\n2) No (Type 2)");
					Scanner input5 = new Scanner(System.in);
					int selection5 = input5.nextInt();
					if (selection5==1) {
						inputError2=true;
					}else {
						inputError2=false;
					}
				} else {
					System.out.println("Would you like to return to the main menu?"+
						       "\n1) Yes (Type 1)"+
							   "\n2) No (Type 2)");
					Scanner input6 = new Scanner(System.in);
					int selection6 = input6.nextInt();
					if (selection6==1) {
						inputError2=true;
					}else {
						inputError2=false;
					}
				}

				
				
/*PostCondition 10: If the user selects 3, the program will print out the account inputs the user provided.
 *PostCondition 11: The program will give the user the option to select an item to update. 
 *		            The user can input a new value and that value will be updated in the SQL tables for that account.
 *PostCondition 12: The program uses another while loop to allow the user to return back and update as many of the items as they want.
 *					The InputError3 will be set to false when the user wants to exit this menu. 
 *PostCondition 13: Upon exiting this menu, the user has the option to return to the main menu or exit the application.
 */				
			}else if (selection2==3) {
				
				boolean inputError3 = true;
				
				while(inputError3==true) {
					System.out.println("Please select which item below you would like to update:"+
							   "\n----------------------------------------------------------------------------------\n");
					sumCategories account = new sumCategories();
					account.prepComparisons();
					System.out.println("Monthly Salary: "+String.format("%.2f", account.getSalary())+" (Type 1 to select)"+
					                   "\nMonthly Rent: "+String.format("%.2f", account.getRent())+" (Type 2 to select)"+
							           "\nMonthly Utilities: "+String.format("%.2f", account.getUtilitiesBudg())+" (Type 3 to select)"+
					                   "\nMonthly Groceries: "+ String.format("%.2f",account.getGroceryBudg())+" (Type 4 to select)"+
							           "\nMonthly Discretionary Income Budget: "+String.format("%.2f", account.getDisposableBudg())+" (Type 5 to select)");

					
					Scanner input8 = new Scanner(System.in);
					int selection8 = input8.nextInt();
	
					
	//This code will update the Salary Input				
					if(selection8==1) {
						Salary updateSal = new Salary();
						updateSal.MonthlySalary();
						account.prepComparisons();
						System.out.println("\nYour updated account info is below:"+"\n\n"+
								"Monthly Salary: "+String.format("%.2f", account.getSalary())+" (Type 1 to select)"+
				                   "\nMonthly Rent: "+String.format("%.2f", account.getRent())+" (Type 2 to select)"+
						           "\nMonthly Utilities: "+String.format("%.2f", account.getUtilitiesBudg())+" (Type 3 to select)"+
				                   "\nMonthly Groceries: "+ String.format("%.2f",account.getGroceryBudg())+" (Type 4 to select)"+
						           "\nMonthly Discretionary Income Budget: "+String.format("%.2f", account.getDisposableBudg())+" (Type 5 to select)"+"\n\nWould you like to update any other items?"+
											"\n1) Yes (Type 1)"+
											"\n2) No (Type 2)");
						Scanner input9 = new Scanner(System.in);
						int selection9 = input9.nextInt();	
						if (selection9==1) {
							inputError3=true;
						}else if(selection9==2) {
							System.out.println("Would you like to return to the main menu?"+
								       "\n1) Yes (Type 1)"+
									   "\n2) No (Type 2)");
							Scanner input10 = new Scanner(System.in);
							int selection10 = input10.nextInt();
							if (selection10==1) {
								inputError3=false;
							}else {
								inputError3=false;
								inputError2=false;
							}
						}

						
	//This code will update the Rent Input						
					}else if(selection8==2) {

						Expense updateRent = new Rents();
						updateRent.display();
						
						account.prepComparisons();
						System.out.println("\nYour updated account info is below:"+"\n\n"+
								"Monthly Salary: "+String.format("%.2f", account.getSalary())+" (Type 1 to select)"+
				                   "\nMonthly Rent: "+String.format("%.2f", account.getRent())+" (Type 2 to select)"+
						           "\nMonthly Utilities: "+String.format("%.2f", account.getUtilitiesBudg())+" (Type 3 to select)"+
				                   "\nMonthly Groceries: "+ String.format("%.2f",account.getGroceryBudg())+" (Type 4 to select)"+
						           "\nMonthly Discretionary Income Budget: "+String.format("%.2f", account.getDisposableBudg())+" (Type 5 to select)"+"\n\nWould you like to update any other items?"+
											"\n1) Yes (Type 1)"+
											"\n2) No (Type 2)");
						Scanner input11 = new Scanner(System.in);
						int selection11 = input11.nextInt();	
						if (selection11==1) {
							inputError3=true;
						}else if(selection11==2) {
							System.out.println("Would you like to return to the main menu?"+
								       "\n1) Yes (Type 1)"+
									   "\n2) No (Type 2)");
							Scanner input12 = new Scanner(System.in);
							int selection12 = input12.nextInt();
							if (selection12==1) {
								inputError3=false;
							}else {
								inputError3=false;
								inputError2=false;
							}	
						}
			
	//This code will update the Utilities Input						
					}else if(selection8==3) {
					Expense updateUtil = new Utilities();
					updateUtil.display();
					
					account.prepComparisons();
					System.out.println("\nYour updated account info is below:"+"\n\n"+
							"Monthly Salary: "+String.format("%.2f", account.getSalary())+" (Type 1 to select)"+
			                   "\nMonthly Rent: "+String.format("%.2f", account.getRent())+" (Type 2 to select)"+
					           "\nMonthly Utilities: "+String.format("%.2f", account.getUtilitiesBudg())+" (Type 3 to select)"+
			                   "\nMonthly Groceries: "+ String.format("%.2f",account.getGroceryBudg())+" (Type 4 to select)"+
					           "\nMonthly Discretionary Income Budget: "+String.format("%.2f", account.getDisposableBudg())+" (Type 5 to select)"+"\n\nWould you like to update any other items?"+
										"\n1) Yes (Type 1)"+
										"\n2) No (Type 2)");
					Scanner input13 = new Scanner(System.in);
					int selection13 = input13.nextInt();	
					if (selection13==1) {
						inputError3=true;
					}else if(selection13==2) {
						System.out.println("Would you like to return to the main menu?"+
							       "\n1) Yes (Type 1)"+
								   "\n2) No (Type 2)");
						Scanner input14 = new Scanner(System.in);
						int selection14 = input14.nextInt();
						if (selection14==1) {
							inputError3=false;
						}else {
							inputError3=false;
							inputError2=false;
						}
					}					
	
	//This code will update the Groceries Input											
					}else if(selection8==4) {
					Expense updateGroc = new Groceries();
					updateGroc.display();

					account.prepComparisons();
					System.out.println("\nYour updated account info is below:"+"\n\n"+
							"Monthly Salary: "+String.format("%.2f", account.getSalary())+" (Type 1 to select)"+
			                   "\nMonthly Rent: "+String.format("%.2f", account.getRent())+" (Type 2 to select)"+
					           "\nMonthly Utilities: "+String.format("%.2f", account.getUtilitiesBudg())+" (Type 3 to select)"+
			                   "\nMonthly Groceries: "+ String.format("%.2f",account.getGroceryBudg())+" (Type 4 to select)"+
					           "\nMonthly Discretionary Income Budget: "+String.format("%.2f", account.getDisposableBudg())+" (Type 5 to select)"+"\n\nWould you like to update any other items?"+
										"\n1) Yes (Type 1)"+
										"\n2) No (Type 2)");
					Scanner input15 = new Scanner(System.in);
					int selection15 = input15.nextInt();	
					if (selection15==1) {
						inputError3=true;
					}else if(selection15==2) {
						System.out.println("Would you like to return to the main menu?"+
							       "\n1) Yes (Type 1)"+
								   "\n2) No (Type 2)");
						Scanner input16 = new Scanner(System.in);
						int selection16 = input16.nextInt();
						if (selection16==1) {
							inputError3=false;
						}else {
							inputError3=false;
							inputError2=false;
						}
					}
	
	//This code will update the Disposable Income Budget Input					
					}else if(selection8==5) {
						System.out.println("What would you like your new Disposable Income budget to be?");
						Scanner input10 = new Scanner(System.in);
						float discBudgUpdate = input10.nextFloat();
					
						
						String connectionUrl = 
								"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
								+ "database=ExpenseDb;"
							    + "user=MyUser;"
								+ "password=ABC1234!;"
							    + "encrypt=true;"
								+ "trustServerCertificate=true;"
							    + "loginTimeout=30;";
	
						String update_discBudget = "UPDATE Account_Info SET Discretionary_budget = (?)";
						
						try (Connection connection = DriverManager.getConnection(connectionUrl);
								PreparedStatement updateDiscretionaryBudget = connection.prepareStatement(update_discBudget);){
							
								updateDiscretionaryBudget.setFloat(1,discBudgUpdate);
								updateDiscretionaryBudget.executeUpdate();
								
								}
						System.out.println("Your Disposable Income budget is now updated.");

						
						account.prepComparisons();
						System.out.println("\nYour updated account info is below:"+"\n\n"+
								"Monthly Salary: "+String.format("%.2f", account.getSalary())+" (Type 1 to select)"+
				                   "\nMonthly Rent: "+String.format("%.2f", account.getRent())+" (Type 2 to select)"+
						           "\nMonthly Utilities: "+String.format("%.2f", account.getUtilitiesBudg())+" (Type 3 to select)"+
				                   "\nMonthly Groceries: "+ String.format("%.2f",account.getGroceryBudg())+" (Type 4 to select)"+
						           "\nMonthly Discretionary Income Budget: "+String.format("%.2f", account.getDisposableBudg())+" (Type 5 to select)"+"\n\nWould you like to update any other items?"+
											"\n1) Yes (Type 1)"+
											"\n2) No (Type 2)");
						Scanner input17 = new Scanner(System.in);
						int selection17 = input17.nextInt();	
						if (selection17==1) {
							inputError3=true;
						}else if(selection17==2) {
							System.out.println("Would you like to return to the main menu?"+
								       "\n1) Yes (Type 1)"+
									   "\n2) No (Type 2)");
							Scanner input18 = new Scanner(System.in);
							int selection18 = input18.nextInt();
							if (selection18==1) {
								inputError3=false;
							}else {
								inputError3=false;
								inputError2=false;
							}
						}						
					}else {
						System.out.println("I'm sorry, your input was not a digit 1-4. Please try again: ");
						inputError3=true;	
					}
	
				} //bracket for end of while loop
		
					
			}else if (selection2==4) {	
				inputError2 = false;

			}else {

	/*PostCondition 14: Program handles error when the user provides an invalid number input for the main menu.
	 */	
				
				inputError2 = true;
				System.out.println("I'm sorry, your input was not a digit 1-4. Please try again: ");
			
			}
			
			} catch (Exception e) {
	/*PostCondition 15: Program handles error when the user provides an invalid character or symbol input for the main menu.
	 */				
				inputError = true;
				System.out.println("I'm sorry, your input was not a digit. Please try again:");
			}

		}
		
		System.out.println("Thank you fur using Personal Budget Tracker 2020! Have a great rest of your day.");		
		
	}

}
