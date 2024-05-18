package usermenustates;

import appUtilities.MoneyMathUtils;
import usermenucommands.AddAccountCommand;
import usermenucommands.ExitCommand;
import usermenucommands.GoToMainMenuCommand;


public class ViewingAccountsState extends AbstractState {
    private static ViewingAccountsState instance;
    String optionHeader = "Account Management";

    private ViewingAccountsState() {
        addCommands();
    }

    public static ViewingAccountsState getViewingAccountsStateInstance() {
        if (instance == null) {
            instance = new ViewingAccountsState();
        }
        return instance;
    }
    @Override
    public void showState() {
        displayAccounts();
        printOptionHeader(optionHeader);
        displayOptions();
    }

    @Override
    public void addCommands() {
        commands.put(1, new AddAccountCommand());
        commands.put(2, new GoToMainMenuCommand());
        commands.put(0, new ExitCommand());

    }

    public void parserRecap(int numOfTransaction, long newBalance, String accountName) {
        String formattedAmount = MoneyMathUtils.formatForDisplay(newBalance);
        displayAccounts();
        printOptionHeader("CSV File has been Processed");
        System.out.println("Added "+numOfTransaction+ " Transactions to your "+ accountName.toUpperCase()+ " Account!");
        System.out.println(accountName.toUpperCase() + " now has a Balance of "+ formattedAmount);
        displayOptions();
    }
}
