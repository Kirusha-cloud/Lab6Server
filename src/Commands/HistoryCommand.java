package Commands;

public class HistoryCommand implements ICommand {
    private CommandBase commandBase;

    public HistoryCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }


    @Override
    public void execute() {
        commandBase.history();
    }
}
