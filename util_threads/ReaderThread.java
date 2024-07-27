package chats.muli_users_chat.util_threads;

import chats.muli_users_chat.utils.Message;
import chats.muli_users_chat.utils.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;

public class ReaderThread extends Thread{
    private final int id;
    private final Scanner in;

    //общая очередь сообщений
    private final Queue<Message> messages;

    public ReaderThread(User user, Queue<Message> messages) {
        this.id = user.clientid();
        this.messages = messages;
        this.in = inInit(user.client());
    }
    @Override
    public void run() {
        String fromClient;
        while(true) {
            fromClient = receive();
            if(fromClient.endsWith("exit")) {
                break;
            } else{
                messages.offer(new Message(id,fromClient));
                System.out.println(messages);
            }
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
            System.out.println("Ошибка инициализации потока ввода");
            throw new RuntimeException(e);
        }
    }
    private String receive() {
        try{
            return in.nextLine();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return "exit";
        }

    }
}
