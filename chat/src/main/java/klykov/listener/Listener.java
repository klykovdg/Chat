package klykov.listener;

import klykov.network.Connection;

public interface Listener {
    void onConnectionReady(Connection connection);
    void onReceiveString(Connection connection, String value);
    void onDisconnection(Connection connection);
    void onException(Connection connection, Exception e);
}
