package com.graphViewer.swing.utils;

import com.graphViewer.model.StatusMessage;
import com.graphViewer.swing.ui.GraphViewer;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    private static UIUtils INSTANCE ;


    private GraphViewer app;

    private UIUtils(){

    }

    public static UIUtils getInstance(){
        if(INSTANCE == null){
            INSTANCE = new UIUtils();
        }
        return INSTANCE;
    }

    public String  openFileDialog(Frame parent, String title){
        FileDialog fd = new FileDialog(parent, title, FileDialog.LOAD);

        fd.setFilenameFilter((dir, name) -> name.endsWith(".csv"));
        fd.setVisible(true);

        if (fd.getFile()!= null){
            String filename = fd.getDirectory() + fd.getFile();
            System.out.println("You chose " + filename);
            return filename;
        }
        System.out.println("You cancelled the choice");
        return null;
    }

    public boolean openMessageDialog(Frame frame, String title, String message){
        JOptionPane pane = new JOptionPane();
        pane.setMessage(message);
        JDialog d = pane.createDialog(null, "title");
        d.setVisible(true);
        int selection = getSelection(pane);
        switch (selection) {
            case JOptionPane.OK_OPTION: {
                System.out.println("OK_OPTION");
                return true;
            }
//            case JOptionPane.CANCEL_OPTION:
            default: {
                System.out.println("CANCEL");
                return false;
            }
        }
    }

    private static int getSelection(JOptionPane optionPane) {
        int returnValue = JOptionPane.CLOSED_OPTION;

        Object selectedValue = optionPane.getValue();
        if (selectedValue != null) {
            Object options[] = optionPane.getOptions();
            if (options == null) {
                if (selectedValue instanceof Integer) {
                    returnValue = ((Integer) selectedValue).intValue();
                }
            } else {
                for (int i = 0, n = options.length; i < n; i++) {
                    if (options[i].equals(selectedValue)) {
                        returnValue = i;
                        break; // out of for loop
                    }
                }
            }
        }
        return returnValue;
    }

}
