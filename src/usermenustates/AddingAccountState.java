package usermenustates;

import appUtilities.MoneyMathUtils;
import database.Database;
import usermenucommands.GoToMainMenuCommand;
import usermenucommands.ViewAccountsCommand;

import java.sql.SQLException;

public class AddingAccountState extends AbstractState {
    private static AddingAccountState instance;
    String optionHeader = "Account Management - Adding an Account";

    private AddingAccountState() {
        addCommands();
    }

    public static AddingAccountState getAddingAccountStateInstance() {
        if (instance == null) {
            instance = new AddingAccountState();
        }
        return instance;
    }

    @Override
    public void showState() {
        displayAccounts();
        printOptionHeader(optionHeader);
        getNewAccountInfo();
    }

    @Override
    public void addCommands() {
        commands.put(1, new ViewAccountsCommand());
        commands.put(2, new GoToMainMenuCommand());


    }

    private void getNewAccountInfo(){

        System.out.print("Name of Account to Add: ");
        try {
            Database db = Database.getDatabaseInstance();
            String newAccountName = scan.nextLine();
            String accountType = checkAccountType();
            System.out.print("What is the Balance of " + newAccountName.toUpperCase() + "\n$");
            long newAccountBalance = validInput(scan.nextLine());
            db.addAccount(newAccountName, newAccountBalance, accountType);
            handleUserInput(1);
        } catch (NumberFormatException | SQLException e) {
            System.out.println("Error adding Account!");
            handleUserInput(2);
        }
    }

    private String checkAccountType() {
        String type = "";
        while (true) {
            System.out.print("Is this Debit or Credit? ");
            type = scan.nextLine().trim();
            if (type.equalsIgnoreCase("debit")) {
                return "debit";
            } else if (type.equalsIgnoreCase("credit")) {
                return "credit";
            } else if (type.equalsIgnoreCase("xx")) {
                handleUserInput(2);
            } else {
                System.out.print("Account Can Only be Credit or Debit! Try Again... or xx to Exit");
            }
        }
    }

    private long validInput(String userString) {
        while (true) {
            try {
                return MoneyMathUtils.parseStringDollarsToCents(userString);
            } catch (NumberFormatException e) {
            }
        }
    }
}

