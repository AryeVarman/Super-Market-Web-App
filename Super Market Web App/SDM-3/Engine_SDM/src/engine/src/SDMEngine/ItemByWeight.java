package engine.src.SDMEngine;

import java.io.Serializable;

public class ItemByWeight extends Item implements Serializable {


    public ItemByWeight(int serialNumber, String name) { super(serialNumber, name); }

    @Override
    public String getPurchaseMethod() { return "Weight"; }

    @Override
    public void isValidAmountOfItem(String amountStr) throws Exception {
        try{
            double amountInt = Double.parseDouble(amountStr);
            if(amountInt <= 0){
                throw new Exception("Invalid input, amount must be positive number");
            }
        }catch (Exception e){
            throw new Exception("Invalid input, this item purchase in weight please type a decimal number");
        }
    }

    @Override
    public String toString() { return super.toString() + " Purchase in: Weight"; }
}
