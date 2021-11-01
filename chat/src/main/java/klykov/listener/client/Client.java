package klykov.listener.client;

import klykov.aids.*;
import klykov.listener.Listener;
import klykov.listener.server.Server;
import klykov.network.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static klykov.admin.Admin.ADMIN;

public class Client implements Listener {
    //поле используется при повторном подключении к Socket
    private int effort = 0;
    //поле задает количество попыток повторного подключения
    private int attempts = 1;
    private String name;
    public final static String EXIT = "exit",
                               CONNECTED = "connected",
                               DISCONNECTED = "disconnected";

    public Client(String name) {
        this.name = name;
    }

    //метод подключает пользователя к серверу и прнимает сообщения из консоли
    public void connect() {
        Address address = new Address();
        String msg;

        try {
            Connection tcpConnection = new TCPConnection(this, address.getIP(), address.getPort());
            Scanner scan = new Scanner(System.in);
            while (true) {
                msg = scan.nextLine();
                tcpConnection.sendString(name + " (" + Time.get() + ") " + msg);
                if (msg.equals(EXIT)) {
                    scan.close();
                    break;
                }
            }
        } catch (ConnectException ce) {
            reconnect(ce);
        } catch (Exception e) {
            System.out.println("There are some problems...");
            ADMIN.log(e);
        }
    }

    //метод показывает историю сообщений для пользователя
    @Override
    public void onConnectionReady(Connection connection) {
        String s;
        try (BufferedReader in = new BufferedReader(new FileReader(Server.CHAT_LOG))) {
            while ((s = in.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            System.out.println("Couldn't read the log file");
            ADMIN.log(e, connection);
        }
        //и сообщает в чат, что пользователь подключился
        connection.sendString(name + " (" + Time.get() + ") " + CONNECTED);
    }

    //метод распечатывает полученные сообщения
    @Override
    public void onReceiveString(Connection connection, String value) {
        System.out.println(value);
    }

    //метод сообщает, что пользователь вышел из чата
    @Override
    public void onDisconnection(Connection connection) {
        connection.sendString(name + " (" + Time.get() + ") " + DISCONNECTED);
    }

    @Override
    public void onException(Connection connection, Exception e) {
        System.out.println("You have logged out");
        ADMIN.log(e, connection);
    }

    private void reconnect(ConnectException ce) {
        //повторное подключение
        if (effort++ != attempts) {
            connect();
        }
        //повторное подключение закончилось неудачей
        if (effort == ++attempts) {
            System.out.println("Couldn't connect\nTry again or wait for some time");
            ADMIN.log(ce);
        }
    }
}