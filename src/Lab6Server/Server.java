package Lab6Server;

import Lab6Client.Command;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

import org.apache.logging.log4j.Logger;

/**
 * Класс, отвечающий за реализацию подключения клиента, получения команды и отправки результата.
 * Логгирование осуществляеися в файл logs.
 */
class Server {
    private static final Logger LOG = LogManager.getLogger();
    private static final int PORT = 5558;
    private static final String ADDRESS = "127.0.0.1";
    private Selector selector;
    private CommandManager commandManager;
    private ByteBuffer readBuffer = ByteBuffer.allocate(2048);
    private int numRead = -1;
    private int previous;


    Server(CommandManager commandManager) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(ADDRESS, PORT));
        selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        this.commandManager = commandManager;
        checkInput();
    }

    /**
     * Метод, реализующий запуск сервера.
     * @throws IOException
     */
    void run() throws IOException {
        LOG.info("Server started");
        while (true) {
            //TODO Requests
            selector.select();
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();
                if (!key.isValid())
                    continue;
                if (key.isAcceptable()) {
                    accept(key);
                }
                if (key.isReadable())
                    read(key);
                if (key.isWritable())
                    write(key);
            }
        }
    }

    /**
     * Метод, реализующий получение нового подключения.
     * @param key
     * @throws IOException
     */
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        LOG.info("Получение нового подключения " + new Date());
    }

    /**
     * Метод, реализующий чтение команды и её десериализацию.
     * @param key
     */
    private void read(SelectionKey key) {
        try {
            Command command;
            SocketChannel socketChannel = (SocketChannel) key.channel();
            numRead = socketChannel.read(readBuffer);
            System.out.println("numRead: " + numRead);
            System.out.println("Позиция буфера: " + readBuffer.position());
            System.out.println("Previous " + previous);
            previous = readBuffer.position();
            ByteArrayInputStream bais = new ByteArrayInputStream(readBuffer.array());
            ObjectInputStream ois = new ObjectInputStream(bais);
            command = (Command) ois.readObject();
            bais.close();
            ois.close();
            LOG.info("Пытаюсь создать объект " + command.getCommandName());
            commandManager.pushCommand(command);
            LOG.info("Object recieved: " + command.getCommandName());
            readBuffer.clear();
            SelectionKey selectionKey = socketChannel.keyFor(selector);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
            selector.wakeup();

        } catch (IOException | ClassNotFoundException ignored) {
        }
    }

    /**
     * Метод, реализующий отправку ответа клиенту.
     * @param key
     * @throws IOException
     */
    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        LOG.info(commandManager.getAnswer());
        baos.write(Objects.requireNonNull(commandManager.getAnswer()).toString().getBytes());
        baos.flush();
        socketChannel.write(ByteBuffer.wrap(baos.toByteArray()));
        LOG.info("Ответ ушёл успешно.");
        SelectionKey selectionKey = socketChannel.keyFor(selector);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    /**
     * Метод, запускающий новый поток для реализациии особых серверных комманд.
     */
    private void checkInput() {
        new Thread(() -> {
            Scanner cin = new Scanner(System.in);
            while (cin.hasNext()) {
                String s = cin.nextLine();
                if (s.equals("save")) {
                    try {
                        commandManager.save();
                        LOG.info("Коллекция сохранена.");
                    } catch (IOException e) {
                        LOG.warn(e.getMessage());
                    }
                }
                if (s.equals("exit")) {
                    try {
                        commandManager.save();
                        LOG.info("Ответ ушёл успешно.");
                        LOG.info("Сервер выключен.");
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
