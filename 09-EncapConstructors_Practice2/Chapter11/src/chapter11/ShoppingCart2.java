/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter11;

/**
 *
 * @author Londiwe Nkwanyana
 */
public class ShoppingCart2 {
  public static void main(String[] args) {
        String name;
        int age;

        // Parse the args array to populate name and age.  
	// Print an error message if fewer than 2 args are passed in.
        if (args.length < 2) {
            System.out.println("Invalid arg list. There must be 2 arguments");
        } else {
            name = args[0];
            age = Integer.parseInt(args[1]);
            System.out.println("Name = "+name+", Age = "+age);
        }
    }
}
