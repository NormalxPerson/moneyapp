package usermenustates;

import database.Database;
import model.Account;
import parser.HolderOfParsers;

import java.io.IOException;
import java.sql.SQLException;

public class SettingUpCSVParserState extends AbstractState {
    String optionHeader = "CSV Parser - Setup";
    Account account;

    public SettingUpCSVParserState() throws SQLException {
        addCommands();
    }
    @Override
    public void showState() {
        displayAccounts();
        printOptionHeader(optionHeader);
        getCSVInfo();
    }

    @Override
    public void addCommands() {

    }

    private boolean validateID(String input) {
        try {
            int choice = Integer.parseInt(input);
           Account account = Database.getDatabaseInstance().validAccountChecker(choice);
           if (account != null) {
               this.account = account;
               return true;
           }
        } catch (SQLException e) {}
        return false;
    }

    private boolean hasBeenParsedBefore() {
        return account.hasBeenParsedPreviously();
    }

    public void getCSVInfo() {
        System.out.print("What Account is the CSV file from? \nEnter Account ID: ");
        if (validateID(scan.nextLine())) {
            try {
                Database.getDatabaseInstance().setupTempTransTable();
                HolderOfParsers parser = new HolderOfParsers(this.account);
                int numOfTrans = parser.getRowNumber();
                long newBalance = account.getBalance();
                System.out.println(parser.getRunningTotal());
                ViewingAccountsState.getViewingAccountsStateInstance().parserRecap(numOfTrans, newBalance, account.getAccountName());
            } catch (IOException | SQLException e) {
            }
        }

    }
}
