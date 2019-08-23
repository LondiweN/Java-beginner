/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter11;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author Londiwe Nkwanyana
 */

public class ShoppingCart {
    public static void main(String[] args){
	// Declare a LocalDateTime object
    LocalDateTime orderDate;
        
	// Initialize the LocalDateTime object and print it.
    orderDate = LocalDateTime.now();
    System.out.println("Order date: "+ orderDate);

	// Format the object using ISO_LOCAL_DATE; Print it.
    String fDate = orderDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    System.out.println("Formatted order date: "+ fDate);
    }

 
}