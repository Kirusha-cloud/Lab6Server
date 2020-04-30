package Commands;


import java.util.ArrayList;

public class SpecialCommand {
    private CommandBase commandBase;
    public SpecialCommand(CommandBase commandBase){
        this.commandBase = commandBase;
    }
    public ArrayList<StringBuilder> execute(){
        return commandBase.send();
    }
}
