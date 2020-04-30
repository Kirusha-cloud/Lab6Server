package Commands;


import Lab6Client.*;
import Lab6Server.CommandManager;
import JsonParsing.Parser;
import JsonParsing.Sources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс, отвечающий за реализацию команд и проверку инициализации коллекции.
 * Логгирование осуществляеися в файл logs.
 */
public class CommandBase {
    private PriorityQueue<Flat> p;
    private static final Logger LOG = LogManager.getLogger();
    private Date date;
    private LinkedList<String> commandQueue = new LinkedList<>();
    private ArrayList<String> commandList = new ArrayList<>();
    private ArrayList<StringBuilder> answerQueue = new ArrayList<>();
    private CommandManager commandManager;
    private boolean initFlag;
    private int maxNumberOfRooms;
    private Random random = new Random();
    private Set<Integer> setId = new TreeSet<>();
    private boolean scriptFlag = false;
    private ArrayDeque<StringBuilder> storage = new ArrayDeque<>();
    private String previousFile = "";
    private HashSet<String> paths = new HashSet<>();

    public CommandBase(PriorityQueue<Flat> p) {
        Collections.addAll(commandList, "add", "add_if_max", "clear", "execute_script", "exit",
                "group_counting_by_transport", "help", "history", "info", "min_by_furniture", "print_field_descending_number_of_rooms",
                "remove_by_id", "remove_greater", "show", "update");
        this.p = p;
        initFlag = fieldChecker();
    }

    public void initializeCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    void add() {
        if (initFlag) {
            StringBuilder ans = new StringBuilder();
            Command command = commandManager.getCommandFromManager();
            addToHistory("add");
            Flat element = command.getElement();
            element.setId(returnId());
            element.setCreationDate(setDate());
            p.add(element);
            ans.append("Элемент успешно добавлен.");
            pushAnswer(ans);
        } else {
            errorReport();
        }

    }

    void addIfMax() {
        if (initFlag) {
            addToHistory("add_if_max");
            StringBuilder ans = new StringBuilder();
            Command command = commandManager.getCommandFromManager();
            Flat element = command.getElement();
            findMaxNOF();
            if (command.getElement().getNumberOfRooms() > maxNumberOfRooms) {
                element.setId(returnId());
                element.setCreationDate(setDate());
                p.add(element);
                ans.append("Элемент успешно добавлен в коллекцию");
            } else ans.append("Элемент не добавлен в коллекцию");

            pushAnswer(ans);
        } else {
            errorReport();
        }
    }

    void clear() {
        if (initFlag) {
            addToHistory("clear");
            StringBuilder ans = new StringBuilder().append("Команда clear: ").append("\n");
            p.clear();
            ans.append("Коллекция успешно очищена.");
            if (!scriptFlag)
                pushAnswer(ans);
            else
                storage.addLast(ans);

        } else {
            errorReport();
        }
    }

    private void advancedScript(String path) throws FileNotFoundException {
        //execute_script C:\IdeaProj\Lab5\JsonFile\Script2.txt
        StringBuilder ans = new StringBuilder();
        if (paths.contains(path) || path.equals(previousFile)) {
            ans.append("Скрипт-файл не может быть выполнен, так как вызывает рекурсию").append("\n");
        } else {
            FileInputStream fileInputStream = new FileInputStream(path);
            Scanner scanner = new Scanner(fileInputStream);
            while (scanner.hasNext()) {
                String[] args = scanner.nextLine().split(" ");
                if (args[0].equals("execute_script")) {
                    paths.add(previousFile);
                    if (paths.contains(args[1]) || args[1].equals(path)) {
                        ans.append("Скрипт-файл не может быть выполнен, так как вызывает рекурсию").append("\n");
                        break;
                    } else {
                        paths.add(previousFile);
                        scriptRunner(args);
                    }
                } else {
                    answerQueue.add(scriptRunner(args));
                }
            }
            answerQueue.add(ans);
            paths.clear();
        }
    }

    void executeScript() {
        try {
            answerQueue.clear();
            StringBuilder ans = new StringBuilder();
            if (initFlag) {
                Command command = commandManager.getCommandFromManager();
                FileInputStream file = new FileInputStream(command.getFilename());
                Scanner input = new Scanner(file);
                while (input.hasNextLine()) {
                    String[] args = input.nextLine().split(" ");
                    if (args[0].equals("execute_script")) {
                        paths.add(command.getFilename());
                        if (args[1].equals(command.getFilename())) {
                            ans.append("Скрипт-файл не может быть выполнен, так как вызывает рекурсию.").append("\n");
                            break;
                        } else {
                            previousFile = command.getFilename();
                            scriptRunner(args);
                        }
                    } else {
                        answerQueue.clear();
                        answerQueue.add(scriptRunner(args));
                    }
                }
                answerQueue.add(ans);
                addToHistory("execute_script");
            } else {
                errorReport();
            }
        } catch (FileNotFoundException e) {
            answerQueue.add(new StringBuilder().append("Не удаётся найти указанный файл."));
        }
    }

