package model;

import database.Database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class Account {
    protected String accountName;
    protected int account_ID;
    protected long balance;
    protected String type;
    protected Map<String, Integer> requiredValsFromCSVColumnNumber;

    public Account(String accountName, int account_ID, long balance, String type) {
        this.accountName = accountName;
        this.account_ID = account_ID;
        this.balance = balance;
        this.type = type;
    }


    public String getAccountName() {
        return accountName;
    }

    public int getAccount_ID() {
        return account_ID;
    }

    public void updateBalance() {
        try {
            this.balance = Database.getDatabaseInstance().verifyBalance(getAccount_ID());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getBalance() {
        updateBalance();
        return this.balance;
    }

    public String getAccountType() {
        return type;
    }

    public boolean hasBeenParsedPreviously() {
        return (requiredValsFromCSVColumnNumber != null);
    }

    public void setCSVColumnInfo(HashMap<String, Integer> csvColumnInfo) {
        this.requiredValsFromCSVColumnNumber = csvColumnInfo;
    }

    public Map<String, Integer> getCSVColumnInfo() {
        return this.requiredValsFromCSVColumnNumber;
    }

    @Override
    public String toString() {
        return "Account:" +
                "accountName='" + accountName + '\'' +
                ", account_ID='" + account_ID + '\'' +
                ", balance=" + balance +
                '}';
    }
}