/*
 * Copyright (c) 2018 Noah Kurrack. All rights reserved.
 * This file is apart of the IFCommissions project.
 * See README for more licensing information.
 */

package com.noahkurrack.IFCommissions;

import com.noahkurrack.IFCommissions.data.Contract;
import com.noahkurrack.IFCommissions.data.Part;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OutputManager {

    private ArrayList<Contract> contracts;

    private File outputDirectory;
    private ArrayList<String> outputFiles;

    private boolean spreadsheet;
    private boolean employeeSpreadsheet;

    public OutputManager(ArrayList<Contract> contracts, ArrayList<String> outputFiles, File outputDirectory, boolean spreadsheet, boolean employeeSpreadsheet) throws IOException, InvalidFormatException {
        this.contracts = contracts;
        this.outputDirectory = outputDirectory;
        this.outputFiles = outputFiles;
        this.spreadsheet = spreadsheet;
        this.employeeSpreadsheet = employeeSpreadsheet;

        output();
    }

    //probably could be implemented better but it works
    private void output() throws IOException, InvalidFormatException {
        if (outputDirectory == null) {
            //no output
            return;
        }

        String timestamp = new SimpleDateFormat("EEE, MMM d, HH_mm").format(new Date());
        String dirName = File.separator + "contract data " + timestamp;
        this.outputDirectory = new File(this.outputDirectory.getCanonicalPath() + dirName);

        if (!this.outputDirectory.isDirectory()) {
            this.outputDirectory.mkdir();
        }

        ArrayList<Row> detailTemplate = new ArrayList<>();
        ArrayList<Row> summaryTemplate = new ArrayList<>();

        //details spreadsheet
        if (spreadsheet) {
            InputStream detailTemplateStream = IFCommissions.class.getClassLoader().getResourceAsStream("contract_detail_template.xlsx");
            Workbook detailWorkbook = WorkbookFactory.create(detailTemplateStream);
            Sheet detailSheet = detailWorkbook.getSheetAt(0);

            for (Row aRow : detailSheet) {
                detailTemplate.add(aRow);
            }

            InputStream summaryTemplateStream = IFCommissions.class.getClassLoader().getResourceAsStream("contract_summary_template.xlsx");
            Workbook summaryWorkbook = WorkbookFactory.create(summaryTemplateStream);
            Sheet summarySheet = summaryWorkbook.getSheetAt(0);

            for (Row aRow : summarySheet) {
                summaryTemplate.add(aRow);
            }

            XSSFWorkbook newWorkbook = new XSSFWorkbook();
            XSSFSheet newSheet = newWorkbook.createSheet("Commissions");
            CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
            cellCopyPolicy.setCopyCellStyle(false);
            cellCopyPolicy.setCopyCellFormula(false);

            //cell styles
            CellStyle tableHeader = getCellStyle(newWorkbook, true, false, -1, (short) -1);
            CellStyle title = getCellStyle(newWorkbook, false, true, -1, IndexedColors.GREY_25_PERCENT.getIndex());

            CellStyle dollarStyle = getCellStyle(newWorkbook, false, false, 8, (short) -1);
            CellStyle totalStyle = getCellStyle(newWorkbook, false, true, 8, (short) -1);
            CellStyle percentageStyle = getCellStyle(newWorkbook, false, false, 9, (short) -1);

            int rowIndex = 0; //(1st)

            //create empty row at top to trick copyRowFrom function
            //summary title
            newSheet.createRow(rowIndex).createCell(0).setCellValue("Summary");
            newSheet.getRow(rowIndex).getCell(0).setCellStyle(title);
            for (int i = 1; i < 5; i++) {
                newSheet.getRow(rowIndex).createCell(i);
                newSheet.getRow(rowIndex).getCell(i).setCellStyle(title);
            }
            newSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            rowIndex++; //1 (2nd)

            //table titles
            newSheet.createRow(rowIndex).copyRowFrom(summaryTemplate.get(0), cellCopyPolicy);
            for (Cell cell : newSheet.getRow(rowIndex)) {
                cell.setCellStyle(tableHeader);
            }
            rowIndex++; //2 (3rd)

            //manual bc copy messes up that specifc cell for unknown reason
            //newSheet.getRow(0).getCell(1).setCellStyle(title);

            double total = 0;
            for (Contract contract : contracts) {
                newSheet.createRow(rowIndex).copyRowFrom(summaryTemplate.get(1), cellCopyPolicy);
                newSheet.getRow(rowIndex).getCell(0).setCellValue(contract.getCustomerInfo());
                newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getSubtotal());
                newSheet.getRow(rowIndex).getCell(1).setCellStyle(dollarStyle);
                newSheet.getRow(rowIndex).getCell(2).setCellValue(contract.getProfit());
                newSheet.getRow(rowIndex).getCell(2).setCellStyle(dollarStyle);
                newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getCommissionPercent() / 100);
                newSheet.getRow(rowIndex).getCell(3).setCellStyle(percentageStyle);
                newSheet.getRow(rowIndex).getCell(4).setCellValue(contract.getCommission());
                newSheet.getRow(rowIndex).getCell(4).setCellStyle(dollarStyle);
                total += contract.getCommission();
                rowIndex++;
            }

            //total row
            newSheet.createRow(rowIndex).copyRowFrom(summaryTemplate.get(2), cellCopyPolicy);
            Cell totalCell = newSheet.getRow(rowIndex).getCell(4);
            totalCell.setCellValue(total);
            totalCell.setCellStyle(totalStyle);
            rowIndex++;

            //blank row
            newSheet.createRow(rowIndex);
            rowIndex++;

            //detail title
            newSheet.createRow(rowIndex).createCell(0).setCellValue("Detail");
            newSheet.getRow(rowIndex).getCell(0).setCellStyle(title);
            for (int i = 1; i < 4; i++) {
                newSheet.getRow(rowIndex).createCell(i);
                newSheet.getRow(rowIndex).getCell(i).setCellStyle(title);
            }
            newSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));
            rowIndex++;

            for (Contract contract : contracts) {
                newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(0), cellCopyPolicy);
                newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getCustomerInfo());
                rowIndex++;

                newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(1), cellCopyPolicy);
                newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getSalesRep());
                rowIndex++;

                newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(2), cellCopyPolicy);
                for (Cell cell : newSheet.getRow(rowIndex)) {
                    cell.setCellStyle(tableHeader);
                }
                rowIndex++;

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

            //Timestamp at end
            String timeGenerated = new SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm:ss a").format(new Date());
            newSheet.createRow(rowIndex).createCell(0).setCellValue("Generated: " + timeGenerated);
            newSheet.getRow(0).createCell(1);
            newSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
            //TODO: copyright notice

            System.out.println("Writing detail file...");
            String path = new File(outputDirectory.getCanonicalPath(), outputFiles.get(0)).getCanonicalPath();
            FileOutputStream outputStream = new FileOutputStream(path);

            newWorkbook.write(outputStream);
            newWorkbook.close();
        }

        if (employeeSpreadsheet) {
            ArrayList<String> employees = new ArrayList<>();
            for (Contract contract : contracts) {
                if (!employees.contains(contract.getSalesRep())) {
                    employees.add(contract.getSalesRep());
                }
            }

            ArrayList<String> fileNames = new ArrayList<>();
            for (String rep : employees) {
                fileNames.add("contracts " + rep + ".xlsx");
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

                CellStyle tableHeader = getCellStyle(newWorkbook, true, false, -1, (short) -1);
                CellStyle title = getCellStyle(newWorkbook, false, true, -1, IndexedColors.GREY_25_PERCENT.getIndex());

                CellStyle dollarStyle = getCellStyle(newWorkbook, false, false, 8, (short) -1);
                CellStyle totalStyle = getCellStyle(newWorkbook, false, true, 8, (short) -1);
                CellStyle percentageStyle = getCellStyle(newWorkbook, false, false, 9, (short) -1);

                int rowIndex = 0; //(1st)

                //create empty row at top to trick copyRowFrom function
                //summary title
                newSheet.createRow(rowIndex).createCell(0).setCellValue("Summary");
                newSheet.getRow(rowIndex).getCell(0).setCellStyle(title);
                for (int i = 1; i < 5; i++) {
                    newSheet.getRow(rowIndex).createCell(i);
                    newSheet.getRow(rowIndex).getCell(i).setCellStyle(title);
                }
                newSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
                rowIndex++; //1 (2nd)

                //table titles
                newSheet.createRow(rowIndex).copyRowFrom(summaryTemplate.get(0), cellCopyPolicy);
                for (Cell cell : newSheet.getRow(rowIndex)) {
                    cell.setCellStyle(tableHeader);
                }
                rowIndex++; //2 (3rd)

                //manual bc copy messes up that specifc cell for unknown reason
                //newSheet.getRow(0).getCell(1).setCellStyle(title);

                double total = 0;
                for (Contract contract : contracts) {
                    if (!contract.getSalesRep().equals(rep)) continue;

                    newSheet.createRow(rowIndex).copyRowFrom(summaryTemplate.get(1), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(0).setCellValue(contract.getCustomerInfo());
                    newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getSubtotal());
                    newSheet.getRow(rowIndex).getCell(1).setCellStyle(dollarStyle);
                    newSheet.getRow(rowIndex).getCell(2).setCellValue(contract.getProfit());
                    newSheet.getRow(rowIndex).getCell(2).setCellStyle(dollarStyle);
                    newSheet.getRow(rowIndex).getCell(3).setCellValue(contract.getCommissionPercent() / 100);
                    newSheet.getRow(rowIndex).getCell(3).setCellStyle(percentageStyle);
                    newSheet.getRow(rowIndex).getCell(4).setCellValue(contract.getCommission());
                    newSheet.getRow(rowIndex).getCell(4).setCellStyle(dollarStyle);
                    total += contract.getCommission();
                    rowIndex++;
                }

                //total row
                newSheet.createRow(rowIndex).copyRowFrom(summaryTemplate.get(2), cellCopyPolicy);
                Cell totalCell = newSheet.getRow(rowIndex).getCell(4);
                totalCell.setCellValue(total);
                totalCell.setCellStyle(totalStyle);
                rowIndex++;

                //blank row
                newSheet.createRow(rowIndex);
                rowIndex++;

                //detail title
                newSheet.createRow(rowIndex).createCell(0).setCellValue("Detail");
                newSheet.getRow(rowIndex).getCell(0).setCellStyle(title);
                for (int i = 1; i < 4; i++) {
                    newSheet.getRow(rowIndex).createCell(i);
                    newSheet.getRow(rowIndex).getCell(i).setCellStyle(title);
                }
                newSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 3));
                rowIndex++;


                for (Contract contract : contracts) {
                    if (!contract.getSalesRep().equals(rep)) continue;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(0), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getCustomerInfo());
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(1), cellCopyPolicy);
                    newSheet.getRow(rowIndex).getCell(1).setCellValue(contract.getSalesRep());
                    rowIndex++;

                    newSheet.createRow(rowIndex).copyRowFrom(detailTemplate.get(2), cellCopyPolicy);
                    for (Cell cell : newSheet.getRow(rowIndex)) {
                        cell.setCellStyle(tableHeader);
                    }
                    rowIndex++;

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

                //Timestamp at end
                String timeGenerated = new SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm:ss a").format(new Date());
                newSheet.createRow(rowIndex).createCell(0).setCellValue("Generated: " + timeGenerated);
                newSheet.getRow(0).createCell(1);
                newSheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
                //TODO: copyright notice

                System.out.println("Writing employee (" + rep + ") file...");
                String file = fileNames.get(employees.indexOf(rep));
                String path = new File(outputDirectory.getCanonicalPath(), file).getCanonicalPath();
                FileOutputStream outputStream = new FileOutputStream(path);

                newWorkbook.write(outputStream);
                newWorkbook.close();

            }
        }
    }

    private CellStyle getCellStyle(Workbook workbook, boolean isUnderlined, boolean isBold, int dataStyle, short color) {
        CellStyle style = workbook.createCellStyle();
        if (isUnderlined) {
            style.setBorderBottom(BorderStyle.THIN);
        }
        if (isBold) {
            XSSFFont bold = (XSSFFont) workbook.createFont();
            bold.setBold(true);
            style.setFont(bold);
        }
        if (dataStyle != -1) {
            style.setDataFormat((short) dataStyle);
        }
        if (color != -1) {
            style.setFillForegroundColor(color);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }
}
