package Commands;

public class ExecuteScriptCommand implements ICommand {
    private CommandBase commandBase;

    public ExecuteScriptCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.executeScript();
    }

}
