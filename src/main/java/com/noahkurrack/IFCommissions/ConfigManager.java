/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions;

import com.noahkurrack.IFCommissions.data.ConfigItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class ConfigManager {

    private File configFile;
    private InputStream defaultConfigStream;

    private ArrayList<ConfigItem> items;

    public ConfigManager() throws IOException {
        items = new ArrayList<>();

        defaultConfigStream = IFCommissions.class.getClassLoader().getResourceAsStream("config-defaults.json");
        InputStream configStream = IFCommissions.class.getClassLoader().getResourceAsStream("config.json");
        this.configFile = Utils.stream2file(configStream, "config");

        //screw it, new config every run of the program. too much hassle to get the current config to be persistent.
        try {
            Files.copy(defaultConfigStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*if (new BufferedReader(new FileReader(configFile)).readLine()==null) {
            configFile = File.createTempFile("config", null);
        }*/

        updateConfig();
    }

    private void updateConfig() {
        items.clear();

        JSONParser jsonParser = new JSONParser();
        JSONObject configData;
        JSONArray configs;
        try {
            configData = (JSONObject) jsonParser.parse(new FileReader(configFile));
            configs = (JSONArray) configData.get("configs");
        } catch (IOException | ParseException e) {
            configs = new JSONArray();
            e.printStackTrace();
        }

        if (configs==null) {
            //throw new Exception("Your config file are still f'd up");
            return;
        }

        for (Object obj : configs) {
            String name = ((JSONObject) obj).get("item").toString();
            String description = ((JSONObject) obj).get("description").toString();
            double cost = Double.valueOf(((JSONObject) obj).get("cost").toString());

            items.add(new ConfigItem(name, description, cost));
        }
    }

    public ArrayList<ConfigItem> getItems() {
        return items;
    }

    public void save(ArrayList<ConfigItem> items1) {
        items = items1;

        JSONObject out = new JSONObject();
        JSONArray configsArray = new JSONArray();
        for (ConfigItem item : items1) {
            JSONObject itemObj = new JSONObject();
            itemObj.put("item", item.getPart());
            itemObj.put("description", item.getDescription());
            itemObj.put("cost", item.getCost());
            configsArray.add(itemObj);
        }
        out.put("configs", configsArray);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(configFile, false));
            writer.write(out.toJSONString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: FIX
    public void restoreDefaults() {
        configFile.delete();
        try {
            InputStream configStream = IFCommissions.class.getClassLoader().getResourceAsStream("config.json");
            this.configFile = Utils.stream2file(configStream, "config");

            //screw it, new config every run of the program. too much hassle to get the current config to be persistent.
            try {
                Files.copy(defaultConfigStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //this.configFile = Utils.stream2file(defaultConfigStream, "config");
            //Files.copy(defaultConfig.toPath(), configFile.toPath());
            //Files.copy(defaultConfigStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateConfig();
    }
}