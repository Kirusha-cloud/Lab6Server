package Commands;

public class GroupCountingByTransportCommand implements ICommand{
    private CommandBase commandBase;

    public GroupCountingByTransportCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.groupCountingByTransport();
    }
}
