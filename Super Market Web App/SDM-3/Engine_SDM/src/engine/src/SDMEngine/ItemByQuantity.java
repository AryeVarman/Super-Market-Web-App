package engine.src.SDMEngine;

import java.io.Serializable;

public class ItemByQuantity extends Item implements Serializable {

    public ItemByQuantity(int serialNumber, String name) { super(serialNumber, name); }

    @Override
    public String getPurchaseMethod() { return "Quantity"; }

    @Override
    public void isValidAmountOfItem(String amountStr)throws Exception {
        try{
            int amountInt = Integer.parseInt(amountStr);
            if(amountInt <= 0){
                throw new Exception("Invalid input, amount must be positive number");
            }
        }catch (Exception e){
            throw new Exception("Invalid input, this item purchase in quantity please type an integer");
        }
    }

    @Override
    public String toString() { return super.toString() + " Purchase in: Quantity"; }
}