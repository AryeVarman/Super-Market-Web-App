package engine.src.SDMEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Customer extends User implements Serializable  {

    private List<Order> orderHistory;
    private double averageCostForOrdersWithoutDelivery;
    private double averageCostForDelivery;

    public Customer(int serialNumber, String name, Coordinate location){
        super(serialNumber,name, UserManager.UserType.CUSTOMER);

        this.name = name;
        this.orderHistory = new LinkedList<>();
        this.averageCostForOrdersWithoutDelivery = 0;
        this.averageCostForDelivery = 0;
    }

    public Customer(int serialNumber, String name) {
        super(serialNumber,name, UserManager.UserType.CUSTOMER);
        this.orderHistory = new LinkedList<>();
        this.averageCostForOrdersWithoutDelivery = 0;
        this.averageCostForDelivery = 0;
    }

    @Override
    public int getSerialNumber() { return serialNumber; }

    public void addOrderToList(Order order) {
        if (order != null) {
            this.orderHistory.add(order);
        }
    }

    public final String getName() { return name; }

    public final List<Order> getOrderHistory() { return orderHistory; }

    public double getAverageCostForOrdersWithoutDelivery() { return averageCostForOrdersWithoutDelivery; }

    public double getAverageCostForDelivery() { return averageCostForDelivery; }

    public void updateAverageCostForOrdersWithoutDelivery(double newCost) {
        double totalCost = (this.orderHistory.size() * this.averageCostForOrdersWithoutDelivery) + newCost;
        this.averageCostForOrdersWithoutDelivery = totalCost / (this.orderHistory.size() + 1);
    }

    public void updateAverageCostForDelivery(double newCost) {
        double totalCost = (this.orderHistory.size() * this.averageCostForDelivery) + newCost;
        this.averageCostForDelivery = totalCost / (this.orderHistory.size() + 1);
    }

    public String customerDetails() { return  "name: " + name + " | serialNumber: " + serialNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Customer customer = (Customer) o;
        return serialNumber == customer.serialNumber &&
                name.equals(customer.name);
    }

    @Override
    public int hashCode() { return Objects.hash(serialNumber); }

    @Override
    public String toString() {
        return
                "name: " + name +
                " | serialNumber: " + serialNumber +
                " | amount of orders made so far: " + orderHistory.size() +
                String.format(" | average cost for orders without delivery: %.2f", averageCostForOrdersWithoutDelivery) +
                String.format(" | average cost for delivery: %.2f", averageCostForDelivery);

    }

    public String toStringForFXML() {
        return  "name: " + name +'\n'+
                "serialNumber: " + serialNumber +'\n'+
                "amount of orders made so far: " + orderHistory.size() +'\n'+
                String.format("average cost for orders without delivery: %.2f\n", averageCostForOrdersWithoutDelivery) +
                String.format("average cost for delivery: %.2f\n", + averageCostForDelivery);

    }
}