    private StringBuilder scriptRunner(String[] args) throws FileNotFoundException {
        scriptFlag = true;
        StringBuilder ans = new StringBuilder();
        switch (args[0]) {
            case "help":
                help();
                ans.append(storage.pollFirst()).append("\n");
                break;
            case "info":
                info();
                ans.append(storage.pollFirst()).append("\n");
                break;
            case "show":
                show();
                ans.append(storage.pollFirst()).append("\n");
                break;
            case "add":
                if (args.length < 12)
                    ans.append("Элемент не может быть добавлен в коллекцию. Команда add не имеет достаточно аргументов.").append("\n");
                else {
                    p.add(flatParser(args));
                    ans.append("Элемент успешно добавлен.").append("\n");
                }
                break;
            case "update":
                if (args.length == 13) {
                    boolean flag = false;
                    int id = Integer.parseInt(args[1]);
                    Iterator<Flat> iterator = p.iterator();
                    while (iterator.hasNext()) {
                        Flat element = iterator.next();
                        if (element.getId() == id) {
                            element.setName(args[2]);
                            element.setCoordinates(new Coordinates(Integer.parseInt(args[3]), Double.parseDouble(args[4])));
                            element.setCreationDate(new Date());
                            element.setArea(Long.parseLong(args[5]));
                            element.setNumberOfRooms(Integer.parseInt(args[6]));
                            element.setFurniture(Boolean.parseBoolean(args[7]));
                            element.setView(View.valueOf(args[8]));
                            element.setTransport(Transport.valueOf(args[9]));
                            element.setHouse(new House(args[10], Long.parseLong(args[11]), Integer.parseInt(args[12])));
                            flag = true;
                            ans.append("Элемент успешно обновлён.").append("\n");
                        }
                    }
                    if (!flag)
                        ans.append("Такого элемента нет в коллекции.").append("\n");
                } else ans.append("Команда update не может быть выполнена. Недостаточно аргументов.");
                break;
            case "remove_by_id":
                boolean flag = false;
                int id = Integer.parseInt(args[1]);
                Iterator<Flat> flatIterator = p.iterator();
                while (flatIterator.hasNext()) {
                    if (flatIterator.next().getId() == id) {
                        flatIterator.remove();
                        ans.append("Элемент успешно удалён.").append("\n");
                        flag = true;
                    }
                }
                if (!flag)
                    ans.append("Такого элемента нет в коллекции.").append("\n");
                break;
            case "clear":
                clear();
                ans.append(storage.pollFirst()).append("\n");
                break;
            case "execute_script":
                advancedScript(args[1]);
                break;
            case "add_if_max":
                if (args.length < 12) {
                    ans.append("Элемент не может быть добавлен в коллекцию. Команда add_if_max не имеет достаточно аргументов.").append("\n");
                } else {
                    findMaxNOF();
                    Flat element = flatParser(args);
                    if (element.getNumberOfRooms() > maxNumberOfRooms)
                        p.add(element);
                    else ans.append("Элемент не добавлен в коллекцию.").append("\n");
                }
                break;
            case "remove_greater":
                boolean flag0 = false;
                Flat element = flatParser(args);
                element.setId(returnId());
                p.add(element);
                Iterator<Flat> iterator0 = p.iterator();
                while (iterator0.hasNext()) {
                    Flat element0 = iterator0.next();
                    if (element0.getNumberOfRooms() - element.getNumberOfRooms() > 0) {
                        iterator0.remove();
                        flag0 = true;
                        ans.append("Элемент ").append(element0.getName()).append("успешно удалён.").append("\n");
                    }
                }
                if (!flag0)
                    ans.append("Все элементы коллекции меньше заданного.").append("\n");
                break;
            case "history":
                history();
                ans.append(storage.pollFirst()).append("\n");
                break;
            case "min_by_furniture":
                minByNumberOfRooms();
                ans.append(storage.pollFirst()).append("\n");
                break;
            case "group_counting_by_transport":
                groupCountingByTransport();
                ans.append(storage.pollFirst()).append("\n");
                break;
            case "print_field_descending_number_of_rooms":
                printFieldDescendingNumberOfRooms();
                ans.append(storage.pollFirst()).append("\n");
                break;
            default:
                ans.append("Такой команды не существует").append("\n");
        }
        scriptFlag = false;
        return ans;
    }

