package com.noahkurrack.IFCommissions;

import com.noahkurrack.IFCommissions.data.ConfigItem;

import java.io.File;
import java.util.ArrayList;

public class ConfigManager {

    private File configFile;

    private ArrayList<ConfigItem> items;

    public ConfigManager() {
        items = new ArrayList<>();

        items.add(new ConfigItem("test", 100));
        items.add(new ConfigItem("test1", 200));
        items.add(new ConfigItem("test2", 300));

        // config file
        // check if one exists
        // if does: load; if not, create default
    }

    public ArrayList<ConfigItem> getItems() {
        return items;
    }

    public void save(ArrayList<ConfigItem> items1) {
        //write to file
    }
}