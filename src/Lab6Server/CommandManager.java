package Lab6Server;

import Lab6Client.Command;;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Класс, реализующий выполнение команды и получение ответа.
 * Осуществляет выбор команды по параметру command.getCommandName
 */
public class CommandManager {
    private ArrayList<StringBuilder> answer = new ArrayList<>();
    private Executor executor;
    private Command command;

    CommandManager(Executor executor){
        this.executor = executor;
    }
    void pushCommand(Command command) throws IOException {
        this.command = command;
        switch (command.getCommandName()) {
            case "help":
                executor.helpCommand();
                answer = executor.send();
                break;
            case "info":
                executor.infoCommand();
                answer = executor.send();
                break;
            case "show":
                executor.showCommand();
                answer = executor.send();
                break;
            case "add":
                executor.addCommand();
                answer = executor.send();
                break;
            case "update":
                executor.updateIdCommand();
                answer = executor.send();
                break;
            case "remove_by_id":
                executor.removeByIdCommand();
                answer = executor.send();
                break;
            case "clear":
                executor.clearCommand();
                answer = executor.send();
                break;
            case "execute_script":
                executor.executeScriptCommand();
                answer = executor.send();
                break;
            case "add_if_max":
                executor.addIfMaxCommand();
                answer = executor.send();
                break;
            case "remove_greater":
                executor.removeGreaterCommand();
                answer = executor.send();
                break;
            case "history":
                executor.historyCommand();
                answer = executor.send();
                break;
            case "min_by_furniture":
                executor.minByFurnitureCommand();
                answer = executor.send();
                break;
            case "group_counting_by_transport":
                executor.groupCountingByTransportCommand();
                answer = executor.send();
                break;
            case "print_field_descending_number_of_rooms":
                executor.printFieldCommand();
                answer = executor.send();
                break;
            default:
                System.out.println("Нераспознанная команда.");
        }
    }
    ArrayList<StringBuilder> getAnswer(){
        return answer;
    }
    public Command getCommandFromManager(){
        return command;
    }
    void save() throws IOException {
        executor.save();
    }
}
