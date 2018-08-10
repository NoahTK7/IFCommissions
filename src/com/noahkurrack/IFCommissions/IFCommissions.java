package com.noahkurrack.IFCommissions;

import com.noahkurrack.IFCommissions.UI.CommissionsGUI;
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

    private File outputDirectory;

    private ArrayList<File> activeFiles;
    private boolean spreadsheet;
    private boolean employeeSpreadsheet;

    private ArrayList<Contract> contracts;

    public static void main(String[] args) {
        //begin execution
        System.out.println("IFCommissions: initializing!");

        IFCommissions instance = new IFCommissions();
        instance.setInstance(instance);

        // config file
        // check if one exists
        // if does: load; if not, create default

        EventQueue.invokeLater(() -> {
            gui = new CommissionsGUI();
            gui.setVisible(true);
        });

        System.out.println("IFCommissions: gui running");
    }

    public IFCommissions() {
        contracts = new ArrayList<>();

        outputDirectory = new File(".");
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

    public void run() {
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
            gui.getRunView().increaseProgress();
            gui.getRunView().submit(contract);
        }
    }

    private void output() {
        //organization

        // generate file names
        //gui.getRunView().displayOutputFiles();

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
        //if none, create directory (from config)
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

//TODO: parse items costs (config?)