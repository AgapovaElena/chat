package chats.muli_users_chat.util_threads;

import chats.muli_users_chat.utils.Message;
import chats.muli_users_chat.utils.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Queue;

public class ServerOutputThread extends Thread{
    private final Queue<Message> messages;
    private final List<User> users;

    public ServerOutputThread(Queue<Message> messages, List<User> users) {
        this.messages = messages;
        this.users = users;
    }
    public void run() {
        while(true) {
            //если очередь не пустая
            if (!messages.isEmpty()) {
                //взять последнее сообщение из очереди метод poll()
                Message messageToSend = messages.poll();
                //в цикле по всем пользователям отправить сообщение
                users.forEach(user -> sendMessageExceptSender(user, messageToSend));
            }
        }


    }
    private void sendMessageExceptSender(User user, Message message) {
        //если Ид клиента = не равен ид пользователя
        if(message.clientId() != user.clientid()) {
            clientoutputStream(user.client()).println(message.message());
        }

    }
    private static PrintWriter clientoutputStream(Socket client) {
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
}
