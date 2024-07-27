package chats.muli_users_chat.util_threads;

import chats.muli_users_chat.MultiUserServer;
import chats.muli_users_chat.utils.User;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

//Отдельный поток на подключения клиента
//в отельном потоке - т.к. метод accept() - блокирующий
public class ClientAcceptThread extends Thread{
    private final MultiUserServer server;

    public ClientAcceptThread(MultiUserServer server) {
        this.server = server;
    }
    public void run() {
        while(true) {
            //ожидаем клиента
            try {
                Socket client = server.getServer().accept();
                //как только клиент подкл. то добавляем его в список Клиентский подключений
                User user = new User(generateId(),client);
                server.getUsers().add(user);
                System.out.println("Кто подключился - "  + server.getUsers().size() + " человек в чате, ");
                Thread readerThread = new ReaderThread(user,server.getMessages());
                server.getUserInputThread().add(readerThread);
                readerThread.start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }
//генерация ИД клиента на сайте
    private int generateId() {
        return new Random()
                .nextInt(server.getUsers().size()*10_000,
                        server.getUsers().size()*100_000+1);
    }
}