package Commands;

public class AddIfMaxCommand implements ICommand{
    private CommandBase commandBase;
     public AddIfMaxCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.addIfMax();
    }
}
