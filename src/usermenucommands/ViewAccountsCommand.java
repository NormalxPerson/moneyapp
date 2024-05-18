package usermenucommands;

import usermenustates.ViewingAccountsState;

public class ViewAccountsCommand implements Command {
    String commandLabel = "View Accounts";

    @Override
    public void execute() {
        ViewingAccountsState.getViewingAccountsStateInstance().showState();
    }

    @Override
    public String printCommandLabel() {
        return this.commandLabel;
    }
}
