package commands;

import collection.CollectionManager;
import collection.HumanBeing;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;
import file.HumanBeingReader;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class AddCommand extends Command {

    private CollectionManager collectionManager;

    private CollectionDatabaseHandler cdh;

    private HumanBeingReader humanBeingReader;

    public AddCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.collectionManager = collectionManager;
        this.cdh = cdh;
    }

    public AddCommand(HumanBeingReader humanBeingReader) {
        super("add");
        this.humanBeingReader = humanBeingReader;
    }

    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData,
                        Lock locker) throws CannotExecuteCommandException, SQLException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            if (arguments.length > 0) {
                throw new CannotExecuteCommandException("У этой команды нет аргументов");
            }
            HumanBeing humanBeing = humanBeingReader.read();
            humanBeing.setOwner(userData.getLogin());
            super.result.add(humanBeing);
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            try {
                locker.lock();

                HumanBeing humanBeing = (HumanBeing) this.getResult().get(0);
                if (!cdh.isAnyRowById(humanBeing.getId())) {
                    cdh.insertRow(humanBeing);
                    collectionManager.add(humanBeing);
                    printStream.println("Элемент добавлен в коллекцию.");
                } else {
                    printStream.println("Элемент с указанным id уже существует.");
                }
            } finally {
                locker.unlock();
            }
        }
    }

    @Override
    public String getDescription() {
        return "добавляет новый элемент в коллекцию";
    }
}
