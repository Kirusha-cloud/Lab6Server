package Commands;


public class RemoveGreaterCommand implements ICommand {
    private CommandBase commandBase;

    public RemoveGreaterCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.removeGreater();
    }
}
