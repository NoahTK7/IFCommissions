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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public Contract(Workbook wb) {
        this.parts = new ArrayList<>();
        this.commission = -1;
        this.cost = 0;

        notFound = new ArrayList<>();

        addPercentage = 0;

        this.workbook = wb;
        extractData();
    }

    public void process() {
        calculateCost();
        calculateCommission();
    }

    private void extractData() {
        //get contract info
        Sheet sheet = workbook.getSheetAt(0);
        Row infoRow = sheet.getRow(19);

        this.customerInfo = infoRow.getCell(0).getStringCellValue();
        this.orderNum = (long) infoRow.getCell(6).getNumericCellValue();
        this.salesRep = sheet.getRow(18).getCell(28).getStringCellValue();

        this.date = infoRow.getCell(8).getDateCellValue();
        //TODO: Check proper date

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
        for (int i = 23; i < endRow-2; i++) {
            String part = sheet.getRow(i).getCell(0).getStringCellValue().trim();
            String description = sheet.getRow(i).getCell(5).getStringCellValue().trim();
            double quantity = sheet.getRow(i).getCell(25).getNumericCellValue();
            //fields with quantities
            if (quantity>1) {
                this.parts.add(new Part(part, description, quantity));
            }
            this.parts.add(new Part(part, description));
        }

        //get subtotal
        this.subtotal = sheet.getRow(endRow).getCell(29).getNumericCellValue();
    }

    private void calculateCost() {
        ArrayList<ConfigItem> items = IFCommissions.getConfigManager().getItems();
        for (Part part : parts) {
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
                System.out.println("error: part not found: " + part.getId() + ". add to config file or part will be ignored.");
                boolean exists = false;
                for (Part p: notFound) {
                    if (p.getId().equals(part.getId())) exists = true;
                }
                if (!exists) {
                    notFound.add(part);
                }
            }
        }
        System.out.println("cost: "+cost);
    }

    private void calculateCommission() {
        this.profit = this.subtotal - this.cost;
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

    class Part{

        private String id;
        private String description;
        private double quantity;
        private double cost;
        private double totalCost;

        Part(String id, String d) {
            this.id = id;
            this.description = d;
            this.quantity = 1;
            this.cost = 0;
        }

        Part(String id, String d, double quantity) {
            this.id = id;
            this.description = d;
            this.quantity = quantity;
            this.cost = 0;
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
}