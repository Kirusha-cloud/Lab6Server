package Commands;

public class SaveCommand implements ICommand {
   private CommandBase commandBase;
   public SaveCommand(CommandBase commandBase){
       this.commandBase = commandBase;
   }

    @Override
    public void execute() {
        commandBase.save();
    }
}
