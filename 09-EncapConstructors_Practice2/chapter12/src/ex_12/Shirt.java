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
public class Shirt extends Item{
     private char size;
    private char colorCode;
    public Shirt(double price, char size, char colorCode){
        super ("Shirt", price);
        this.size = size;
        this.colorCode = colorCode;
}
}