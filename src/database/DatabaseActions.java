package database;

import model.Account;
import model.DebitAccount;
import model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseActions {
  private Connection conn;
  private static final String makeAccountTable = "CREATE_ACCOUNT_TABLE";
  private static final String makeTransactionTable = "CREATE_TRANSACTION_TABLE";
  private static final String addAccount = "INSERT_ACCOUNT";
  private static final String addTransaction = "INSERT TRANSACTION";
  private static final String getAllTransactions = "GET_ALL_TRANSACTIONS";
  private static final String getAccountIdByName = "GET_ID_BY_ACCOUNT_NAME";


  public DatabaseActions(Connection conn) {
    this.conn = conn;
  }

  public void createAccountTable() throws SQLException {
    Statement st = conn.createStatement();
    int n = st.executeUpdate(QueryManager.queriesMap.get(makeAccountTable));
    if (n == 0) {
      System.out.println("Created Account Table");
    }
  }

  public void createTransactionTable() throws SQLException {
    Statement st = conn.createStatement();
    int m = st.executeUpdate(QueryManager.queriesMap.get(makeTransactionTable));
    if (m == 0) {
      System.out.println("Created Transaction Table");
    }
  }

/*  public ArrayList<String> getTableNames(ArrayList<String> accountNames) throws SQLException {
    ArrayList<String> listOfTableNames = accountNames;
    DatabaseMetaData dbMetaData = conn.getMetaData();
    try (ResultSet rs = dbMetaData.getTables(null, null, "%", new String[]{"TABLE"})) {
      while (rs.next()) {
        String tableName = rs.getString("TABLE_NAME");
        if (!listOfTableNames.contains(tableName)) {
          listOfTableNames.add(tableName);
        }
      }
      return listOfTableNames;
    }
  }*/ //getTableNames()

  public List<Account> getAccounts() throws SQLException {
    List<Account> listOfAccounts = new ArrayList<>();
    Statement st = conn.createStatement();
    try (ResultSet rs = st.executeQuery("SELECT account_id, account_name, account_balance, account_type, datecolnumCSV, descriptioncolnumCSV, depositamtcolnumCSV, withdrawalamtcolnumCSV FROM Accounts")) {
      while (rs.next()) {

        int id = rs.getInt("account_id");
        String name = rs.getString("account_name");
        long balance = rs.getLong("account_balance");
        String type = rs.getString("account_type");

        int dateCol = rs.getInt("datecolnumCSV");
        int descCol = rs.getInt("descriptioncolnumCSV");
        int depositAmtCol = rs.getInt("depositamtcolnumCSV");
        int withdrawalAmtCol = rs.getInt("withdrawalamtcolnumCSV");

        HashMap<String, Integer> csvInfoMap = new HashMap<>();
        if (dateCol > 0 && descCol > 0 && depositAmtCol > 0 && withdrawalAmtCol > 0) {
          csvInfoMap.put("date", dateCol);
          csvInfoMap.put("description", descCol);
          csvInfoMap.put("depositamt", depositAmtCol);
          csvInfoMap.put("withdrawalamt", withdrawalAmtCol);
        }

        Account account = null;

        if ("debit".equalsIgnoreCase(type)) {
          account = new DebitAccount(name, id, balance, type);
        } else if ("credit".equalsIgnoreCase(type)) {
         // account = new CreditAccount(name, id, balance, type);
        }
        if (account != null) {
          if (!csvInfoMap.isEmpty()) {
            account.setCSVColumnInfo(csvInfoMap);
          }
          listOfAccounts.add(account);
        }
        }
      }
      return listOfAccounts;
    }

    public Account checkValidAccountId(int id) {
        Account validAccount = null;
        try {
            List<Account> accounts = getAccounts();
            for(Account a : accounts) {
              if (id == a.getAccount_ID()) {
                validAccount = a;
              }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return validAccount;
    }


  public int getIdByAccountName(String accountName) throws SQLException {
    PreparedStatement preparedst = conn.prepareStatement(QueryManager.queriesMap.get(getAccountIdByName));
    preparedst.setString(1, accountName);
    try (ResultSet rs = preparedst.executeQuery()) {
       rs.next();
         return rs.getInt(1);

    }
  }

  private void appendCSVInfoToAccount(Account account) {
    HashMap<String, Integer> csvInfoMap = getAccountCSVInfo(account.getAccount_ID());
    if (!csvInfoMap.isEmpty()) {
      account.setCSVColumnInfo(csvInfoMap);
    }
  }

  public HashMap<String, Integer> getAccountCSVInfo(int id) {
    HashMap<String, Integer> csvInfoMap = new HashMap<>();

    try (Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery("SELECT datecolnumCSV, descriptioncolnumCSV, depositamtcolnumCSV, withdrawalamtcolnumCSV FROM Accounts WHERE account_id = " + id)) {

      if (rs.next()) {
        int dateCol = rs.getInt(1);
        int descCol = rs.getInt(2);
        int depositAmtCol = rs.getInt(3);
        int withdrawalAmtCol = rs.getInt(4);

        if (dateCol > 0 && descCol > 0 && depositAmtCol > 0 && withdrawalAmtCol > 0) {
          csvInfoMap.put("date", dateCol);
          csvInfoMap.put("description", descCol);
          csvInfoMap.put("depositamt", depositAmtCol);
          csvInfoMap.put("withdrawalamt", withdrawalAmtCol);
          return csvInfoMap;
        }
      }
    } catch (SQLException e) {
      System.out.println(e);
    }
    return null;
  }

  public void updateAccountCSVColumns(int datecolnumCSV, int descriptioncolnumCSV, int depositamtcolnumCSV, int withdrawalamtcolnumCSV, int id) {
    // SQL statement to update columns if they are null
    String sql = "UPDATE Accounts SET " +
            "datecolnumCSV = COALESCE(datecolnumCSV, ?), " +
            "descriptioncolnumCSV = COALESCE(descriptioncolnumCSV, ?), " +
            "depositamtcolnumCSV = COALESCE(depositamtcolnumCSV, ?), " +
            "withdrawalamtcolnumCSV = COALESCE(withdrawalamtcolnumCSV, ?) " +
            "WHERE account_id = "+id+" AND datecolnumCSV IS NULL OR descriptioncolnumCSV IS NULL OR depositamtcolnumCSV IS NULL OR withdrawalamtcolnumCSV IS NULL;";


    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

      // Set parameters to PreparedStatement
      pstmt.setInt(1, datecolnumCSV);
      pstmt.setInt(2, descriptioncolnumCSV);
      pstmt.setInt(3, depositamtcolnumCSV);
      pstmt.setInt(4, withdrawalamtcolnumCSV);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Error adding CSV INFO to Account");
    }
  }

  public void insertAccount(String accountName, long balance, String type) throws SQLException {
    PreparedStatement preparedst = conn.prepareStatement(QueryManager.queriesMap.get(addAccount));
    preparedst.setString(1, accountName);
    preparedst.setLong(2, balance);
    preparedst.setString(3, type);
    preparedst.executeUpdate();
  }

  public long verifyAccountBalance(int id) {
      try {
        PreparedStatement preparedst = conn.prepareStatement("SELECT account_balance from Accounts WHERE account_id = ?;");
        preparedst.setInt(1, id);
        ResultSet rs = preparedst.executeQuery();

        if (rs.next()) {
          return rs.getLong("account_balance");
        } else {
          throw new RuntimeException("Error updating Account Balance!");
        }
      }  catch (SQLException e) {
        throw new RuntimeException("SQL error occurred", e);
      }
  }

  public void insertTransactionFromList(List<Transaction> transList) throws SQLException {
    String insertTransactionListSql = "INSERT INTO Transactions (date, description, amount, account_id) " +
            "VALUES (?, ?, ?, ?);";

    try (PreparedStatement preparedst = conn.prepareStatement(insertTransactionListSql)) {
      conn.setAutoCommit(false);

      for ( Transaction t : transList ) {
        preparedst.setString(1, t.getDate().toString());
        preparedst.setString(2, t.getDescription());
        preparedst.setLong(3, t.getAmount());
        preparedst.setInt(4, t.getAccountId());
        preparedst.addBatch();
      }

      preparedst.executeBatch();
      conn.commit(); // Commit the transaction
      deleteRealTransFromTemp();
    } catch (SQLException e) {
      System.out.println("Error during database insert without check: " + e.getMessage());
      throw e; // Rethrow the exception
    }
  }

  public void mergeTransTables() {
    String mergeTempAndRealSql = "INSERT INTO Transactions (date, description, account_id, amount) " +
            "SELECT date, description, account_id, amount " +
            "FROM TempTransactions WHERE NOT EXISTS (SELECT 1 FROM Transactions " +
            "WHERE Transactions.date = TempTransactions.date " +
            "AND Transactions.description = TempTransactions.description " +
            "AND Transactions.account_id = TempTransactions.account_id);";

    try (Statement st = conn.createStatement()) {
      st.executeUpdate(mergeTempAndRealSql);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
  }

  public void deleteRealTransFromTemp() {
    String deleteSql = "DELETE FROM TempTransactions " +
            "WHERE EXISTS (SELECT 1 FROM Transactions " +
            "WHERE Transactions.date = TempTransactions.date " +
            "AND Transactions.description = TempTransactions.description " +
            "AND Transactions.account_id = TempTransactions.account_id);";

    try (Statement st = conn.createStatement()) {
      st.executeUpdate(deleteSql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Transaction> getPotentialDuplicateTransactions() {
    String sql = "SELECT date, description, amount, account_id FROM TempTransactions " +
            "WHERE EXISTS (SELECT 1 FROM Transactions " +
            "WHERE Transactions.date = TempTransactions.date " +
            "AND Transactions.amount = TempTransactions.amount " +
            "AND Transactions.description = TempTransactions.description " +
            "AND Transactions.account_id = TempTransactions.account_id)";

    List<Transaction> transactions = new ArrayList<>();
    try {
      System.out.println("Executing query: " + sql);
      PreparedStatement stmt = conn.prepareStatement(sql);
      ResultSet rs = stmt.executeQuery();

      // Check if ResultSet is empty and log the condition
      if (!rs.isBeforeFirst()) {
        System.out.println("No potential duplicates found.");
      } else {
        while (rs.next()) {
          String date = rs.getString("date");
          String description = rs.getString("description");
          long amount = rs.getLong("amount");
          int accountId = rs.getInt("account_id");
          String type = amount < 0 ? "withdrawal" : "deposit";


          Transaction transaction = new Transaction(date, description, amount, accountId, type);
          transactions.add(transaction);
        }
        System.out.println("Found " + transactions.size() + " potential duplicates.");
      }
    } catch (SQLException e) {
      System.out.println("SQL Error: " + e.getMessage());
    }
    return transactions;
  }

  /*public List<Transaction> getPotentialDuplicateTransactions() {
    List<Transaction> transactions = new ArrayList<>();
    try (PreparedStatement preSt = conn.prepareStatement("SELECT date, description, amount, account_id FROM TempTransactions WHERE EXISTS (SELECT 1 FROM Transactions WHERE Transactions.date = TempTransactions.date AND Transactions.amount = TempTransactions.amount AND Transactions.description = TempTransactions.description AND Transactions.account_id = TempTransactions.account_id)");
         ResultSet rs = preSt.executeQuery()) {

          while (rs.next()) {
            LocalDate date = rs.getDate("date").toLocalDate();
            String description = rs.getString("description");
            long amount = rs.getLong("amount");
            int accountId = rs.getInt("account_id");
            String type = "";
            if (amount < 0) { type = "withdrawal";}
            else if (amount > 0) { type = "deposit";}

            Transaction transaction = new Transaction(date, description, amount, accountId, type);
            transactions.add(transaction);
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    return transactions;
}*/

public long getAmountfromDuplicateTransactions() {

  try (PreparedStatement preSt = conn.prepareStatement("SELECT sum(amount) FROM TempTransactions ");
       ResultSet rs = preSt.executeQuery()) {

      return rs.getLong(1);
  } catch (SQLException e) {
    System.out.println("Error getting total from actual dupes!");
  }
  return 0;
}
public void checkDBThenAddTransactions(List<Transaction> transList) throws SQLException {
  try (PreparedStatement preparedst = conn.prepareStatement("INSERT INTO TempTransactions (date, description, amount, account_id) VALUES (?, ?, ?, ?);")) {
    conn.setAutoCommit(false);

    for ( Transaction t : transList ) {
      preparedst.setString(1, t.getDate().toString());
      preparedst.setString(2, t.getDescription());
      preparedst.setLong(3, t.getAmount());
      preparedst.setInt(4, t.getAccountId());
      preparedst.addBatch();
    }

    preparedst.executeBatch();
    conn.commit(); // Commit the transaction
    //List<Transaction> tempTransactions = getPotentialDuplicateTransactions();

    mergeTransTables();
    //List<Transaction> tempTransactionsm = getPotentialDuplicateTransactions();
    deleteRealTransFromTemp();
    //List<Transaction> tempTransactionsd = getPotentialDuplicateTransactions();

  } catch (SQLException e) {
    System.out.println("Error during database insert/merge: " + e.getMessage());
    throw e; // Rethrow the exception
  }


}

  public void getAllTransactions() throws SQLException {

    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery(QueryManager.queriesMap.get(getAllTransactions));
    ResultSetMetaData rsMetaData = rs.getMetaData();
    int numOfColumns = rsMetaData.getColumnCount();

    System.out.println("Columns:");
    for ( int i = 1; i <= numOfColumns; i++ ) {
      System.out.print(rsMetaData.getColumnName(i));
    }
    System.out.println("\n--------"); // Separator

  }

    public void setupTempTransactionTable() {
    try (Statement st = conn.createStatement()) {
      st.executeUpdate("CREATE TABLE IF NOT EXISTS TempTransactions (date TEXT NOT NULL, description TEXT NOT NULL, amount REAL NOT NULL, account_id INTEGER NOT NULL);");
    } catch (SQLException e) {
      System.out.println("error creating temp trans table");
    }
  }



  /*public TransactionTable getTransactionTableMetadata(String tableName) throws SQLException {

    TransactionTable transactionTable = new TransactionTable();
    // Query the table with a zero-row result to get metadata without fetching any data
    String query = "SELECT * FROM " + tableName + " WHERE 1=0;";
    try (PreparedStatement pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {
      ResultSetMetaData rsmd = rs.getMetaData();
      int columnCount = rsmd.getColumnCount();
      for (int i = 1; i <= columnCount; i++) {
        transactionTable.addColumn(rsmd.getColumnName(i), rsmd.getColumnTypeName(i), i);
      }
    }
    return transactionTable;
  }*/




private class QueryManager {
  private static final HashMap<String, String> queriesMap = new HashMap<>();

  static {
    queriesMap.put(makeAccountTable, "CREATE TABLE IF NOT EXISTS Accounts (account_id integer PRIMARY KEY AUTOINCREMENT, account_name TEXT NOT NULL UNIQUE, account_balance REAL, account_type TEXT NOT NULL CHECK (account_type IN ('debit', 'credit')), datecolnumCSV INTEGER, descriptioncolnumCSV INTEGER, depositamtcolnumCSV INTEGER, withdrawalamtcolnumCSV INTEGER);");
    queriesMap.put(makeTransactionTable, "CREATE TABLE IF NOT EXISTS Transactions (transaction_id integer PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, description TEXT NOT NULL, amount REAL NOT NULL, account_id INTEGER NOT NULL, FOREIGN KEY(account_id) REFERENCES Accounts(account_id));");


    queriesMap.put(addAccount, "INSERT INTO Accounts (account_name, account_balance, account_type) VALUES (?, ?, ?);");
    queriesMap.put(addTransaction, "INSERT INTO Transactions (date, description, amount, account_id) VALUES (?, ?, ?, ?);");

    queriesMap.put(getAllTransactions, "SELECT * FROM Transactions");
    queriesMap.put(getAccountIdByName, "SELECT account_id FROM ACCOUNTS WHERE account_name = (account_name) VALUES (?);");

  }

}



}
