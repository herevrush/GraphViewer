package com.graph.viewer.core;


import com.graph.viewer.model.GraphData;
import com.graph.viewer.model.GraphEdge;
import com.graph.viewer.model.GraphNode;
import com.graph.viewer.ui.graph.GraphPanel;
import com.graph.viewer.utils.StatusUtils;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.basicRenderer.SwingBasicGraphRenderer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;


public class GraphController implements ViewerListener{
//    protected boolean loop = true;
    private GraphData graphData;
    private Viewer graphStreamViewer;
    private Dimension viewSize;
    private ViewerPipe viewerPipe;
    private Graph graph;

    private boolean loop = true;

    private GraphPanel graphPanel;
    private Layout layout;
    public GraphController(GraphPanel graphPanel){
        this.graphPanel = graphPanel;
        graphData = new GraphData();

    }

    public int addNodes(String fileName){
        GraphImporter.getInstance().importNodesFromCSV(fileName,graphData);
        return graphData.getNodes().keySet().size();
    }


    public int addEdges(String fileName) throws Exception {
        GraphImporter.getInstance().importEdgesFromCSV(fileName, graphData);
        return graphData.getEdges().keySet().size();
    }

    public GraphData getGraphData() {
        return graphData;
    }

    public Graph generateGraph(){
        try{
            if(graphData.getNodes() != null){
                for (GraphNode n:graphData.getNodes().values()) {
                    Node node = graph.addNode(n.getName());
                    if(node != null){
                        node.setAttribute("ui.label",n.getName());
                    }
                }
            }

            if(graphData.getEdges() != null){
                for (GraphEdge graphEdge:graphData.getEdges().values()) {
                    Edge edge = graph.addEdge(graphEdge.getName(),graphEdge.getSource().getName(), graphEdge.getTarget().getName());
                    if(edge != null){
                    StringBuilder edgeProp = new StringBuilder();
                    if(graphEdge.getProperties() != null &&  graphEdge.getProperties().size() > 0){
                        graphEdge.getProperties().forEach(( k,v) ->{
//                            System.out.println(edgeProp.toString());
                            edgeProp.append(v);
//                            edge.setAttribute((String)k,(String)v);
                        });
                    }
//                    System.out.println(edgeProp.toString());
//                        edge.setAttribute("ui.label",edgeProp.toString());
                    }
                    else{
                        System.out.println(graphEdge.getName());
                    }
                }

            }
            return graph;
        }
        catch(Exception e){
            System.out.println(" could not generateGraph");
            e.printStackTrace();
        }
        return null;
    }
    private void setSelectedNode(final Node newSelectedNode) {


        System.out.println( "Selected Node : " +  newSelectedNode.getId());
        if(newSelectedNode != null) {
            String previousNode = graphData.getSelectedNode();
            if (previousNode != null) {
                Node selected = graph.getNode(String.valueOf(previousNode));
                if (selected != null) {
                    //remove previous node
                    if (selected.hasAttribute("ui.selected")) {
                        selected.removeAttribute("ui.selected");
                    }
                }
            }
            if (!newSelectedNode.hasAttribute("ui.selected")) {
                newSelectedNode.addAttribute("ui.selected", new Object[0]);
            }
            graphData.setSelectedNode(newSelectedNode.getId());
            graphPanel.updateInfo(newSelectedNode, graphData);

        }

    }
    public Graph getGraph() {
        return graph;
    }

    public Layout getLayout() {
        return layout;
    }

    public void createNewGraph(){
        try{
            graph = new MultiGraph("graph1");
            graph.addAttribute("ui.stylesheet", "url('style.css')");

            graph.addAttribute("ui.quality");
            graph.addAttribute("ui.antialias");
            graph.setAutoCreate(true);
            graph.setStrict(true);

        }
        catch(Exception e){
            System.out.println(" could not create new graph");
            e.printStackTrace();
        }

    }

