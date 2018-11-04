/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.data;

public class ConfigItem {

    private String part;
    private String description;
    private double cost;

    public ConfigItem(String part, String description, double cost) {
        this.part = part;
        this.description = description;
        this.cost = cost;
    }

    public ConfigItem(ConfigItem item) {
        this.part = item.getPart();
        this.description = item.getDescription();
        this.cost = item.getCost();
    }

    public String getPart() {
        return part;
    }

    public double getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}