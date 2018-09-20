package com.noahkurrack.IFCommissions.data;

import com.noahkurrack.IFCommissions.IFCommissions;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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

    private ArrayList<String> parts;
    private double subtotal;

    private double cost;

    private double profit;
    private double commissionPercent;

    private double commission;

    //options
    private boolean selfGenerated;
    private static int numSelfGenerated;

    public Contract(Workbook wb) {
        this.parts = new ArrayList<>();
        this.commission = -1;
        this.cost = 0;

        //options
        selfGenerated = false;
        numSelfGenerated = 0;

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
            this.parts.add(sheet.getRow(i).getCell(0).getStringCellValue().trim());
            //System.out.print(sheet.getRow(i).getCell(0).getStringCellValue() + " ");
        }

        //TODO: wire cost in description

        //get subtotal
        this.subtotal = sheet.getRow(endRow).getCell(29).getNumericCellValue();
        System.out.println("subtotal: " + subtotal);
    }

    private void calculateCost() {
        ArrayList<ConfigItem> items = IFCommissions.getConfigManager().getItems();
        for (String s : parts) {
            boolean found = false;
            for (ConfigItem item : items) {
                if (item.getPart().equals(s)) {
                    cost += item.getCost();
                    found = true;
                }
            }
            if (!found) {
                //TODO: error part not found (ignore but send message)
            }
        }
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

        //options
        if (this.selfGenerated) {
            numSelfGenerated++;
            if (numSelfGenerated>3) {
                commissionPercent += 5;
            } else {
                commissionPercent += 7;
            }
        }

        this.commission = this.profit * (this.commissionPercent/100);
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

    public ArrayList<String> getParts() {
        return parts;
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

    public boolean isSelfGenerated() {
        return selfGenerated;
    }

    public void setSelfGenerated(boolean selfGenerated) {
        this.selfGenerated = selfGenerated;
    }
}