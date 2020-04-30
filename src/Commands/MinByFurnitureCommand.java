package Commands;

public class MinByFurnitureCommand implements ICommand {
    private CommandBase commandBase;

    public MinByFurnitureCommand(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    @Override
    public void execute() {
        commandBase.minByNumberOfRooms();
    }
}
