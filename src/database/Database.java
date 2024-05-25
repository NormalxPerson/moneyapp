package database;

import model.Account;
import model.Transaction;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

public class Database {
  private static Database instance;
  private static final String path2DB = System.getProperty("user.home") + "/.javaDB/info.db";
  private static final String url = "jdbc:sqlite:" + path2DB;
  private Connection connectionWire = null;
  private DatabaseActions da = null;

/*
  private TransactionTable transactionTable = new TransactionTable();
*/


  private Database() throws SQLException {
    bootDB();
    this.da = new DatabaseActions(connectionWire);
/*
    getTransactionTable();
*/
  }

  public static Database getDatabaseInstance() throws SQLException {
    if (instance == null) {
      instance = new Database();
    }
    return instance;
  }

  private void bootDB() throws SQLException {
    File dbFile = new File(path2DB);
    boolean dbFileExists;

    if (!dbFile.exists()) {
      dbFileExists = false;
    } else {
      dbFileExists = true;
    }

    try (Connection conn = DriverManager.getConnection(url)) {
      this.da = new DatabaseActions(conn);

      if (conn != null && !dbFileExists) {
        System.out.println("A new Data has been created");
        this.connectionWire = conn;

        da.createAccountTable();
        da.insertAccount("mainAccount", 0, "debit");
        da.createTransactionTable();
        System.out.println("Connected to new Database");

      } else if (conn != null && dbFileExists) {

        if (!tableExists(conn, "Accounts")) {
          da.createAccountTable();
          da.insertAccount("mainAccount", 0, "debit");
        }
        if (!tableExists(conn, "Transactions")) {
          da.createTransactionTable();
        }
        System.out.println("Connected to Database");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    this.connectionWire = DriverManager.getConnection(url);
  }

  public void addAccount(String name, long balance, String type) {
    System.out.println(name);
    System.out.println(balance);
    System.out.println(type);
      try {
          da.insertAccount(name, balance, type);
        System.out.println("Added Account: " + name.toUpperCase() + " the Balance is $" + balance);
      } catch (SQLException e) {
        System.out.println("Error Adding account in DB method");
      }
  }

  public void checkDBThenAddTransactionList(List<Transaction> transList) throws SQLException {
    try {
      da.checkDBThenAddTransactions(transList);
      System.out.println("Batch of " + transList.size() + " added to DB!");
    } catch (SQLException e) {System.out.println("Error when adding to DB...  checked for dupes!");}
  }

  public void addTransactionList(List<Transaction> transList) throws SQLException {
    try {
      da.insertTransactionFromList(transList);
      System.out.println("Batch of " + transList.size() + " added to DB!");
    } catch (SQLException e) {System.out.println("Error when adding to DB without checking!");}
  }

  public long getRunningTotal(int id) {
      return da.getRunningTotal(id);
  }

  public void deleteTransTable() {
    da.deleteTempTransTable();
  }

  public boolean tableExists(Connection conn, String tableName) throws SQLException {
    DatabaseMetaData dbMetaData = conn.getMetaData();
    try (ResultSet rs = dbMetaData.getTables(null, null, tableName, null)) {
      boolean next = rs.next();
      if (next) {
        return true;
      } else {
        return false;
      }
    }
  }

/*  private void getTransactionTable() throws SQLException {
    this.transactionTable = da.getTransactionTableMetadata("Transactions");

  }*/

/*  public TransactionTable getTransTable() {
    return transactionTable;
  }*/

  public HashMap<Integer, Account> getAccounts() {
    try {
      return mapOfAccounts(da.getAccounts());
    } catch (SQLException e) {
        System.out.println("Error getting Accounts from DB");
    }
    return null;
  }

  private HashMap<Integer, Account> mapOfAccounts(List<Account> allAccounts) {
    HashMap<Integer, Account> mapOfAccounts = new HashMap<>();
    for ( Account account : allAccounts) {
      mapOfAccounts.put(account.getAccount_ID(), account);
    }
    return mapOfAccounts;
  }

  public Account validAccountChecker(int id) {
    return da.checkValidAccountId(id);
  }

  public long verifyBalance(int id) {
    return da.verifyAccountBalance(id);
  }


  public void setupTempTransTable() {
    da.setupTempTransactionTable();
  }

  public List<Transaction> getDuplicateTransactionsFromDB() {
    return da.getPotentialDuplicateTransactions();
  }

  public void updateAccountBalance(int id, long newBalance) {
    da.updateAccountBalance(id, newBalance);
  }
}



/*  private void setTableNamesToList() throws SQLException {
    this.tableNames = da.getTableNames(this.tableNames);
  }

  public ArrayList<String> getListTableNames() {
    return this.tableNames;
  }

  private void setAccountNamesToList() throws SQLException {
    this.accountNames = da.getAccountNames(this.accountNames);
  }

  public ArrayList<String> getAccountNamesList() {
    return this.accountNames;
  }*/








