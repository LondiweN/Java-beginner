/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chapter10;

/**
 *
 * @author Londiwe Nkwanyana
 */
public class Order2 {
    static final char CORP = 'C';
    static final char PRIVATE = 'P';
    static final char NONPROFIT = 'N';
    String name;
    double total;
    String stateCode;
    double discount;
    char custType;

    public Order2(String name, double total, String state, char custType) {
        this.name = name;
        this.total = total;
        this.stateCode = state;
        this.custType = custType;
        calcDiscount();
    }
    
    public String getDiscount(){
        return Double.toString(discount) + "%";
    }

    public void calcDiscount() {
        // Replace the if logic with a switch statement.
        switch (NONPROFIT) {
            case NONPROFIT:
                discount = (total > 900)? 10.00 : 7.00;
                   break;
            case PRIVATE:
                discount = (total > 900)? 7.00 : 0.00;
                break;
            case CORP:
                discount = (total < 500)? 8.00 : 5.00;
                break;
            default:
                break;
        }
    }
}
