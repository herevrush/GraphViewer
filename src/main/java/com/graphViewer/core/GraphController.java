package com.graphViewer.core;


import com.graphViewer.model.GraphData;
import com.graphViewer.model.GraphEdge;
import com.graphViewer.model.GraphNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;


public class GraphController implements ViewerListener, MouseInputListener {
    protected boolean loop = true;
    private GraphData graphData;
    private Viewer graphstreamViewer;
    private ViewerPipe viewerPipe;
    private static  String styleSheet = "graph { fill-color: #D0CDFF; }"
            + "node { size: 3px; fill-mode: dyn-plain; " +
            "fill-color: #BB3B13; text-color: #BB3B13; text-alignment: under; text-background-mode: rounded-box; " +
            "text-background-color: #565656; text-padding: 1px, 4px; text-offset: 0px, 5px; }"
            + "node.current { fill-color: #ffc438; }"
            + "edge { fill-color: #141EA1; text-color: #141EA1; text-alignment: under; arrow-size: 3px, 2px; " +
            "text-background-mode: rounded-box; text-background-color: #565656; " +
            "text-padding: 2px, 2px; text-offset: 0px, 5px; }"
            + "edge.loop { text-alignment: left; text-background-mode: rounded-box; " +
            "text-background-color: #565656; text-padding: 5px, 4px; text-offset: 20px, -25px; }";

    public GraphController(){
        graphData = new GraphData();

    }

    public int addNodes(String fileName){
        GraphImporter.getInstance().importNodesFromCSV(fileName,graphData);
        return graphData.getNodes().keySet().size();
    }


    public int addEdges(String fileName){
        GraphImporter.getInstance().importEdgesFromCSV(fileName, graphData);
        return graphData.getEdges().keySet().size();
    }

    public GraphData getGraphData() {
        return graphData;
    }

    public Graph createNewGraph(){
        try{
            Graph graph = new MultiGraph("graph1");
            graph.addAttribute("ui.stylesheet", styleSheet);
            graph.addAttribute("ui.quality");
            graph.addAttribute("ui.antialias");
            graph.setAutoCreate(true);
            graph.setStrict(false);
            if(graphData.getNodes() != null){
                for (GraphNode n:graphData.getNodes().values()) {
                    Node node = graph.addNode(n.getName());
                    node.setAttribute("ui.label",n.getName());
//                    if(n.getProperties() != null &&  n.getProperties().size() > 0){
//                        n.getProperties().forEach(( k,v) ->{
//                            node.setAttribute((String)k,(String)v);
//                        });
//                    }
                }
            }

            if(graphData.getEdges() != null){
                for (GraphEdge graphEdge:graphData.getEdges().values()) {
                    Edge edge = graph.addEdge(graphEdge.getName(),graphEdge.getSource().getName(), graphEdge.getTarget().getName());
                    edge.setAttribute("ui.label",graphEdge.getName());
//                    if(graphEdge.getProperties() != null &&  graphEdge.getProperties().size() > 0){
//                        graphEdge.getProperties().forEach(( k,v) ->{
//                            edge.setAttribute((String)k,(String)v);
//                        });
//                    }
                }

            }
            return graph;
        }
        catch(Exception e){
            System.out.println(" could not create new graph");
        }
        return null;
    }

    private void addViewer(Graph graph){
        graphstreamViewer = new Viewer(graph,
                Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        graphstreamViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
        graphstreamViewer.enableAutoLayout();

    }

    public Viewer generateGraph(){
        try{
            Graph graph = createNewGraph();
            if(graph != null){
                graphstreamViewer = new Viewer(graph,
                        Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
                graphstreamViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
                graphstreamViewer.enableAutoLayout();
            }


//            Viewer viewer = graph.display();





            ViewPanel viewPanel= graphstreamViewer.addDefaultView(false);

            viewPanel.addMouseListener(this);
            System.out.println("size is :: " + viewPanel.getSize());;
            viewPanel.setSize(1200, 1000);
            viewPanel.getCamera().setViewCenter(0,0,0);

            viewerPipe = graphstreamViewer.newViewerPipe();
            viewerPipe.addViewerListener(this);

//            graphstreamView.getCamera().setViewPercent(0.5);
            viewerPipe.addSink(graph);

            System.out.println(graphstreamViewer.getView("graph1"));
//            viewerPipe.pump();


//            graphstreamView.getCamera().setViewPercent(0.5);
//            Panel panel = new Panel(new BorderLayout()) {
//                public void update(java.awt.Graphics g) {
//                    /* Do not erase the background */
//                    paint(g);
//                }
//                @Override
//                public Dimension getPreferredSize() {
//                    return new Dimension(1200, 1000);
//                }
//            };

//            JRootPane root = new JRootPane();

//            root.setSize(1200, 1000);
//            panel.add(root);
//            panel.setBackground(Color.cyan);
//            panel.setSize(1200, 1000);
//            root.getContentPane().add(graphstreamView);

//            frame.add(graphstreamView);
//            frame.setSize(1200,1000);
//            frame.pack();
//            frame.setVisible(true);
            return graphstreamViewer;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Viewer getGraphstreamViewer() {
        return graphstreamViewer;
    }



    public void viewClosed(String id) {
        loop = false;
        graphstreamViewer.getDefaultView().removeMouseListener(this);
    }

    public void buttonPushed(String id) {
        System.out.println("Button pushed on node "+id);
    }

    public void buttonReleased(String id) {
        System.out.println("Button released on node "+id);
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        viewerPipe.pump();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
