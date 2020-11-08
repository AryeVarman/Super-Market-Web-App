package engine.src.SDMEngine;

import java.util.Date;
import java.util.Objects;

public abstract class User {

    protected int serialNumber;
    protected String name;
    protected UserManager.UserType userType;
    protected final DigitalWallet digitalWallet;

    public User(int serialNumber, String name, UserManager.UserType userType){
        this.serialNumber = serialNumber;
        this.name = name;
        this.userType = userType;
        digitalWallet = new DigitalWallet();
    }

    public void makeTransaction(DigitalWallet.TransactionType transactionType, Date date, double transactionAmount) {
        this.digitalWallet.makeTransaction(transactionType, date, transactionAmount);
    }

    public DigitalWallet getDigitalWallet() { return digitalWallet; }

    public String getName() { return name; }

    public int getSerialNumber() { return serialNumber; }

    public UserManager.UserType getUserType() { return userType; }

    @Override
    public String toString() {
        return "User{" +
                "serialNumber=" + serialNumber +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return serialNumber == user.serialNumber &&
                name.equals(user.name) &&
                userType == user.userType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber, name, userType);
    }
}
