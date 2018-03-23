package com.graphViewer.swing.ui;


import com.graphViewer.core.GraphController;
import com.graphViewer.swing.ui.StatusBar;
import com.graphViewer.swing.ui.graph.GraphPanel;
import com.graphViewer.swing.ui.graph.GraphToolBar;
import com.graphViewer.swing.utils.StatusUtils;
import org.graphstream.ui.layout.Layout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

public class GraphViewer extends JFrame implements Observer {


    /** The tool bar of the window. */
    private GraphToolBar toolBar;
    /** The main pane of the window. */
    private GraphPanel graphPanel;

    private GraphController graphController;

    /** The status bar of the window. */
    private StatusBar statusBar;



    private ComponentListener windowListener = new ComponentListener() {
        @Override
        public void componentResized(ComponentEvent e) {
         resized();
        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {

        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }
    };

    /**
     * Initiate a new GraphViewer with default settings.
     */
    public GraphViewer() {
        graphController = new GraphController();
        init();
    }

    public GraphPanel getGraphPanel() {
        return graphPanel;
    }

    public GraphController getGraphController() {
        return graphController;
    }

    private void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        applyWindowSettings();




        statusBar = new StatusBar();
        add(statusBar, BorderLayout.SOUTH);

        toolBar = new GraphToolBar(this);
        add(toolBar, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        graphPanel = new GraphPanel(this);
        panel.add(graphPanel,BorderLayout.CENTER);



        addComponentListener(windowListener);
        addWindowListener(new CloseConfirmationAdapter());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void applyWindowSettings() {
        setTitle("My Graph Visualizer");
        Dimension scrrenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension(scrrenSize.width-100, scrrenSize.height-100) ;
        setSize(size);
        setMinimumSize(size);
        pack();
        revalidate();
    }

    public final void resized() {
        StatusUtils.getInstance(this).setEndMessage(" Size: " + this.getSize().width + " x " + this.getSize().height);
    }



    @Override
    public void update(Observable o, Object arg) {
        applyWindowSettings();
    }

    static final class CloseConfirmationAdapter extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent e) {
            if (JOptionPane.showConfirmDialog(e.getWindow(),
                    "Are you sure you want to close the application?",
                    "Exit DN/App?", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                System.out.println("Bye bye!");
                e.getWindow().dispose();
                System.exit(0);

            }

        }
    }


    /**
     * @return the toolBar
     */
    public final GraphToolBar getToolBar() {
        return toolBar;
    }

    /**
     * @return the statusBar
     */
    public final StatusBar getStatusBar() {
        return statusBar;
    }


}
