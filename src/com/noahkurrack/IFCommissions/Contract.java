package com.noahkurrack.IFCommissions;

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

    public Contract(Workbook wb) {
        this.parts = new ArrayList<>();

        this.workbook = wb;
        extractData();

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
            if (sheet.getRow(i).getCell(0).getStringCellValue().equalsIgnoreCase("Date")){
                endRow = i;
            }
        }
        //get parts
        for (int i = 23; i < endRow-2; i++) {
            this.parts.add(sheet.getRow(i).getCell(0).getStringCellValue());
            System.out.print(sheet.getRow(i).getCell(0).getStringCellValue() + " ");
        }

        //get subtotal
        this.subtotal = sheet.getRow(endRow).getCell(29).getNumericCellValue();
        System.out.print(subtotal + "\n");
    }

    private double calculateCommission() {
        int finalCommission = 0;
        //algorithm
            //calculate cost
            //calculate markup percentage
            //determine commission percentage
            //calculate commission
        return finalCommission;
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
}