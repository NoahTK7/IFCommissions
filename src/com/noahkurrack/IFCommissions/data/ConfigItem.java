package com.noahkurrack.IFCommissions.data;

public class ConfigItem {

    private String part;
    private double cost;

    public ConfigItem(String part, double cost) {
        this.part = part;
        this.cost = cost;
    }

    public String getPart() {
        return part;
    }

    public double getCost() {
        return cost;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}