    void groupCountingByTransport() {
        if (initFlag) {
            addToHistory("group_counting_by_transport");
            StringBuilder ans = new StringBuilder().append("Команда group_counting_by_transport:").append("\n");
            Map<Transport, Long> flatByTransport = p.stream().collect(Collectors.groupingBy(Flat::getTransport, Collectors.counting()));
            for (Map.Entry<Transport, Long> item : flatByTransport.entrySet()) {
                ans.append("Количество элементов с транспортом ").append(item.getKey()).append(": ").append(item.getValue()).append("\n");
            }
            if (!scriptFlag)
                pushAnswer(ans);
            else
                storage.addLast(ans);

        } else {
            errorReport();
        }
    }

    void help() {
        if (initFlag) {
            addToHistory("help");
            StringBuilder ans = new StringBuilder();
            ans.append("Список доступных команд:").append("\n");
            for (String command : commandList) {
                ans.append(command).append("\n");
            }
            ans.deleteCharAt(ans.length() - 1);
            if (!scriptFlag) {
                pushAnswer(ans);
            } else
                storage.addLast(ans);
        } else {
            errorReport();
        }

    }

    ArrayList<StringBuilder> send() {
        return answerQueue;
    }

    void history() {
        if (initFlag) {
            addToHistory("history");
            StringBuilder ans = new StringBuilder();
            ans.append("Последние 13 использованных команд: ").append("\n");
            for (String s : commandQueue) {
                ans.append(s).append("\n");
            }
            ans.deleteCharAt(ans.length() - 1);
            if (!scriptFlag) {
                pushAnswer(ans);
            } else
                storage.addLast(ans);
        } else {
            errorReport();
        }
    }

    void info() {
        if (initFlag) {
            addToHistory("info");
            StringBuilder ans = new StringBuilder();
            ans.append("Команда info: ").append("\n");
            ans.append("Класс коллекции: ").append(p.getClass()).append("\n");
            ans.append("Время создания коллекции: ").append(date).append("\n");
            ans.append("Количество элементов коллекции: ").append(p.size());
            if (!scriptFlag)
                pushAnswer(ans);
            else
                storage.addLast(ans);
        } else {
            errorReport();
        }
    }


    void minByNumberOfRooms() {
        if (initFlag) {
            addToHistory("min_by_furniture");
            StringBuilder ans = new StringBuilder().append("Элемент коллекции, c минимальным числом комнат: ").append("\n");
            ans.append("Name: ");
            if (p.size() != 0) {
                Flat element = p.stream().min(Comparator.comparingInt(Flat::getNumberOfRooms)).get();
                ans.append(element.getName()).append("; id: ").append(element.getId()).append("; Число комнат: ").append(element.getNumberOfRooms());
            }
            else {ans.append("Коллекция пуста.");}
                if (!scriptFlag) {
                    pushAnswer(ans);
                } else
                    storage.addLast(ans);
        } else {
            errorReport();
        }
    }

    void printFieldDescendingNumberOfRooms() {
        if (initFlag) {
            addToHistory("print_field_descending_number_of_rooms");
            StringBuilder ans = new StringBuilder().append("Вывести значение поля number_of_rooms в порядке убывания:").append("\n");
            p.stream().map(Flat::getNumberOfRooms)
                    .sorted(Comparator.reverseOrder())
                    .forEach(g -> ans.append(g).append("\n"));
            ans.deleteCharAt(ans.length() - 1);
            if (!scriptFlag) {
                pushAnswer(ans);
            } else
                storage.addLast(ans);
        } else {
            errorReport();
        }
    }

    void removeById() {
        if (initFlag) {
            StringBuilder ans = new StringBuilder().append("Удаление элемента по ID: ").append("\n");
            Command command = commandManager.getCommandFromManager();
            int size = p.size();
            int id = command.getId();
            if (p.size() != 0) {
                p.removeIf(element -> element.getId() == id);
            } else
                ans.append("Коллекция пуста. Элемент не может быть удалён.");
            if (size == p.size() && p.size() != 0)
                ans.append("Такого элемента нет в коллекции.");
            if (size != p.size())
                ans.append("Элемент успешно удалён.");
            if (!scriptFlag) {
                pushAnswer(ans);
            } else
                storage.addLast(ans);
            addToHistory("remove_by_id");
        } else {
            errorReport();
        }
    }

