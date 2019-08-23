/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex_14;

/**
 *
 * @author Londiwe Nkwanyana
 */
public class Calculator {
    public int add(int x, int y){
        return x + y;
    }
    // This method could throw an ArithmeticException
    //    public double divide(int x, int y){
    //        return x / y;
    public double divide(int x, int y) throws ArithmeticException {
        return x / y;
    }
}