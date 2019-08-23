/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex_13;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Londiwe Nkwanyana
 */
public class ShoppingCart01 {
    public static void main(String[] args)
    {
        String[] days = {"monday","saturday","tuesday","sunday","friday"};
        
        // Convert the days array into an ArrayList
        // Loop through the ArrayList, printing out "sunday" elements in 
        //   upper case (use toUpperCase() method of String class) 
        // Print all other days in lower case 
        // Print out the ArrayList  
        ArrayList<String> dayList = new ArrayList(Arrays.asList(days));
            for (String s : dayList){
            if (s.equals("sunday")){
                   System.out.println(s.toUpperCase());
               }else System.out.println(s);
            }
            System.out.println(dayList);            
    }
}



  
