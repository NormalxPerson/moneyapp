package usermenustates;

import usermenucommands.ExitCommand;
import usermenucommands.SetUpCSVParserCommand;
import usermenucommands.ViewAccountsCommand;

public class MainMenuState extends AbstractState {
    private String optionHeader = "Personal Finance Manager";
    private static MainMenuState instance;
    protected int max;

    private MainMenuState() {
        addCommands();
    }

    public static MainMenuState getMainMenuStateInstance() {
        if (instance == null) {
            instance = new MainMenuState();
        }

        return instance;
    }

    @Override
    public void addCommands() {
        commands.put(1, new ViewAccountsCommand());
        commands.put(2, new SetUpCSVParserCommand());
        commands.put(0, new ExitCommand());
        this.max = commands.size()-1;
    }
    @Override
    public void showState() {
        printOptionHeader(optionHeader);
        displayOptions();
    }

}
