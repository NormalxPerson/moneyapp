package usermenucommands;

import usermenustates.SettingUpCSVParserState;

import java.sql.SQLException;

public class SetUpCSVParserCommand implements Command{
    String commandLabel = "Add Transactions via CSV File";

    @Override
    public void execute() {
        SettingUpCSVParserState state = null;
        try {
            state = new SettingUpCSVParserState();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        state.showState();
    }

    @Override
    public String printCommandLabel() {
        return commandLabel;
    }
}
