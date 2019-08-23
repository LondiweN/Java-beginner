/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter11;

import java.util.ArrayList;

/**
 *
 * @author Londiwe Nkwanyana
 */
public class ShoppingCart4 {
    public static void main(String[] args){   
        // Declare, instantiate, and initialize an ArrayList of Strings.  Print and test your code.
        ArrayList<String> items = new ArrayList<>();
        items.add("Caps");
        items.add("Watches");
        items.add("Shirts");
        
        // add (insert) another element at a specific index
        System.out.println(items);
        items.add(0, "Socks");
        System.out.println(items);               

	// Check for the existence of a specific String element.  
        //   If it exists, remove it.
          if (items.contains ("Jersey")){
           items.remove("Jersey");
         }
         System.out.println(items);
    }
}
