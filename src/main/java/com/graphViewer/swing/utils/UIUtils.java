package com.graphViewer.swing.utils;

import com.graphViewer.model.StatusMessage;
import com.graphViewer.swing.ui.GraphViewer;

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
        String filename = fd.getDirectory() + fd.getFile();
        if (filename != null){
            System.out.println("You chose " + filename);
            return filename;
        }
        System.out.println("You cancelled the choice");
        return null;
    }


}
