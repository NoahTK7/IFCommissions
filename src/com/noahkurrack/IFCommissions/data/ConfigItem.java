package com.noahkurrack.IFCommissions.data;

public class ConfigItem {

    private String part;
    private long cost;

    public ConfigItem(String part, long cost) {
        this.part = part;
        this.cost = cost;
    }

    public String getPart() {
        return part;
    }

    public long getCost() {
        return cost;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
}