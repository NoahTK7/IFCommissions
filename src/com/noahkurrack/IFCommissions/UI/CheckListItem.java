package com.noahkurrack.IFCommissions.UI;

import java.io.File;

//credit: http://www.java2s.com/Tutorials/Java/Swing_How_to/JList/Create_JList_of_CheckBox.htm
public class CheckListItem {

    private String label;
    private File file;
    private boolean isSelected = false;

    public CheckListItem(File file) {
        this.label = file.getName();
        this.file = file;
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
