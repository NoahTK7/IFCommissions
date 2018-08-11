package com.noahkurrack.IFCommissions;

import com.noahkurrack.IFCommissions.data.ConfigItem;

import java.io.File;
import java.util.ArrayList;

public class ConfigManager {

    private File configFile;

    private ArrayList<ConfigItem> items;

    public ConfigManager() {
        items = new ArrayList<>();

        items.add(new ConfigItem("Basic Dog Package", 500));
        items.add(new ConfigItem("400' Wire Installed", 300));
        items.add(new ConfigItem("Training (2 Visits)", 200));

        // TODO: config file
        // config file
        // check if one exists
        // if does: load; if not, create default
    }

    public ArrayList<ConfigItem> getItems() {
        return items;
    }

    public void save(ArrayList<ConfigItem> items1) {
        items = items1;
        //write to file
    }
}