/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions;

import com.noahkurrack.IFCommissions.data.ConfigItem;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Utils {

    public static File stream2file (InputStream in, String fileName) throws IOException {
        final File tempFile = File.createTempFile(fileName, null);
        tempFile.deleteOnExit();
        /*try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }*/
        return tempFile;
    }

    //returns new array in memory with new config item objects
    public static ArrayList<ConfigItem> getDeepCopy(ArrayList<ConfigItem> in) {
        ArrayList<ConfigItem> configItemsCopy = new ArrayList<>();
        for (ConfigItem item : in) {
            configItemsCopy.add(new ConfigItem(item));
        }
        return configItemsCopy;
    }
}
