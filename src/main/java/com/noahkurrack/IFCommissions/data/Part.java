/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.data;

public class Part{

    private String id;
    private String description;
    private double quantity;
    private double cost;
    private double totalCost;

    Part(String id, String d, double quantity) {
        this.id = id;
        this.description = d;
        this.quantity = quantity;
        this.cost = 0;
    }

    //manual adjustment
    Part(double cost) {
        this.id = "Manual Cost Adjustment";
        this.description = "Manual Cost Adjustment";
        this.quantity = 1;
        this.setCost(cost);
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setCost(double cost) {
        this.cost = cost;
        totalCost = this.cost * this.quantity;
    }
}
