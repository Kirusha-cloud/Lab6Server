package Lab6Server;

import Lab6Client.Flat;
import Commands.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.PriorityQueue;

import static JsonParsing.Sources.path_out;

/**
 * Точка входа серверного приложения.
 * Создаются все экземпляры всех необхходимых модулей.
 * Создаётся коллекция и иницализируется.
 * Запускается сервер.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(path_out));
            Type foundQueueType = new TypeToken<PriorityQueue<Flat>>() {
            }.getType();
            PriorityQueue<Flat> flatsQueue = new Gson().fromJson(inputStreamReader, foundQueueType);
            CommandBase commandBase = new CommandBase(flatsQueue);
            Executor executor = new Executor(new AddCommand(commandBase), new AddIfMaxCommand(commandBase), new ClearCommand(commandBase),
                    new ExecuteScriptCommand(commandBase), new GroupCountingByTransportCommand(commandBase),
                    new HelpCommand(commandBase), new HistoryCommand(commandBase), new InfoCommand(commandBase), new MinByFurnitureCommand(commandBase),
                    new PrintFieldDescendingNumberOfRoomsCommand(commandBase), new RemoveByIdCommand(commandBase), new RemoveGreaterCommand(commandBase),
                    new ShowCommand(commandBase), new UpdateIdCommand(commandBase), new SpecialCommand(commandBase),new SaveCommand(commandBase));
            CommandManager commandManager = new CommandManager(executor);
            commandBase.initializeCommandManager(commandManager);
            Server server = new Server(commandManager);
            server.run();
        }
        catch (NullPointerException e){
            System.out.println("Переменная окружения задана некорректно.");
            System.exit(0);
        }
        catch (FileNotFoundException e){
            System.out.println("У файла, хранящего коллекцию не хватает прав на чтение. Добавьте право на чтение файлу " +
                    "и перезапустите программу.");
        }
        catch (com.google.gson.JsonSyntaxException e) {
            System.out.println("Неверно заполненный файл! Программа не может быть выполнена!");
        }
    }
}