    void removeGreater() {
        if (initFlag) {
            boolean flag = false;
            Command command = commandManager.getCommandFromManager();
            StringBuilder ans = new StringBuilder();
            Flat element = command.getElement();
            element.setId(returnId());
            Iterator<Flat> iterator = p.iterator();
            while (iterator.hasNext()) {
                Flat element0 = iterator.next();
                if (element0.getNumberOfRooms() - element.getNumberOfRooms() > 0) {
                    iterator.remove();
                    flag = true;
                    ans.append("Элемент ").append(element0.getName()).append("успешно удалён.").append("\n");
                }
            }
            if (!flag)
                ans.append("Все элементы меньше заданного.");
            addToHistory("remove_greater");
            if (!scriptFlag) {
                pushAnswer(ans);
            } else
                storage.addLast(ans);
        } else {
            errorReport();
        }
    }

    void show() {
        if (initFlag) {
            addToHistory("show");
            StringBuilder ans = new StringBuilder().append("Команда show: ").append("\n");
            p.stream().forEach(x -> ans.append(x.toString()).append("\n"));
            ans.deleteCharAt(ans.length() - 1);
            if (!scriptFlag) {
                pushAnswer(ans);
            } else
                storage.addLast(ans);
        } else {
            errorReport();
        }
    }

    void updateId() {
        int id = 0;
        Date date = new Date();
        if (initFlag) {
            boolean flag = false;
            StringBuilder ans = new StringBuilder();
            Command command = commandManager.getCommandFromManager();
            Flat flatToPush = command.getElement();
            if (p.size() != 0) {
                Iterator<Flat> iterator = p.iterator();
                while (iterator.hasNext()){
                    Flat element = iterator.next();
                    if (element.getId() - command.getId() == 0){
                        id = element.getId();
                        date = element.getCreationDate();
                        iterator.remove();
                        flag = true;
                    }
                }
            } else {ans.append("Коллекция пуста. ");}
            addToHistory("update");
            if (flag) {
                flatToPush.setId(id);
                flatToPush.setCreationDate(date);
                p.add(flatToPush);
                ans.append("Элемент успешно обновлён.");
            }
            else ans.append("Такого элемента нет в коллекции.");
            if (!scriptFlag) {
                pushAnswer(ans);
            } else
                storage.addLast(ans);
        } else {
            errorReport();
        }
    }


    private void addToHistory(String command) {
        if (initFlag) {
            if (commandQueue.size() == 13) {
                commandQueue.removeLast();
                commandQueue.addFirst(command);
            } else commandQueue.addFirst(command);
        } else {
            errorReport();
        }
    }

    private Integer returnId() {
        if (initFlag) {
            Integer id = random.nextInt(100);
            while (setId.contains(id)) {
                id = random.nextInt(100);
            }
            setId.add(id);
            return id;
        } else {
            errorReport();
            return null;
        }
    }

    private void findMaxNOF() {
        for (Flat flat : p) {
            if (flat.getNumberOfRooms() >= maxNumberOfRooms) {
                maxNumberOfRooms = flat.getNumberOfRooms();
            }
        }
    }

    void save() {
        try {
            addToHistory("save");
            OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(Sources.path_out));
            output.write(Parser.parsToJson(p));
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            errorReport();
        } catch (IOException e) {
            LOG.info(("Произошла ошибка при сохранении файла."));
        }
    }

    private void errorReport() {
        StringBuilder ans = new StringBuilder();
        ans.append("Ошибка при инициализации коллекции. Любые действия с коллекцией не будут сохранены.");
        pushAnswer(ans);
    }

    private boolean fieldChecker() {
        date = new Date();
        boolean flag = true;
        for (Flat flat : p) {
            flat.setCreationDate(setDate());
            if (flat.getView() == null || flat.getId() == null || flat.getName() == null || flat.getName().equals("")
                    || flat.getCoordinates() == null || flat.getTransport() == null || flat.getHouse() == null || (!new File(Sources.path_out).canWrite())) {
                LOG.info(flat.getView() + " " + flat.getId() + " " + flat.getName() + " " +
                        flat.getCoordinates() + " " + flat.getTransport() + " " + flat.getHouse());
                flag = false;
                break;
            }
        }
        return flag;
    }

    private Date setDate() {
        return new Date();
    }

    private void pushAnswer(StringBuilder s) {
        answerQueue.clear();
        answerQueue.add(s);
    }

    private Flat flatParser(String[] args) {
        Flat element = new Flat();
        element.setId(returnId());
        element.setName(args[1]);
        element.setCoordinates(new Coordinates(Integer.parseInt(args[2]), Double.parseDouble(args[3])));
        element.setCreationDate(new Date());
        element.setArea(Long.parseLong(args[4]));
        element.setNumberOfRooms(Integer.parseInt(args[5]));
        element.setFurniture(Boolean.parseBoolean(args[6]));
        element.setView(View.valueOf(args[7]));
        element.setTransport(Transport.valueOf(args[8]));
        element.setHouse(new House(args[9], Long.parseLong(args[10]), Integer.parseInt(args[11])));
        return element;
    }
}
