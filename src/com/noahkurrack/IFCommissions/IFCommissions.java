package com.noahkurrack.IFCommissions;

import com.noahkurrack.IFCommissions.UI.CommissionsGUI;
import com.noahkurrack.IFCommissions.data.Contract;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

        configManager = new ConfigManager();

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

    public void run() {
        // TODO: generate file names
        if (spreadsheet) {
            outputFiles.add("spread");
        }
        if (employeeSpreadsheet) {
            outputFiles.add("empSpread");
        }

        gui.getRunView().setStats(outputFiles, activeFiles.size());

        //create contract object for each file in directory
        for (File file: activeFiles) {
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(file);
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
            System.out.println(file.getName());
            Contract contract = new Contract(workbook);
            contracts.add(contract);
            gui.getRunView().submit(contract);
        }
    }

    private void output() {
        //TODO: output

        if (spreadsheet) {
            //
        }
        if (employeeSpreadsheet) {
            //
        }
        //output
        //create workbook, sheet
        for (Contract contract: contracts) {
            //create row for each contract, add to sheet
            System.out.println(contract.getCustomerInfo() + " " + contract.getOrderNum() + " " + contract.getSalesRep());
        }
        //look for directory
        //if none, create directory (from configManager)
        //write workbook to file (name with timestamp)
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
//TODO: wire cost in description