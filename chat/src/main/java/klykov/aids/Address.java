package klykov.aids;

import static klykov.admin.Admin.ADMIN;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;

public class Address {
    private JSONParser parser;
    private JSONObject jsonFile;
    private String path = "src\\main\\resources\\settings.json";

    public Address() {
        parser = new JSONParser();
        try {
            jsonFile = (JSONObject)parser.parse(new FileReader(path));
        } catch (Exception e) {
            ADMIN.log(e);
        }
    }

    public String getIP() {
        return "" + jsonFile.get("ip");
    }

    public int getPort() {
        return (int)(long)jsonFile.get("port");
    }
}
