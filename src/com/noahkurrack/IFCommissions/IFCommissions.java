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

    private File directory;

    private ArrayList<File> activeFiles;
    private boolean spreadsheet;
    private boolean employeeSpreadsheet;

    private ArrayList<Contract> contracts;

    public static void main(String[] args) {
        //begin execution
        System.out.println("IFCommissions: initializing!");

        IFCommissions instance = new IFCommissions();
        instance.setInstance(instance);

        /*
        Config.loadConfig();

        File directory = new File(Config.inputPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        */

        EventQueue.invokeLater(() -> {
            gui = new CommissionsGUI();
            gui.setVisible(true);
        });

        System.out.println("IFCommissions: gui running");
    }

    public IFCommissions() {
        contracts = new ArrayList<>();

        directory = new File(".");
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
        if (directory.listFiles() == null){
            //error: no files
            return;
        }
        for (File file: activeFiles) {
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(file);
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
            System.out.println(file.getName());
            contracts.add(new Contract(workbook));
        }
    }

    private void output() {
        //organization
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

    public void setDirectory(File directory) {
        this.directory = directory;
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