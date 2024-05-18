package usermenustates;

import database.Database;
import model.Account;
import usermenucommands.Command;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractState implements State {
    protected Scanner scan = new Scanner(System.in);
    protected HashMap<Integer, Account> mapOfAccounts = new HashMap<>();
    protected Map<Integer, Command> commands = new HashMap<>();
    protected int min = 0;

    @Override
    public abstract void showState();

    @Override
    public abstract void addCommands();

    protected void handleUserInput(int choice) {
        Command userCommand = commands.get(choice);
        userCommand.execute();
    }

    protected boolean validateInput(String input) {
        try {
            int choice = Integer.parseInt(input);
            return choice >= min && choice <= commands.size()-1;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    protected void updateAccountList() {
        try {
            this.mapOfAccounts = Database.getDatabaseInstance().getAccounts();
        } catch (SQLException e) {
            System.out.println("Failed to update list!");
        }
    }

    protected void displayAccounts() {
        updateAccountList();
        if (mapOfAccounts.isEmpty()) {
            System.out.println("No accounts found!");
            return;
        }

        System.out.println("\n| ID   | Name               | Balance        | Type    |");
        System.out.println("|------|--------------------|----------------|---------|");

        // Display each account in a formatted manner
        for (Map.Entry<Integer, Account> set : mapOfAccounts.entrySet()) {
            Account account = set.getValue();
            double balanceInDollars = account.getBalance() / 100.0;
            // Adjusted formatting string to include account type at the end
            System.out.format("| %-4d | %-18s | $%,10.2f    | %-7s |\n",
                    account.getAccount_ID(), account.getAccountName(), balanceInDollars, account.getAccountType());
            System.out.println("|------|--------------------|----------------|---------|");
        }
    }

    protected void displayOptions() {
        for (int i = 1; i <= commands.size()-1; i++) {
            System.out.println("\t"+i +". " + commands.get(i).printCommandLabel());
        }
        System.out.println("\t0. " + commands.get(0).printCommandLabel());
        System.out.print("\tSelect an option: ");
        try {
            String userInput = scan.nextLine();
            if(!validateInput(userInput)) {
                System.out.println("Invalid Input!");
                showState();
            }
            else {
                int choice = Integer.parseInt(userInput);
                handleUserInput(choice);
            }
        } catch (NumberFormatException e) {
            showState();
        }
    }

    protected void printOptionHeader(String title) {
        int paddingLeft = (54 - title.length()) / 2;
        int paddingRight = 54 - title.length() - paddingLeft;
        System.out.println("|" + "-".repeat(paddingLeft) + title + "-".repeat(paddingRight) + "|");
    }


}
