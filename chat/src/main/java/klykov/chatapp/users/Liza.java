package klykov.chatapp.users;

import klykov.listener.client.Client;

public class Liza {

    public static void main(String[] args) {
        Client me = new Client("Liza");
        me.connect();
    }
}
