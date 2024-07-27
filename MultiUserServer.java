package chats.muli_users_chat;


import chats.muli_users_chat.util_threads.ClientAcceptThread;
import chats.muli_users_chat.util_threads.ServerOutputThread;
import chats.muli_users_chat.utils.Message;
import chats.muli_users_chat.utils.User;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiUserServer {
    private final int BACKLOG = 4;  //количество человек на сервере
    private final List<User> users;   //список подключенных клиентов
    private final List<Thread> userInputThread; //список потоков чтения
    private final Queue<Message> messages;
    private final ServerSocket server;

    public MultiUserServer(String host, int port) {
        this.server = serverInit(host,port);
        this.users = new ArrayList<>();
        this.userInputThread = new ArrayList<>();
        this.messages = new ConcurrentLinkedQueue<>();

    }

    public List<Thread> getUserInputThread() {
        return userInputThread;
    }

    public Queue<Message> getMessages() {
        return messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public ServerSocket getServer() {
        return server;
    }

    public void start() {
        //Инициализация сервера
        try
        {
            //Запуск потока ожидания подключения клиентов
            ClientAcceptThread acceptThread = new ClientAcceptThread(this);
            //поток стартовал
            acceptThread.start();

            Thread serverOutputThread = new ServerOutputThread(messages,users);
            serverOutputThread.start();
            Thread.sleep(600_000);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {


        }

    };
    private ServerSocket serverInit(String host, int port) {
        try {
            System.out.println("SERVER > starting");
            return new ServerSocket(port,10, InetAddress.getByName(host));
        } catch (IOException e) {
            System.out.println("ошибка при инициализации серверного сокета");
            throw new RuntimeException(e);
        }
    }
    private Socket clientInit(ServerSocket server) {
        Socket client = null;
        try {
            client = server.accept();
        } catch (IOException e) {
            System.out.println("Ошибка при подключении клиентского сокета");
            throw new RuntimeException(e);
        }
        inputOutputStreamsInit(); //инициализировали потоки ввода/вывода
        return client;
    };
    private void inputOutputStreamsInit() {
//        in = inInit(client);
//        out = outInit(client);
    }

    private static Scanner inInit(Socket client) {
        try {
            return new Scanner(
                    new InputStreamReader(
                            client.getInputStream()
                    )
            );
        } catch (IOException e) {
            System.out.println("ошибка инициализации потока ввода");
            throw new RuntimeException(e);
        }
    }


    //запуск программы
    public static void main(String[] args) {
        MultiUserServer server = new MultiUserServer("127.0.0.1",9000);
        server.start();
    }


}
