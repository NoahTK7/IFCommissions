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

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

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
                if (!contract.isFailed()) {
                    contracts.add(contract);
                }
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
//            try {
//                Thread.sleep(100); //lol want to see progress bar
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        try {
            new OutputManager(contracts, outputFiles, outputDirectory, spreadsheet, employeeSpreadsheet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Contract.displayPartsNotFound();
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
//TODO: add summary table to employee spreadsheet