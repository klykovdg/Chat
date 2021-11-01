package klykov.network;

public interface Connection {
    void sendString(String s);
    void disconnect();
}
