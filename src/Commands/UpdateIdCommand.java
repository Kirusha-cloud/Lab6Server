package Commands;


public class UpdateIdCommand implements ICommand {
    private CommandBase commandBase;

    public UpdateIdCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.updateId();
    }
}
