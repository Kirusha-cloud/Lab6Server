package Commands;

public class ShowCommand implements ICommand {
    private CommandBase commandBase;

    public ShowCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.show();
    }
}
