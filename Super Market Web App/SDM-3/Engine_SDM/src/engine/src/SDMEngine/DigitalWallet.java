package engine.src.SDMEngine;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;

public class DigitalWallet {
    public enum TransactionType { CHARGE, RECEIVE, PAY }
    private class Transaction {

        private final TransactionType transactionType;
        private final double transactionAmount;
        private final double balanceBeforeTransaction;
        private final double balanceAfterTransaction;
        private final Date date;
        private final String dateStr;

        public Transaction(TransactionType transactionType, Date date,
                           double transactionAmount, double balanceBeforeTransaction) {
            this.transactionType = transactionType;
            this.transactionAmount = transactionAmount;
            this.balanceBeforeTransaction = balanceBeforeTransaction;
            this.balanceAfterTransaction = calculateBalance();
            this.date = date;
            dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        }

        private double calculateBalance() {
            if(this.transactionType.equals(TransactionType.PAY)){
                return this.balanceBeforeTransaction - this.transactionAmount;
            }
            else {
                return this.balanceBeforeTransaction + this.transactionAmount;
            }
        }

        public TransactionType getTransactionType() { return transactionType; }

        public Date getDate() { return date; }

        public double getTransactionAmount() { return transactionAmount; }

        public double getBalanceBeforeTransaction() { return balanceBeforeTransaction; }

        public double getBalanceAfterTransaction() { return balanceAfterTransaction; }
    }

    private double accountBalance;
    List<Transaction> transactionsHistory;

    public DigitalWallet() {
        accountBalance = 0;
        transactionsHistory = new LinkedList<>();
    }

    public void makeTransaction(TransactionType transactionType, Date date, double transactionAmount) {
        Transaction newTransaction = new Transaction(transactionType, date, transactionAmount, this.accountBalance);
        this.accountBalance = newTransaction.getBalanceAfterTransaction();
        this.transactionsHistory.add(newTransaction);
    }

    public double getAccountBalance() { return accountBalance; }
}