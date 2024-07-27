package chats.muli_users_chat.Main;

import chats.muli_users_chat.Client;

public class Main1 {
    public static void main(String[] args) {
        Client c = new Client("127.0.0.1",9000);
        c.start();
    }
}