    public void visualize(int zoomLevel){
        graphStreamViewer = new Viewer(graph,
                Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        graphStreamViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
//        graphStreamViewer.enableAutoLayout();

        layout = new SpringBox(false);

        graph.addSink(layout);
        layout.addAttributeSink(graph);
        layout.setQuality(0.7);

//        graphStreamViewer.enableAutoLayout(layout);

        DefaultView viewPanel = new DefaultView(graphStreamViewer, Viewer.DEFAULT_VIEW_ID,
                new SwingBasicGraphRenderer());
        graphStreamViewer.addView(viewPanel);
        //        viewPanel = graphStreamViewer.addDefaultView(false);
        viewPanel.addMouseListener(new GraphMouseListener());
        viewPanel.setMinimumSize(viewSize);
        viewPanel.setPreferredSize(viewSize);
        viewPanel.setMaximumSize(viewSize);
//        viewPanel.setEnabled(false);
        viewPanel.getCamera().setViewPercent((double)zoomLevel/10);
        viewPanel.getCamera().setViewCenter(0,0,0);

        viewerPipe = graphStreamViewer.newViewerPipe();
        viewerPipe.addViewerListener(this);
        viewerPipe.addSink(graph);
//        viewPanel.setMouseManager(new GraphMouseManager(viewerPipe));

    }

    public Viewer getGraphStreamViewer() {
        return graphStreamViewer;
    }

    @Override
    public void viewClosed(String s) {
        loop = false;
        graphStreamViewer.getDefaultView().removeMouseListener(new GraphMouseListener());
    }

    @Override
    public void buttonPushed(String s) {

        System.out.println("Button pushed on node " + s);
        Node node = graph.getNode(s);
        if(node != null){
            node.addAttribute("layout.frozen", new Object[0]);
            setSelectedNode(graph.getNode(s));
        }

    }

    @Override
    public void buttonReleased(String s) {
        System.out.println(" Button released" + s);
        Node node = graph.getNode(s);
        if(node != null){
            node.removeAttribute("layout.frozen");
        }
    }

    class GraphMouseListener implements MouseInputListener{

        @Override
        public void mouseClicked(MouseEvent e) {
//            System.out.println(" mouse mouseClicked");
//            viewerPipe.pump();

        }

        @Override
        public void mousePressed(MouseEvent e) {
            System.out.println(" mouse mousePressed");
            graphStreamViewer.getDefaultView().requestFocus();
            viewerPipe.pump();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            System.out.println(" mouse released");
            graphStreamViewer.getDefaultView().requestFocus();
            viewerPipe.pump();

        }

        @Override
        public void mouseEntered(MouseEvent e) {
//        System.out.println(" mouse mouseEntered");

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
//            System.out.println(" mouse mouseDragged");

        }

        @Override
        public void mouseMoved(MouseEvent e) {
//            System.out.println(" mouse mouseMoved");

        }
    }

//    public Viewer generateGraph(Graph graph){
//        try{
//            Graph graph = createNewGraph();
//            if(graph != null){
//                graphstreamViewer = new Viewer(graph,
//                        Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
//                graphstreamViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
//                graphstreamViewer.enableAutoLayout();
//            }
//
//
////            Viewer viewer = graph.display();
//
//
//
//
//
//            ViewPanel viewPanel= graphstreamViewer.addDefaultView(false);
//
//            viewPanel.addMouseListener(this);
//            System.out.println("size is :: " + viewPanel.getSize());
//            viewPanel.setSize(1200, 1000);
//            viewPanel.getCamera().setViewCenter(0,0,0);
//
//            viewerPipe = graphstreamViewer.newViewerPipe();
//            viewerPipe.addViewerListener(this);
//
////            graphstreamView.getCamera().setViewPercent(0.5);
//            viewerPipe.addSink(graph);
//
//            System.out.println(graphstreamViewer.getView("graph1"));
////            viewerPipe.pump();
//
//
////            graphstreamView.getCamera().setViewPercent(0.5);
////            Panel panel = new Panel(new BorderLayout()) {
////                public void update(java.awt.Graphics g) {
////                    /* Do not erase the background */
////                    paint(g);
////                }
////                @Override
////                public Dimension getPreferredSize() {
////                    return new Dimension(1200, 1000);
////                }
////            };
//
////            JRootPane root = new JRootPane();
//
////            root.setSize(1200, 1000);
////            panel.add(root);
////            panel.setBackground(Color.cyan);
////            panel.setSize(1200, 1000);
////            root.getContentPane().add(graphstreamView);
//
////            frame.add(graphstreamView);
////            frame.setSize(1200,1000);
////            frame.pack();
////            frame.setVisible(true);
//            return graphstreamViewer;
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public Viewer getGraphstreamViewer() {
//        return graphstreamViewer;
//    }




}
