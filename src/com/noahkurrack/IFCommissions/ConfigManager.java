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
import java.util.ArrayList;

public class ConfigManager {

    private File configFile;
    private File defaultConfig;

    private ArrayList<ConfigItem> items;

    public ConfigManager() throws IOException {
        items = new ArrayList<>();

        ClassLoader classLoader = this.getClass().getClassLoader();
        this.defaultConfig = new File(classLoader.getResource("com/noahkurrack/IFCommissions/assets/config-defaults.json").getFile());
        this.configFile = new File(classLoader.getResource("com/noahkurrack/IFCommissions/assets/config.json").getFile());

        if (new BufferedReader(new FileReader(configFile)).readLine()==null) {
            configFile.delete();
            Files.copy(defaultConfig.toPath(), configFile.toPath());
        }

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

    public void restoreDefaults() {
        configFile.delete();
        try {
            Files.copy(defaultConfig.toPath(), configFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateConfig();
    }
}