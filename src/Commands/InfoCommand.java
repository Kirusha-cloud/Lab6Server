package Commands;

public class InfoCommand implements ICommand {
    private CommandBase commandBase;

    public InfoCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.info();
    }
}
