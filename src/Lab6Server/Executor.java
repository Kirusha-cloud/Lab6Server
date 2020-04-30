package Lab6Server;
import Commands.ICommand;
import Commands.SpecialCommand;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Класс, отвечающий за выполнение команды.
 * Содержит в себе экземпляр каждой команды.
 */
class Executor {
    private ICommand add;
    private ICommand addIfMax;
    private ICommand clear;
    private ICommand executeScript;
    private ICommand groupCountingByTransport;
    private ICommand help;
    private ICommand history;
    private ICommand info;
    private ICommand minByFurniture;
    private ICommand printFieldNOF;
    private ICommand removeById;
    private ICommand removeGreater;
    private ICommand show;
    private ICommand updateId;
    private SpecialCommand send;
    private ICommand save;

    Executor(ICommand add, ICommand addIfMax, ICommand clear, ICommand executeScript,
             ICommand groupCountingByTransport, ICommand help, ICommand history, ICommand info,
             ICommand minByFurniture, ICommand printFieldNOF, ICommand removeById, ICommand removeGreater,
             ICommand show, ICommand updateId, SpecialCommand send, ICommand save) {
        this.add = add;
        this.addIfMax = addIfMax;
        this.clear = clear;
        this.executeScript = executeScript;
        this.groupCountingByTransport = groupCountingByTransport;
        this.help = help;
        this.history = history;
        this.info = info;
        this.minByFurniture = minByFurniture;
        this.printFieldNOF = printFieldNOF;
        this.removeById = removeById;
        this.removeGreater = removeGreater;
        this.show = show;
        this.updateId = updateId;
        this.send = send;
        this.save = save;
    }

    void addCommand() throws IOException {
        add.execute();
    }

    void addIfMaxCommand() throws IOException {
        addIfMax.execute();
    }

    void clearCommand() throws IOException {
        clear.execute();
    }

    void executeScriptCommand() throws IOException {
        executeScript.execute();
    }

    void groupCountingByTransportCommand() throws IOException {
        groupCountingByTransport.execute();
    }

    void helpCommand() throws IOException {
        help.execute();
    }

    void historyCommand() throws IOException {
        history.execute();
    }

    void infoCommand() throws IOException {
        info.execute();
    }

    void minByFurnitureCommand() throws IOException {
        minByFurniture.execute();
    }

    void printFieldCommand() throws IOException {
        printFieldNOF.execute();
    }

    void removeByIdCommand() throws IOException {
        removeById.execute();
    }

    void removeGreaterCommand() throws IOException {
        removeGreater.execute();
    }

    void showCommand() throws IOException {
        show.execute();
    }

    void updateIdCommand() throws IOException {
        updateId.execute();
    }
    ArrayList<StringBuilder> send(){
        return send.execute();
    }
    void save() throws IOException {
        save.execute();
    }
}
