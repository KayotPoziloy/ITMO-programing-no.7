import collection.CollectionManager;
import collection.HumanBeing;
import commands.CommandManager;
import database.CollectionDatabaseHandler;
import database.DatabaseConnection;
import database.UserDatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.core.jmx.Server;

import java.io.*;
import java.net.SocketException;
import java.sql.Connection;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Application {
    /**
     * Корневой логгер для записи логов
     */
    private static final Logger rootLogger = LogManager.getRootLogger();

    private Connection dbConnection;

    public void start(int port) {
        this.createDatabaseConnection();
        UserDatabaseHandler udh = new UserDatabaseHandler(dbConnection);
        CollectionDatabaseHandler cdh = new CollectionDatabaseHandler(dbConnection);

        try {

            HumanBeing[] humanBeings = cdh.loadInMemory();
            CollectionManager collectionManager = new CollectionManager(humanBeings);
            rootLogger.info("Коллекция была загружена из бд.");
            Lock locker = new ReentrantLock();
            CommandManager commandManager = new CommandManager(collectionManager, cdh, locker);
            rootLogger.info("Класс Application готов.");

            ServerConnection serverConnection = new ServerConnection();//здесь хранится datagramSocket сервера.
            serverConnection.createFromPort(port);

            RequestReader requestReader = new RequestReader(serverConnection.getServerSocket());
            ResponseSender responseSender = new ResponseSender(serverConnection.getServerSocket());
            CommandProcessor commandProcessor = new CommandProcessor(udh, cdh, commandManager);

            Server server = new Server(requestReader, responseSender, commandProcessor);
            new Thread(server).start();

        } catch (SQLException ex) {
            System.out.println("Ошибка при загрузке коллекции в память. Завершение работы сервера. " + ex);
            System.exit(-10);
        }
    }

//    /**
//     * Создается объект Selector.
//     * Создается ServerSocketChannel и устанавливается порт с помощью serverConnection.getServerPort().
//     * Канал работает в неблокирующем режиме.
//     */
//    private Selector selector;
//    private void setupSelector() throws IOException {
//        selector = Selector.open();
//        DatagramChannel datagramChannel = DatagramChannel.open();
//        datagramChannel.configureBlocking(false);
//    }


    private void createDatabaseConnection() {
        Scanner scanner = new Scanner(System.in);
//        String jdbcURL = "jdbc:postgresql://pg:5432/studs";
//        String login = "s369108";
//        String password = "VINZQfO4RYGGDKwR";

        String jdbcURL = "jdbc:postgresql://localhost:5432/studs";
        String login = "s369108";
        String password = "VINZQfO4RYGGDKwR";

//        try {
//            scanner = new Scanner(new FileReader("credentials.txt"));
//        } catch (FileNotFoundException ex) {
//            rootLogger.error("Не найден файл credentials.txt с данными для входа. Завершение работы.");
//            System.exit(-1);
//        }
//        try {
//            login = scanner.nextLine().trim();
//            password = scanner.nextLine().trim();
//        } catch (NoSuchElementException ex) {
//            rootLogger.error("Не найдены данные для входа. Завершение работы.");
//            System.exit(-1);
//        }
        DatabaseConnection databaseConnection = new DatabaseConnection(jdbcURL, login, password);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            dbConnection = databaseConnection.connectToDatabase();

//            dbConnection = DriverManager.getConnection(jdbcURL, login, password);
            rootLogger.info("Соединение с бд установлено.");
        } catch (SQLException ex) {
            rootLogger.error("Соединение с бд не установлено. Завершение работы сервера " + ex);
            System.exit(-1);
        }
    }
}