package main;

/* Postcondition 1: This class ExportFile exports a summary of expenses by category and variances to budget in a text file
*/

import java.io.*;
import java.lang.*;
import java.util.*;

public class exportFile {

	private Formatter x;
	
	public void OpenFile() {
		
		try {
			x = new Formatter("src/main/Expense_Report");
		}	         
		catch(Exception e) {
			System.out.println("You have an error!");
		}	
	}

	public void addReport(String discBudg, String eval1, String eval2, String eval3) {	
		x.format("Welcome to the Personal Budget Tracker 2020! Your summarized report is below:"+"\n\n"+discBudg+ "\n\n"+ eval1 + "\n"+ eval2 + "\n"+eval3);
	}
	
	public void closeFile() {	
		x.close();
	}	
	
}
