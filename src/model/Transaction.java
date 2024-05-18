package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private LocalDate date;
    private String description;
    private long amount;
    private int accountId;
    private TransactionType transactionType;


    public Transaction(String dateString, String description, long amount, int accountId, String transactionType) {
        this.date = DateFormatEnum.parseWithAllFormats(dateString);
        this.description = description;
        this.amount = amount;
        this.accountId = accountId;
        this.transactionType = setTransType(transactionType);
    }

    private String getDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        return date.format(formatter);
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public long getAmount() {
        return amount;
    }

    private TransactionType setTransType(String type) {
        if (type.equalsIgnoreCase("deposit")) {
            return TransactionType.DEPOSIT;
        }
        else if (type.equalsIgnoreCase("withdrawal")) {
            return TransactionType.WITHDRAWAL;
        }
        return null;
    }

    public TransactionType getTransType() {
        return transactionType;
    }

    public void setAmount(long amt) {
        this.amount = amt;
    }
    public int getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", accountId='" + accountId + '\'' +
                ", type='" + transactionType.name() + '\'' +  // Use getDescription for more info
                '}';
    }
}




