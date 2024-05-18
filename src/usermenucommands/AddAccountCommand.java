package usermenucommands;

import usermenustates.AddingAccountState;

public class AddAccountCommand implements Command {
    String commandLabel = "Add New Account";

    @Override
    public void execute() {
        AddingAccountState.getAddingAccountStateInstance().showState();
    }

    @Override
    public String printCommandLabel() {
        return commandLabel;
    }
}
