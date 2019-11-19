/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions.data;

import com.noahkurrack.IFCommissions.IFCommissions;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class Contract {

    private Workbook workbook;

    private String customerInfo;
    private long orderNum;
    private String salesRep;
    private Date date;

    private ArrayList<Part> parts;
    private double subtotal;

    private double cost;

    private double profit;
    private double commissionPercent;

    private double commission;

    private static ArrayList<Part> notFound;

    private double addPercentage;
    private boolean isServiceTech;
    private double manualCostAdjustment;

    private boolean isFailed;

    public Contract(Workbook wb, File file) {
        this.parts = new ArrayList<>();
        this.commission = -1;
        this.cost = 0;
        this.profit = 0;

        notFound = new ArrayList<>();

        addPercentage = 0;
        manualCostAdjustment = 0;

        this.workbook = wb;

        try {
            extractData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    IFCommissions.getGui(),
                    "Spreadsheet cannot be read... skipping...",
                    "Contract error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.out.println("Could not read spreadsheet ("+ file +")...skipping.");
            isFailed = true;
        }

        boolean error =  false;
        //TODO: do checks (e.g. date)

    }

    public void process() {
        this.parts.add(new Part(manualCostAdjustment));

        calculateCost();
        calculateCommission();
    }

    private void extractData() throws Exception {
        //get contract info
        Sheet sheet = workbook.getSheetAt(0);
        Row infoRow = sheet.getRow(19);

        this.customerInfo = infoRow.getCell(0).getStringCellValue();
        this.orderNum = (long) infoRow.getCell(6).getNumericCellValue();
        this.salesRep = sheet.getRow(18).getCell(28).getStringCellValue();

        this.date = infoRow.getCell(8).getDateCellValue();

        //end row used as reference to end of parts section of contract (varies)
        int endRow = -1;
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i).getCell(0) == null) continue;
            try {
                sheet.getRow(i).getCell(0).getStringCellValue();
            } catch (IllegalStateException e) {
                continue;
            }
            if (sheet.getRow(i).getCell(0).getStringCellValue().equalsIgnoreCase("Date")){
                endRow = i;
            }
        }
        //get parts
        for (int i = 23; i <= endRow-2; i++) {
            String part = sheet.getRow(i).getCell(0).getStringCellValue().trim();
            String description = sheet.getRow(i).getCell(5).getStringCellValue().trim();
            double quantity = sheet.getRow(i).getCell(22).getNumericCellValue();
            //fields with quantities
            //weird but some fields have 0 when quantity should be 1
            if (quantity>1) {
                this.parts.add(new Part(part, description, quantity));
            } else {
                this.parts.add(new Part(part, description, 1));
            }
        }

        //get subtotal
        this.subtotal = sheet.getRow(endRow).getCell(30).getNumericCellValue();

        if (subtotal == 0) {
            throw new Exception("found subtotal of 0 (or empty cell)");
        }

        this.subtotal = Math.floor(this.subtotal);
    }

    private void calculateCost() {
        ArrayList<ConfigItem> items = IFCommissions.getConfigManager().getItems();
        for (Part part : parts) {
            if (part.getId().equalsIgnoreCase("Manual Cost Adjustment")) {
                cost += part.getTotalCost();
            }
            boolean found = false;
            for (ConfigItem item : items) {
                if (item.getPart().equalsIgnoreCase(part.getId())) {
                    found = true;
                    if (part.getQuantity()>1) {
                        part.setCost(item.getCost());
                        cost += part.getTotalCost();
                    } else {
                        part.setCost(item.getCost());
                        cost += part.getTotalCost();
                    }
                }
            }
            if (!found) {
                //System.out.println("error: part not found: " + part.getId() + ". add to config file or part will be ignored.");
                boolean exists = false;
                for (Part p: notFound) {
                    if (p.getId().equals(part.getId())) exists = true;
                }
                if (!exists) {
                    notFound.add(part);
                }
            }
        }
        this.cost = Math.floor(this.cost);
        //System.out.println("cost: "+cost);
    }

    private void calculateCommission() {
        if (!isServiceTech) {
            this.profit = Math.floor(this.subtotal - this.cost);
            int profitPercentage = (int) Math.round(this.profit / this.cost * 100);

            if (profitPercentage <= 68) {
                commissionPercent = 10;
            } else if (profitPercentage <= 79) {
                commissionPercent = 11;
            } else if (profitPercentage <= 94) {
                commissionPercent = 12;
            } else if (profitPercentage <= 114) {
                commissionPercent = 13;
            } else if (profitPercentage <= 139) {
                commissionPercent = 14;
            } else {
                commissionPercent = 15;
            }

            commissionPercent += addPercentage;

            this.commission = this.profit * (this.commissionPercent/100);
        } else {
            this.commissionPercent = 8;
            commissionPercent += addPercentage;

            this.commission = this.subtotal * (this.commissionPercent/100);
        }
    }

    public static void displayPartsNotFound() {
        if (notFound.size()==0) return;

        StringBuilder out = new StringBuilder();
        for (Part aNotFound : notFound) {
            out.append(aNotFound.getDescription()).append(" (").append(aNotFound.getId()).append(")").append("\n");
        }

        JOptionPane.showMessageDialog(
                IFCommissions.getGui(),
                "The following parts not found in configuration, so no cost will be associated: \n\n"+out+"\nEdit the configuration if one of these parts should have a cost.\nOtherwise, parts will be ignored.",
                "Parts Not Found",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    //Getters
    public String getCustomerInfo() {
        return customerInfo;
    }

    public long getOrderNum() {
        return orderNum;
    }

    public String getSalesRep() {
        return salesRep;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getProfit() {
        return profit;
    }

    public double getCommissionPercent() {
        return commissionPercent;
    }

    public double getCommission() {
        return commission;
    }

    public Date getDate() {
        return date;
    }

    public double getAddPercentage() {
        return addPercentage;
    }

    public void setAddPercentage(double addPercentage) {
        this.addPercentage = addPercentage;
    }

    public boolean getIsServiceTech() {
        return isServiceTech;
    }

    public void setIsServiceTech(boolean isServiceTech) {
        this.isServiceTech = isServiceTech;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public double getCost() {
        return cost;
    }

    public double getManualCostAdjustment() {
        return manualCostAdjustment;
    }

    public void setManualCostAdjustment(double manualCostAdjustment) {
        this.manualCostAdjustment = manualCostAdjustment;
    }

    public boolean isFailed() {
        return isFailed;
    }
}