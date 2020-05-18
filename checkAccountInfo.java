package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.stream.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.*;


/* Postcondition 1: This checkAccountInfo object's purpose is to check if there are any existing Account_Ids in the Account_info table.
* 				    If there are account_Ids, the method will return the ID back. If there are no records, it will return a zero.
					This will be used to determine whether the program needs to ask the user for inputs or if it can continue to the main menu.
*/
public class checkAccountInfo implements Callable<Integer> {
	
	
	public static int checkIfNull;
	
	public Integer call() throws SQLException {
	
	
		String connectionUrl = 
				"jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
				+ "database=ExpenseDb;"
			    + "user=MyUser;"
				+ "password=ABC1234!;"
			    + "encrypt=true;"
				+ "trustServerCertificate=true;"
			    + "loginTimeout=30;";

		try (Connection connection = DriverManager.getConnection(connectionUrl);
				Statement statement1 = connection.createStatement();) {
			
			String sql = 
					"select account_id from account_info;";
			
	    	ResultSet result1 = statement1.executeQuery(sql);
	    	
	    	while (result1.next()) {
	    		checkIfNull = result1.getInt(1);
	    	}
		
		}
		
		return checkIfNull;
		
		
	}
}
