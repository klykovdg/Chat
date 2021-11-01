package klykov.chatapp;

import klykov.listener.server.Server;

public class MainServer {

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
