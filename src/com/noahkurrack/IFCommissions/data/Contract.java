package com.noahkurrack.IFCommissions.data;

import com.noahkurrack.IFCommissions.IFCommissions;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

public class Contract {

    private Workbook workbook;

    private String customerInfo;
    private long orderNum;
    private String salesRep;

    private ArrayList<String> parts;
    private double subtotal;

    private double cost;

    private double profit;
    private double profitRatio;
    private double commissionPercent;

    private double commission;

    public Contract(Workbook wb) {
        this.parts = new ArrayList<>();
        this.commission = -1;
        this.cost = 0;

        this.workbook = wb;
        extractData();

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
        System.out.println("cost: " + cost);
    }

    private void calculateCommission() {
        int finalCommission = 0;
        // TODO: commission algorithm
        // algorithm
            //calculate cost
            //calculate markup percentage
            //determine commission percentage
            //calculate commission
        this.commission = finalCommission;
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

    public double getProfitRatio() {
        return profitRatio;
    }

    public double getCommission() {
        return commission;
    }
}