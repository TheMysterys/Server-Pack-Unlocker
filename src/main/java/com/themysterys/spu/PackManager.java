package com.themysterys.spu;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PackManager {

    private static final File folder = new File("config/Server-Pack-Unlocker");
    private static File configFile;

    private final HashMap<String, Integer> packMap;

    public PackManager() {
        packMap = new HashMap<>();
        generateFoldersAndFiles();
        readJSON();
        writeJSON();
    }

    public void addPack(String serverIP, int position) {
        packMap.put(serverIP, position);
    }

    public int getPackPosition(String serverIP) {
        return packMap.getOrDefault(serverIP, -1);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void generateFoldersAndFiles() {
        if (!folder.exists()) {
            System.out.println("[Server Pack Unlocker] Creating new config folder");
            folder.mkdir();
        }
        if (folder.isDirectory()) {
            configFile = new File(folder, "pack-order.json");


            if (!configFile.exists()) {
                System.out.println("[Server Pack Unlocker] Creating new pack order file");
                try {
                    configFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (configFile.isDirectory()) {
                throw new IllegalStateException("[Server Pack Unlocker] 'pack-order.json' must be a file!");
            }
        } else {
            throw new IllegalStateException("[Server Pack Unlocker] 'config/Server-Pack-Unlocker' must be a folder!");
        }
    }

    @SuppressWarnings("unchecked")
    private void readJSON() {
        try {
            Map<String, Integer> json = new HashMap<>();
            json = new Gson().fromJson(new FileReader(configFile), json.getClass());
            if (json == null) {
                System.err.println("[Server Pack Unlocker] Invalid configuration!");
                return;
            }
            packMap.putAll(json);
        } catch (JsonSyntaxException e) {
            System.err.println("[Server Pack Unlocker] Invalid configuration!");
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException ignored) {}
    }

    public void writeJSON() {
        try {
            // Convert HashMap to JSON and write to file
            String json = new Gson().toJson(packMap);
            FileWriter writer = new FileWriter(configFile);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new IllegalStateException("[Server Pack Unlocker] Can't update config file", e);
        }
    }
}
