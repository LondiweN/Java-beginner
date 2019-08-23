/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex_12;

/**
 *
 * @author Londiwe Nkwanyana
 */
public class Shirt3 extends Item3{
    private char size;
    private char colorCode;
    
    public Shirt3(double price, char size, char colorCode){
        super ("Shirt", price);
        this.size = size;
        this.colorCode = colorCode;
    }
    
    public void display(){
        super.display();
        System.out.println("\tSize: "+size);
        System.out.println("\tColor Code: "+ colorCode);
    } 
    
    // Code a public getColor method that converts the colorCode to a the color name
       // Use a switch statement.  Return the color name. 
    public String getColor(){
        String color = "";
        switch (colorCode){
            case 'B':
                color = "Brown";
                break;
            case 'P':
                color = "Pink";
                break;
            case 'G':
                color = "Green";
                break;
            case 'R':
                color = "Red";
                break;
            default:
                color = "Invalid code";
        }
        return color;
   }
}


