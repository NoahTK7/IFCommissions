package com.noahkurrack.IFCommissions;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class IFCommissions {

    private static ArrayList<Contract> contracts;

    public static void main(String[] args) {
        //begin execution
        System.out.println("IFCommissions: initializing");

        contracts = new ArrayList<>();

        Config.loadConfig();

        File directory = new File(Config.inputPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        //create contract object for each file in directory
        if (directory.listFiles() == null){
            //error: no files
            return;
        }

        for (File file: directory.listFiles()) {
            Workbook workbook = null;
            try {
                 workbook = WorkbookFactory.create(file);
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
            contracts.add(new Contract(workbook));
        }

        for (Contract contract: contracts) {
            //output
            System.out.println(contract.getCustomerInfo() + " " + contract.getOrderNum() + " " + contract.getSalesRep());
        }
        System.out.println("IFCommissions: exit");
    }
}