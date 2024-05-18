package usermenucommands;

public class ExitCommand implements Command {
    String commandLabel = "Quit App";

    @Override
    public void execute() {
        System.out.println("Exiting the program.");
        System.exit(0);
    }

    @Override
    public String printCommandLabel() {
        return commandLabel;
    }
}
