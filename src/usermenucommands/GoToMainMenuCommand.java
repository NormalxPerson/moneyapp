package usermenucommands;

import usermenustates.MainMenuState;

public class GoToMainMenuCommand implements Command {
    String commandLabel = "Go Back to Main Menu";

    @Override
    public void execute() {
        MainMenuState state = MainMenuState.getMainMenuStateInstance();
        state.showState();
    }

    @Override
    public String printCommandLabel() {
        return commandLabel;
    }
}
