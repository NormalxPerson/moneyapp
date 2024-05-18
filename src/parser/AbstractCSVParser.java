package parser;

import appUtilities.DuplicateTransactionHandler;
import appUtilities.MoneyMathUtils;
import database.Database;
import model.Account;
import model.Transaction;
import model.TransactionType;
import usermenustates.MainMenuState;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

public abstract class AbstractCSVParser implements CSVParser {
    protected Account account;
    protected int accountID;
    protected BufferedReader reader;
    protected Map<String, Integer> columnNumToHeaderName = new HashMap<>();
    protected int rowNumber = 0;
    protected long runningTotal = 0;
    protected int batchSize = 45;
    protected List<Transaction> newTransList = new ArrayList<>();
    protected List<Transaction> potentialDuplicates = new ArrayList<>();
    protected Set<String> uniqueTransactions = new HashSet<>();


    protected void holdingEachLine() throws IOException, SQLException {
        String line;
        System.out.println("dupes list: " + potentialDuplicates.toString());
        while ((line = reader.readLine()) != null) {
            scrapingNeededValues(returnRowValues(line));
            rowNumber++;
        }
        checkForDuplicate();
        sendTransList(this.newTransList);
        System.out.println("dupes list: should be empty" + potentialDuplicates.toString());

        getPotentialsFromDB();
        updateRunningTotal();
        System.out.println("Processed "+rowNumber+" Transactions!");
    }

    protected void gotNewHeaders() {
        try {
            String line = reader.readLine();
            if (columnNumToHeaderName.isEmpty()) {
                gatherHeaders(line);
            }
            else { rowNumber++; }
        }catch (IOException e) {}
    }

    protected void scrapingNeededValues(HashMap<Integer, String> rowVals) {
        HashMap<String, String> transactionInfo = new HashMap<>();

        for( String key : columnNumToHeaderName.keySet()) {
            String neededVal = rowVals.get(columnNumToHeaderName.get(key));
            if (!neededVal.isEmpty()) {
                transactionInfo.put(key, rowVals.get(columnNumToHeaderName.get(key)));
            }
        }
        processTransactionInfo(determineAmount(transactionInfo));
    }

    protected HashMap<String, String> determineAmount(HashMap<String, String> transactionInfo) {
        String deposit = transactionInfo.get("deposit");
        String withdrawal = transactionInfo.get("withdrawal");

        if (deposit != null && deposit.equals(withdrawal)) {
            String realAmount = deposit.replaceAll("[^\\d.]", "");

            if (deposit.contains("(")) {  // Indicates a negative amount, thus a withdrawal
                transactionInfo.remove("deposit");
                transactionInfo.put("transactionType", TransactionType.WITHDRAWAL.name());
                transactionInfo.put("withdrawal", setRealAmount(realAmount, transactionInfo.get("transactionType")));

            } else {
                transactionInfo.remove("withdrawal");
                transactionInfo.put("transactionType", TransactionType.DEPOSIT.name());
                transactionInfo.put("deposit", setRealAmount(realAmount, transactionInfo.get("transactionType")));

            }

            return transactionInfo;
        }
            if (deposit != null && withdrawal == null) {
                transactionInfo.remove("withdrawal");  // Remove empty or null withdrawal
                transactionInfo.put("transactionType", TransactionType.DEPOSIT.name());
                transactionInfo.put("deposit", setRealAmount(deposit.replaceAll("[^\\d.]", ""),transactionInfo.get("transactionType")));

            }
            if (withdrawal != null && deposit == null) {
                transactionInfo.remove("deposit");  // Remove empty or null deposit
                transactionInfo.put("transactionType", TransactionType.WITHDRAWAL.name());
                transactionInfo.put("withdrawal", setRealAmount(withdrawal.replaceAll("[^\\d.]", ""),transactionInfo.get("transactionType")));
            }

        return transactionInfo;
    }

    protected String setRealAmount(String amount, String transactionType) {
        if (transactionType.equalsIgnoreCase("deposit")) {
            return amount;
        }
        else if (transactionType.equalsIgnoreCase("withdrawal")) {
            return "-"+amount;
        }
        return amount;
    }

    protected void processTransactionInfo(HashMap<String, String> transInfo) {
        String date = transInfo.get("date");

        String transType = transInfo.get("transactionType").toLowerCase();
        double beforeConvertAmt = Double.parseDouble(transInfo.get(transType));
        long amount = MoneyMathUtils.dollarsToCents(beforeConvertAmt);

        int accountId = accountID;
        String description = transInfo.get("description");

        String uniqueTransKey = date + "|" + description + "|" + amount;
        Transaction transaction = new Transaction(date, description, amount, accountId, transType);

        if (transaction != null) {
            if (!uniqueTransactions.add(uniqueTransKey)) {
                potentialDuplicates.add(transaction);
            } else {
                newTransList.add(transaction);
               runningTotal += amount;
            }
            if (newTransList.size() == batchSize) {
                sendTransList(newTransList);
            }
        }
    }

