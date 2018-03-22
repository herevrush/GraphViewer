/**
 * Main Layout for the UI.
 */
package com.graphViewer.main.ui;

import com.graphViewer.main.core.GraphController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.graphstream.ui.swingViewer.ViewPanel;

import java.awt.*;
import java.awt.Event;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public class MainLayout {
    private final static Logger LOGGER = Logger.getLogger(MainLayout.class.getName());
    private Display display;
    private Shell shell;
    private Button loadNodesBtn;
    private Button loadEdgesBtn;
    private Button loadbtn;
    private Frame graphFrame;
    private ScrolledComposite scrolledComponent;
    private Button resetBtn;
    private Button zoomInBtn;
    private Button zoomOutBtn;
    private ProgressBar progressBar;
    private GraphController graphController;
    private Label progressLabel;
    private Label nodesLabel;
    private Label edgesLabel;
    public MainLayout(Display display, GraphController graphController){
        this.graphController = graphController;
        this.display = display;
        this.shell = new Shell(display);
        init();
        Monitor primary = display.getPrimaryMonitor();

        /* get the size of the screen */
        org.eclipse.swt.graphics.Rectangle bounds = primary.getBounds();


        /* get the size of the window */
        Rectangle rect = shell.getBounds();

        /* calculate the centre */
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        /* set the new location */
        shell.setLocation(x, y);

//        sc1.pack();
        scrolledComponent.setMinSize(bounds.width-150 , bounds.height-250 );
        shell.setSize( bounds.width-100, bounds.height-100);
    }

    public Shell getShell() {
        return shell;
    }

    public void showUI(){
        Monitor primary = display.getPrimaryMonitor();

        /* get the size of the screen */
        org.eclipse.swt.graphics.Rectangle bounds = primary.getBounds();


        /* get the size of the window */
        Rectangle rect = shell.getBounds();

        /* calculate the centre */
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        /* set the new location */
        shell.setLocation(x, y);

        shell.setSize( bounds.width-100, bounds.height-100);
        shell.open();
        shell.addListener(SWT.Close, new Listener() {
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                System.out.println(" Closing shell" + shell);
//                event.doit = false;

            }
        });
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        display.dispose();
    }
    private void init(){

        org.eclipse.swt.layout.GridLayout gLayout = new org.eclipse.swt.layout.GridLayout();
        gLayout.numColumns = 1;
        shell.setLayout(gLayout);
        initButtonBar();
        initGraphUI();
        initProgressBar();
    }

    private void initButtonBar(){
        final org.eclipse.swt.widgets.Composite btnComposite = new org.eclipse.swt.widgets.Composite(shell, SWT.NONE);
        btnComposite.setLayout(new GridLayout(8,false));

        loadNodesBtn = new Button(btnComposite, SWT.PUSH);
        loadNodesBtn.setText("Load Nodes");
        loadNodesBtn.addListener(SWT.Selection, event -> {
//            graphController.addNodes();
            String fd = openfileDialog();
            System.out.println(fd);

            if(fd != null){
                Display.getDefault().asyncExec(new Runnable(){
                    public void run(){
                        updateProgress(" Loading Nodes....", 60);
                        int nodes = graphController.addNodes(fd);
                        System.out.println("nodes = " + String.valueOf(nodes));
                        nodesLabel.setText( String.valueOf(nodes));
                        stopProgress(200);
                    }
                });

            }
        });

        loadEdgesBtn = new Button(btnComposite, SWT.PUSH);
        loadEdgesBtn.setText("Load Edges");
        loadEdgesBtn.addListener(SWT.Selection, event -> {
            String fd = openfileDialog();
            System.out.println(fd);

            if(fd != null){
                Display.getDefault().asyncExec(new Runnable(){
                    public void run(){
                        updateProgress(" Loading Edges....", 60);
                        int edges = graphController.addEdges(fd);
                        System.out.println("edges " + edges);
                        edgesLabel.setText( String.valueOf(edges));
                        stopProgress(200);
                    }

                });

            }

        });

        loadbtn = new Button(btnComposite, SWT.PUSH);
        loadbtn.setText("Generate Graph");
        loadbtn.addListener(SWT.Selection, event -> {
            System.out.println( "Button was selected" );
        });

        resetBtn = new Button(btnComposite, SWT.PUSH);
        resetBtn.setText("Reset View");
        resetBtn.addListener(SWT.Selection, event -> {
            System.out.println( "Button was selected" );
        });

        zoomInBtn = new Button(btnComposite, SWT.PUSH);
        zoomInBtn.setText("Zoom In Graph");
        zoomInBtn.addListener(SWT.Selection, event -> {
            System.out.println( "Button was selected" );
        });

        zoomOutBtn = new Button(btnComposite, SWT.PUSH);
        zoomOutBtn.setText("Zoom Out Graph");
        zoomOutBtn.addListener(SWT.Selection, event -> {
            System.out.println( "Button was selected" );
        });

        Composite nodesComposite = new Composite(btnComposite, SWT.BORDER);
        nodesComposite.setLayout(new GridLayout(2,true));
        Label label = new Label(nodesComposite, SWT.NONE);
        label.setText( " Nodes: ");
        nodesLabel = new Label(nodesComposite, SWT.NONE);
        nodesLabel.setText( "            ");

        Composite edgesComposite = new Composite(btnComposite, SWT.BORDER);
        edgesComposite.setLayout(new GridLayout(2,true));
        label = new Label(edgesComposite, SWT.NONE);
        label.setText( " Edges: ");
        edgesLabel = new Label(edgesComposite, SWT.FILL);
        edgesLabel.setText( "              ");
    }

    private void initGraphUI(){
        scrolledComponent= new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        scrolledComponent.setLayoutData(gridData);
        org.eclipse.swt.widgets.Composite graphComposite = new Composite(scrolledComponent, SWT.NO_BACKGROUND | SWT.EMBEDDED);
        scrolledComponent.setContent(graphComposite);
        try {
            System.setProperty("sun.awt.noerasebackground","true");
        } catch (NoSuchMethodError error) {
            LOGGER.severe(" Error in MainLayout::initGraph  - " + error.getLocalizedMessage());
        }

        graphFrame = SWT_AWT.new_Frame(graphComposite);
    }

    private void initProgressBar(){
        Composite progressComposite = new Composite(shell, SWT.BORDER);
        GridData gridData = new GridData(GridData.FILL, GridData.END,false,false);
        progressComposite.setLayout(new GridLayout(1,false));
        progressComposite.setLayoutData(gridData);
        progressLabel = new Label(progressComposite, SWT.NONE);
        gridData = new GridData(GridData.FILL, GridData.FILL,true,false);
        progressLabel.setLayoutData(gridData);
        progressBar = new ProgressBar(progressComposite, SWT.INDETERMINATE);
        gridData = new GridData(GridData.FILL, GridData.FILL,true,true);
        progressBar.setLayoutData(gridData);
        progressLabel.setText(" ...");
        progressBar.setVisible(false);
//        progressBar.setState(SWT.SMOOTH);
//        progressBar.setMinimum(30);
    }
    private String openfileDialog(){
        FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
        String[] filterExt = { "*.csv"};
        fileDialog.setFilterExtensions(filterExt);
//        fileDialog.setFilterPath(System.getProperty("user.home"));
        Collection files = new ArrayList();
        return fileDialog.open();
    }

    private void updateProgress(String message, int i){
        progressBar.setVisible(true);

        progressLabel.setText(message);
        progressBar.setSelection(i);
        progressBar.setMaximum(100);

    }

    private void stopProgress(int wait){
        if(wait > 0){
            Display.getDefault().timerExec(wait, new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisible(false);
                            progressLabel.setText("...");
                }
            });
        }



    }

}
