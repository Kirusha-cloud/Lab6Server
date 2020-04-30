package Commands;

public class PrintFieldDescendingNumberOfRoomsCommand implements ICommand {
    private CommandBase commandBase;

    public PrintFieldDescendingNumberOfRoomsCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }


    @Override
    public void execute() {
        commandBase.printFieldDescendingNumberOfRooms();
    }
}
