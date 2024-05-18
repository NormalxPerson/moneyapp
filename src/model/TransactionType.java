package model;

public enum TransactionType {
    DEPOSIT("UPDATE accounts SET balance = balance + ? WHERE account_id = ?;"),
    WITHDRAWAL("UPDATE accounts SET balance = balance - ? WHERE account_id = ?;");

    private final String sqlUpdateBalance;

    TransactionType(String sqlUpdateBalance) {
        this.sqlUpdateBalance = sqlUpdateBalance;
    }

    public String getSqlUpdateBalance() {
        return sqlUpdateBalance;
    }
}