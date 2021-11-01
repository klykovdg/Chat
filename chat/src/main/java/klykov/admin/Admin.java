package klykov.admin;

import klykov.network.Connection;
import java.io.*;

public class Admin implements AutoCloseable {
    private File log = new File("exception.log");
    private PrintWriter writer;
    public static final Admin ADMIN = new Admin();

    private Admin() {
        createLog();
    }

    public void log(Exception e) {
        e.printStackTrace(writer);
    }

    public void log(Exception e, Connection connection) {
        writer.write(connection + " exception: ");
        writer.flush();
        e.printStackTrace(writer);
    }

    public void close() throws IOException {
        writer.close();
    }

    private void createLog() {
        try {
            if (!log.exists()) {
                log.createNewFile();
            }
            writer = new PrintWriter(new FileWriter(log, true), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
