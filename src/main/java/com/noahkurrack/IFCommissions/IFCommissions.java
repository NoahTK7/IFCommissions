/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions;

import com.noahkurrack.IFCommissions.UI.CommissionsGUI;
import com.noahkurrack.IFCommissions.data.Contract;
import com.noahkurrack.IFCommissions.data.Part;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

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

        for (File file : activeFiles) {
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(file);
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
            //System.out.println(file.getName());
            try {
                Contract contract = new Contract(workbook, file);
                contracts.add(contract);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        contracts.sort(Comparator.comparing(Contract::getDate));

        gui.getOptionsView().populateTable(contracts);
    }

    public void run() {
        if (spreadsheet) {
            String fileName = "contracts.xlsx";
            outputFiles.add(fileName);
        }

        //employee spreadsheets generated in output function

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
        try {
            output();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Contract.displayPartsNotFound();
    }

    //probably could be implemented better but it works
    private void output() throws IOException, InvalidFormatException {
        if (outputDirectory == null) {
            //no output
            return;
        }

        String timestamp = new SimpleDateFormat("EEE, MMM d, HH:mm").format(new Date());
        String dirName = "/contract data " + timestamp;
        this.outputDirectory = new File(this.outputDirectory.getCanonicalPath()+dirName);

        if (!this.outputDirectory.isDirectory()) {
            this.outputDirectory.mkdir();
        }

        ArrayList<Row> detailTemplate = new ArrayList<>();

        if (spreadsheet) {
            InputStream templateStream = IFCommissions.class.getClassLoader().getResourceAsStream("contract_detail_template.xlsx");
            Workbook workbook = WorkbookFactory.create(templateStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row aSheet : sheet) {
                detailTemplate.add(aSheet);
            }

            XSSFWorkbook newWorkbook = new XSSFWorkbook();
            XSSFSheet newSheet = newWorkbook.createSheet("Commissions");
            CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
            cellCopyPolicy.setCopyCellStyle(false);
            cellCopyPolicy.setCopyCellFormula(false);

            //create empty row at top to trick copyRowFrom function
            String timeGenerated = new SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm:ss a").format(new Date());
            newSheet.createRow(0).createCell(0).setCellValue("Generated: "+timeGenerated);
            newSheet.getRow(0).createCell(1);
            newSheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
            int rowIndex = 1;
            for (Contract contract: contracts) {
                newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(0), cellCopyPolicy);
                newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getCustomerInfo());
                rowIndex++;

                newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(1), cellCopyPolicy);
                newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getSalesRep());
                rowIndex++;

                CellStyle tableHeader = newWorkbook.createCellStyle();
                tableHeader.setBorderBottom(BorderStyle.THIN);

                newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(2), cellCopyPolicy);
                for (Cell cell : newSheet.getRow(rowIndex)) {
                    cell.setCellStyle(tableHeader);
                }
                rowIndex++;

                CellStyle dollarStyle = newWorkbook.createCellStyle();
                dollarStyle.setDataFormat((short) 8);
                CellStyle percentageStyle = newWorkbook.createCellStyle();
                percentageStyle.setDataFormat((short) 9);

                for (Part part : contract.getParts()) {
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
                newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getCommissionPercent() / 100);
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

            System.out.println("Writing detail file...");
            String path = new File(outputDirectory.getCanonicalPath(), outputFiles.get(0)).getCanonicalPath();
            FileOutputStream outputStream = new FileOutputStream(path);
            newWorkbook.write(outputStream);
            newWorkbook.close();
        }

        if (employeeSpreadsheet) {
            ArrayList<String> employees = new ArrayList<>();
            for (Contract contract : contracts) {
                if (!employees.contains(contract.getSalesRep())){
                    employees.add(contract.getSalesRep());
                }
            }

            ArrayList<String> fileNames = new ArrayList<>();
            for (String rep : employees) {
                fileNames.add("contracts "+rep+".xlsx");
            }

            for (String rep : employees) {
                InputStream templateStream = IFCommissions.class.getClassLoader().getResourceAsStream("contract_detail_template.xlsx");
                Workbook workbook = WorkbookFactory.create(templateStream);
                Sheet sheet = workbook.getSheetAt(0);

                for (Row aSheet : sheet) {
                    detailTemplate.add(aSheet);
                }

                XSSFWorkbook newWorkbook = new XSSFWorkbook();
                XSSFSheet newSheet = newWorkbook.createSheet("Commissions");
                CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
                cellCopyPolicy.setCopyCellStyle(false);
                cellCopyPolicy.setCopyCellFormula(false);

                //create empty row at top to trick copyRowFrom function
                String timeGenerated = new SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm:ss a").format(new Date());
                newSheet.createRow(0).createCell(0).setCellValue("Generated: " + timeGenerated);
                newSheet.getRow(0).createCell(1);
                newSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
                int rowIndex = 1;
                for (Contract contract : contracts) {
                    if (!contract.getSalesRep().equals(rep)) continue;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(0), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getCustomerInfo());
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(1), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getSalesRep());
                    rowIndex++;

                    CellStyle tableHeader = newWorkbook.createCellStyle();
                    tableHeader.setBorderBottom(BorderStyle.THIN);

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(2), cellCopyPolicy);
                    for (Cell cell : newSheet.getRow(rowIndex)) {
                        cell.setCellStyle(tableHeader);
                    }
                    rowIndex++;

                    CellStyle dollarStyle = newWorkbook.createCellStyle();
                    dollarStyle.setDataFormat((short) 8);
                    CellStyle percentageStyle = newWorkbook.createCellStyle();
                    percentageStyle.setDataFormat((short) 9);

                    for (Part part : contract.getParts()) {
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
                    newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getCommissionPercent() / 100);
                    newSheet.getRow(rowIndex).getCell(3).setCellStyle(percentageStyle);
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(8), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getCommission());
                    newSheet.getRow(rowIndex).getCell(3).setCellStyle(dollarStyle);
                    rowIndex++;

                    //blank row
                    newSheet.createRow(rowIndex);
                    rowIndex++;
                }

                System.out.println("Writing employee ("+rep+") file...");
                String file = fileNames.get(employees.indexOf(rep));
                String path = new File(outputDirectory.getCanonicalPath(), file).getCanonicalPath();
                FileOutputStream outputStream = new FileOutputStream(path);
                newWorkbook.write(outputStream);
                newWorkbook.close();
            }
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
//TODO: sort files alphabetically in setup view;
//TODO: add config for handwork, driveway