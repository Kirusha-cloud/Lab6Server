package Commands;

public class HelpCommand implements ICommand {
    private CommandBase commandBase;

    public HelpCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.help();
    }
}
