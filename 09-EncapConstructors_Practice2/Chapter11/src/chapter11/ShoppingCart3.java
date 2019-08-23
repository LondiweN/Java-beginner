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
public class ShoppingCart3 {
    Item[] items = {new Item("Shirt",25.60), 
            new Item("WristBand",1.00), 
            new Item("Pants",35.99)};
    
    public static void main(String[] args){   
        ShoppingCart3 cart = new ShoppingCart3();
        cart.displayTotal();
    }
    
    // Use a standard for loop to iterate through the items array, adding up the total price
    //    Skip any items that are back ordered.  Display the Shopping Cart total.
    public void displayTotal(){
        double total = 0;
        for(int idx = 0; idx < items.length; idx++){
            if(items[idx].isBackOrdered()) 
		break;
            total += items[idx].getPrice();
        }
    System.out.println("Shopping Cart total: "+total);
    }
}