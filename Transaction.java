package main;
import java.io.Serializable;

/* Postcondition 1: This class stores items from the import file as attributes of expense transactions.
*/
public class Transaction<T> implements Serializable {
	
	public T amount;
	public T vendor;
	public T category;
	
	public Transaction (T amount2, T vendor2, T category2) {
		this.amount = amount2;
		this.vendor = vendor2;
		this.category = category2;	
	}

	public T getAmount() {
		return amount;
	}
	
	//convert the getAmount into a double
	public double toDouble() {
		
		double theValue=0;
		//call the getAmount function to get the value, convert it to a string and then parse it to a double
		theValue = Double.parseDouble(getAmount().toString());
		
		return theValue;
	}
	
	
	public T getVendor() {
		return vendor;
	}
	
	public T getCategory() {
		return category;
	}
}
