package chats.muli_users_chat;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final String host;
    private final int port;
    private Scanner in;     //поток ввода (для каждого клиента будет свой)
    private PrintWriter out;    //поток вывода (для каждого клиента будет свой)
    private final Scanner CONSOLE = new Scanner(System.in);

    public Client(String host, int port) {
        this.host = host;
        this.port = port;

    }
    public void start() {
        try(Socket client = clientInit()
                ) {
            //запуск потока слушания консоли клиента
            ClientOutputThread sendThread = new ClientOutputThread(out);
           sendThread.start();

            while (true) {
                //получили от сервера сообщение
                String fromServer = receive();      // 2 - клиент получает сообщение
                System.out.println("\n"+ fromServer);
                //если exit - конец
                if(fromServer.equals("exit")) {
                    send("exit");
                    break;
                }

            }

        } catch (IOException e) {
            System.out.println("Ошибка подключения к серверу");
            e.printStackTrace();
        }
    };
    private Socket clientInit () {
        Socket client;
        try {
            client =  new Socket(host, port);
        } catch (IOException e) {
            System.out.println("ошибка подключения  к серверу");
            throw new RuntimeException(e);
        }
        inputOutputStreamsInit(client);
        return client;
    }
    private void inputOutputStreamsInit(Socket client) {
        in = inInit(client);
        out = outInit(client);
    }
    private PrintWriter outInit(Socket client) {
        //Инициализация потока вывода -
        try {
            return new PrintWriter(              //такое кол-во оберток позволяет любое сообщение печать методом println целиком
                    new BufferedWriter(           //буфферезированный потомк
                            new OutputStreamWriter(       //символьный поток
                                    client.getOutputStream()  //байтовый потом
                            )
                    ),true      //2парам true говорит, о том как только положили, то сообщение отправится без
                    // autoflush = true отправит целиком сообщение только
            );
        } catch (IOException e) {
            System.out.println("ошибка при инициализации потока вывода");
            throw new RuntimeException(e);

        }

    }
    private Scanner inInit(Socket client) {
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
    private void sendMessage() {
        System.out.println();
        String message = CONSOLE.nextLine();
        send(message);
    }
    private void send (String message) {
        //сервер отправляет сообщения клиентов (между клиентами)
        out.println(message);
    }
    private  String receive() {
        //получать все сообщения от всех клиентов
        return in.nextLine();

    }
//Отдельный поток для слушания консоли клиента
    class ClientOutputThread extends Thread {
        private final PrintWriter out;  //поток вывода
        private final Scanner console;  //консоль
        public ClientOutputThread(PrintWriter out) {
            this.out = out;
            this.console = new Scanner(System.in);
            //обзявление потока Демоном - при завершении основного потока
            // закрывается все приложение.
            //если не объявить, то приложение не закроется
            setDaemon(true);
        }
        //запуск потока методом run
        @Override
        public void run() {
            while (true) {
                String message = console.nextLine();
                out.println("AGAPOVA> " + message);
                if(message.equals("exit")) {
                    break;
                }
            }
            console.close();
        }
    }


    public static void main(String[] args) {
        Client client = new Client("127.0.0.1",9000);
        client.start();
    }

}
