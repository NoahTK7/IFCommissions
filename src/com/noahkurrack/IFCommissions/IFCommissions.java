/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions;

import com.noahkurrack.IFCommissions.UI.CommissionsGUI;
import com.noahkurrack.IFCommissions.data.Contract;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class IFCommissions {

    private static IFCommissions instance;
    private static CommissionsGUI gui;
    private static ConfigManager configManager;

    private File outputDirectory;
    private ArrayList<String> outputFiles;

    private ArrayList<File> activeFiles;
    private boolean spreadsheet;
    private boolean employeeSpreadsheet;

    private ArrayList<Contract> contracts;

    public static void main(String[] args) {
        //begin execution
        System.out.println("IFCommissions: initializing!");

        IFCommissions instance = new IFCommissions();
        instance.setInstance(instance);

        try {
            configManager = new ConfigManager();
        } catch (IOException e) {
            System.out.println("Configuration error. Cannot proceed.");
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            gui = new CommissionsGUI();
            gui.setVisible(true);
        });

        System.out.println("IFCommissions: gui running");
    }

    public IFCommissions() {
        contracts = new ArrayList<>();

        outputDirectory = new File(".");
        outputFiles = new ArrayList<>();
        activeFiles = new ArrayList<>();
        spreadsheet = true;
        employeeSpreadsheet = false;
    }

    private void setInstance(IFCommissions instance) {
        IFCommissions.instance = instance;
    }

    public static IFCommissions getInstance() {
        return instance;
    }

    public static CommissionsGUI getGui() {
        return gui;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public void processContracts() {
        //already populated, user pressed back button
        if (contracts.size()>0) {
            contracts.clear();
        }

        for (File file: activeFiles) {
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(file);
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
            //System.out.println(file.getName());
            Contract contract = new Contract(workbook);
            contracts.add(contract);
        }

        contracts.sort(new Comparator<Contract>() {
            @Override
            public int compare(Contract o1, Contract o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        gui.getOptionsView().populateTable(contracts);
    }

    public void run() {
        // TODO: generate file names (name with timestamp)
        if (spreadsheet) {
            outputFiles.add("detail.xlsx");
        }
        if (employeeSpreadsheet) {
            outputFiles.add("empSpread");
        }

        gui.getRunView().setStats(outputFiles, activeFiles.size());

        //create contract object for each file in directory
        for (Contract contract : contracts) {
            contract.process();
            gui.getRunView().submit(contract);
            try {
                Thread.sleep(100); //lol want to see progress bar
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        output();
        Contract.displayPartsNotFound();
    }

    //TODO: Class?
    private void output() {
        //TODO: output

        if (outputDirectory == null) {
            //no output
            return;
        }

        if (!this.outputDirectory.isDirectory()) {
            this.outputDirectory.mkdir();
        }

        ClassLoader classLoader = this.getClass().getClassLoader();
        ArrayList<Row> detailTemplate = new ArrayList<>();

        if (spreadsheet) {
            try {
                Workbook workbook = WorkbookFactory.create(new File(classLoader.getResource("com/noahkurrack/IFCommissions/assets/contract_detail_template.xlsx").getFile()));
                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> iterator = sheet.iterator();

                while (iterator.hasNext()) {
                    detailTemplate.add(iterator.next());
                }

                XSSFWorkbook newWorkbook = new XSSFWorkbook();
                XSSFSheet newSheet = newWorkbook.createSheet("Commissions");
                CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
                cellCopyPolicy.setCopyCellStyle(false);
                cellCopyPolicy.setCopyCellFormula(false);

                //create empty row at top to trick copyRowFrom function
                newSheet.createRow(0);
                int rowIndex = 1;
                for (Contract contract: contracts) {
                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(0), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getCustomerInfo());
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(1), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getSalesRep());
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(2), cellCopyPolicy);
                    rowIndex++;

                    CellStyle dollarStyle = newWorkbook.createCellStyle();
                    dollarStyle.setDataFormat((short)8);
                    CellStyle percentageStyle = newWorkbook.createCellStyle();
                    percentageStyle.setDataFormat((short)9);

                    //TODO: add new row to contractEntry for each part
                    for (Contract.Part part : contract.getParts()) {
                        newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(3), cellCopyPolicy);
                        newSheet.getRow(rowIndex).getCell(0).setCellValue(part.getId());
                        newSheet.getRow(rowIndex).getCell(1).setCellValue(part.getDescription());
                        newSheet.getRow(rowIndex).getCell(2).setCellValue(part.getQuantity());
                        newSheet.getRow(rowIndex).getCell(3).setCellValue(part.getTotalCost());
                        newSheet.getRow(rowIndex).getCell(3).setCellStyle(dollarStyle);
                        rowIndex++;
                    }

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(4), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getCost());
                    newSheet.getRow(rowIndex).getCell(3).setCellStyle(dollarStyle);
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(5), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getSubtotal());
                    newSheet.getRow(rowIndex).getCell(3).setCellStyle(dollarStyle);
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(6), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getProfit());
                    newSheet.getRow(rowIndex).getCell(3).setCellStyle(dollarStyle);
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(7), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getCommissionPercent()/100);
                    newSheet.getRow(rowIndex).getCell(3).setCellStyle(percentageStyle);
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(8), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getCommission());
                    newSheet.getRow(rowIndex).getCell(3).setCellStyle(dollarStyle);
                    rowIndex++;

                    //blank row
                    newSheet.createRow(rowIndex);
                    rowIndex++;

                    System.out.println(contract.getCustomerInfo() + " " + contract.getOrderNum() + " " + contract.getSalesRep());
                }

                try {
                    System.out.println("Writing detail file...");
                    FileOutputStream outputStream = new FileOutputStream(outputDirectory.getName()+"/"+outputFiles.get(0));
                    newWorkbook.write(outputStream);
                    newWorkbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
        }
        if (employeeSpreadsheet) {
            //TODO: per employee spreadsheet
        }
    }

    public void setOutputDirectory(File directory) {
        this.outputDirectory = directory;
    }

    public void setActiveFiles(ArrayList<File> activeFiles) {
        this.activeFiles = activeFiles;
    }

    public void setSpreadsheet(boolean spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    public void setEmployeeSpreadsheet(boolean employeeSpreadsheet) {
        this.employeeSpreadsheet = employeeSpreadsheet;
    }
}