    public void checkForDuplicate() {
        if (!potentialDuplicates.isEmpty()) {
            DuplicateTransactionHandler dupeProcessor = new DuplicateTransactionHandler(potentialDuplicates, 1);
            List<Transaction> notDupes = dupeProcessor.processPotentialDuplicates();

            if (!notDupes.isEmpty()) { sendTransList(notDupes); }
            potentialDuplicates.clear();
        }
    }

    protected void sendTransList(List<Transaction> transList) {
        try {
            if (!transList.isEmpty()) {
                Database.getDatabaseInstance().checkDBThenAddTransactionList(transList);
                transList.clear();
            }
        } catch (SQLException e) {System.out.println(e);}
    }

    protected void getPotentialsFromDB() throws SQLException {
        if (potentialDuplicates.isEmpty()) {
        potentialDuplicates = Database.getDatabaseInstance().getDuplicateTransactionsFromDB();

        DuplicateTransactionHandler dupeProcessor = new DuplicateTransactionHandler(potentialDuplicates,2);
        List<Transaction> notDupes = dupeProcessor.processPotentialDuplicates();
                if (!notDupes.isEmpty()) {
                    Database.getDatabaseInstance().addTransactionList(notDupes);
                    potentialDuplicates.clear();
                }


        } else { System.out.println("PotentialDuplicate is not empty when getting dupes from DB");}

    }

    protected void updateRunningTotal() {
        try {
            long dupeAmt = Database.getDatabaseInstance().getDupeAmt();
            if (dupeAmt >= 0) { runningTotal = runningTotal - dupeAmt; }
            else { runningTotal = runningTotal + dupeAmt; }
        } catch (SQLException e) {throw new RuntimeException(e);}
    }

    protected void gatherHeaders(String line) {
        Collection<String> vals = returnRowValues(line).values();
        String[] values = vals.toArray(new String[0]);
        HashMap<String, Integer> columnLocationForKeyword;
        HeaderChecker hc = new HeaderChecker();
        columnLocationForKeyword = hc.compareCSVHeader(values);

        if (columnLocationForKeyword != null && !columnLocationForKeyword.isEmpty()) {
            this.columnNumToHeaderName.putAll(columnLocationForKeyword);
        }
    }

    protected HashMap<Integer, String> returnRowValues(String line) {
        HashMap<Integer, String> values = new HashMap<>();
        StringBuilder word = new StringBuilder();
        boolean inQuotes = false;
        int columnNumber = 1;

        char[] letters = line.toCharArray();
        for (int i = 0; i < letters.length; i++) {
            if (letters[i] == '"') {
                inQuotes = !inQuotes;
            } else if (letters[i] == ',' && !inQuotes) {
                values.put(columnNumber, word.toString());
                word.setLength(0);
                columnNumber++;
            }
            else { word.append(letters[i]); }
        }
        values.put(columnNumber, word.toString());
        return values;
    }

    protected HashMap<Integer, String> headvals(HashMap<Integer, String> map) {
        return map;
    }

    public void setReader() {
        File file = openFileChooser();
        if (file != null) {
            try {
                this.reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                System.out.println("Error! Was that a CSV File?");
                System.out.println("Going Back to Main Menu...");
                MainMenuState.getMainMenuStateInstance();
            }
        }
    }

    public long getRunningTotal() {
        return runningTotal;
    }

    @Override
    public boolean isValidCSVFile(File file) {
        return  (file.isFile() && file.getName().toLowerCase().endsWith(".csv"));
    }

    protected File openFileChooser() {
        JFrame frame = new JFrame();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("/home/normalxcitizen/javastuff/moneyapp/resources"));
        fileChooser.setDialogTitle("Select a CSV file");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV Files", "csv", "CSV"));

        int userSelection = fileChooser.showOpenDialog(frame);


        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File userFile = fileChooser.getSelectedFile();
            if (userFile != null && isValidCSVFile(userFile)) {
                return userFile;
            } else {
                JOptionPane.showMessageDialog(null, "Please select a valid CSV file.", "Invalid File", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    protected void setAccount(Account account) {
        this.account = account;
        this.accountID = account.getAccount_ID();
        hasBeenParsedBefore();
    }

    protected void setColumnNumToHeaderName(HashMap<String, Integer> map) {
        this.columnNumToHeaderName = map;
    }

    public void hasBeenParsedBefore() {
        if (account.hasBeenParsedPreviously()) {
            this.columnNumToHeaderName = account.getCSVColumnInfo();
        }
    }

    public int getRowNumber() {
        return rowNumber;
    }

}