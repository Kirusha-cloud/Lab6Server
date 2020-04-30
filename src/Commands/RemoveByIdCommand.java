package Commands;

public class RemoveByIdCommand implements ICommand {
    private CommandBase commandBase;

    public RemoveByIdCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.removeById();
    }
}
