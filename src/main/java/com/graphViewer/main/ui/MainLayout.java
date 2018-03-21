/**
 * Main Layout for the UI.
 */
package com.graphViewer.main.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.graphstream.ui.swingViewer.ViewPanel;

import java.awt.*;
import java.awt.Event;
import java.util.logging.Logger;

public class MainLayout {
    private final static Logger LOGGER = Logger.getLogger(MainLayout.class.getName());
    private Display display;
    private Shell shell;
    private Button loadNodesBtn;
    private Button loadEdgesBtn;
    private Button loadbtn;
    private Frame graphFrame;
    private ViewPanel graphStreamView;
    private ScrolledComposite scrolledComponent;
    private Button resetBtn;
    private Button zoomInBtn;
    private Button zoomOutBtn;


    public MainLayout(Display display){
        this.display = display;
        this.shell = new Shell(display);
//        org.eclipse.swt.graphics.Color red = display.getSystemColor(SWT.COLOR_RED);
//        Color blue = display.getSystemColor(SWT.COLOR_BLUE);
        init();
        Monitor primary = display.getPrimaryMonitor();

        /** get the size of the screen */
        org.eclipse.swt.graphics.Rectangle bounds = primary.getBounds();


        /** get the size of the window */
        Rectangle rect = shell.getBounds();

        /** calculate the centre */
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        /** set the new location */
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

        /** get the size of the screen */
        org.eclipse.swt.graphics.Rectangle bounds = primary.getBounds();


        /** get the size of the window */
        Rectangle rect = shell.getBounds();

        /** calculate the centre */
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        /** set the new location */
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
    }

    private void initButtonBar(){
        final org.eclipse.swt.widgets.Composite btnComposite = new org.eclipse.swt.widgets.Composite(shell, SWT.NONE);
        btnComposite.setLayout(new GridLayout(8,true));

        loadNodesBtn = new Button(btnComposite, SWT.PUSH);
        loadNodesBtn.setText("Load Nodes");
        loadNodesBtn.addListener(SWT.Selection, event -> {
            System.out.println( "Button was selected" );
        });

        loadEdgesBtn = new Button(btnComposite, SWT.PUSH);
        loadEdgesBtn.setText("Load Edges");
        loadEdgesBtn.addListener(SWT.Selection, event -> {
            System.out.println( "Button was selected" );
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
    }

    private void initGraphUI(){
        scrolledComponent= new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
//        sc1.set
//        GridLayout layout1 = new GridLayout();
        RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = false;
        rowLayout.pack = false;
        rowLayout.justify = true;
        rowLayout.type = SWT.VERTICAL;
        rowLayout.marginLeft = 5;
        rowLayout.marginTop = 5;
        rowLayout.marginRight = 5;
        rowLayout.marginBottom = 5;
        rowLayout.spacing = 0;
        scrolledComponent.setLayout(rowLayout);
//        sc1.setLayout(layout1);
        scrolledComponent.setExpandHorizontal(true);
        scrolledComponent.setExpandVertical(true);
        org.eclipse.swt.widgets.Composite graphComposite = new Composite(scrolledComponent, SWT.NO_BACKGROUND | SWT.EMBEDDED);
//        graphComposite.setLayout(layout1);
        scrolledComponent.setContent(graphComposite);
        try {
            System.setProperty("sun.awt.noerasebackground","true");
        } catch (NoSuchMethodError error) {}

        graphFrame = SWT_AWT.new_Frame(graphComposite);
    }
}
