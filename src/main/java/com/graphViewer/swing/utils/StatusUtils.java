package com.graphViewer.swing.utils;

import com.graphViewer.model.StatusMessage;
import com.graphViewer.swing.ui.GraphViewer;

import java.awt.*;

public class StatusUtils {

    private static StatusUtils INSTANCE ;


    private GraphViewer app;

    private StatusUtils(GraphViewer app){
        this.app = app;
    }

    public static StatusUtils getInstance(GraphViewer swingApp){
        if(INSTANCE == null){
            INSTANCE = new StatusUtils(swingApp);
        }
        return INSTANCE;
    }


    public void setErrorStatus(final String message) {
        if (app != null) {
            app.getStatusBar().setMessage(message, StatusMessage.ERROR);
        }
    }

    public  void setEndMessage(final String message) {
        if (app != null) {
            app.getStatusBar().writeEnd(message);
        }
    }

    public  void setInfoStatus(final String message) {
        if (app != null) {
            app.getStatusBar().setMessage(message, StatusMessage.INFO);
        }
    }


}
