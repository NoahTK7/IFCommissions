/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.UI.util;

import java.io.File;

//adapted from http://www.java2s.com/Tutorials/Java/Swing_How_to/JList/Create_JList_of_CheckBox.htm
public class CheckListItem {

    private String label;
    private File file;
    private boolean isSelected = false;

    public CheckListItem(File file) {
        this.label = file.getName();
        this.file = file;
    }

    //custom select all item
    public CheckListItem() {
        this.label = "Select all";
        this.file = null;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return label;
    }

    public File getFile() {
        return file;
    }
}