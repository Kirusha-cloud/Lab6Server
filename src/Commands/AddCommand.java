package Commands;

public class AddCommand implements ICommand{
    private CommandBase commandBase;
    public AddCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.add();
    }
}
