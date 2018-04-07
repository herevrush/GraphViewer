package com.graph.viewer.core;


import com.graph.viewer.model.GraphData;
import com.graph.viewer.model.GraphEdge;
import com.graph.viewer.model.GraphNode;
import com.graph.viewer.ui.graph.GraphPanel;
import com.graph.viewer.utils.StatusUtils;
import org.graphstream.algorithm.Toolkit;
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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
        viewSize = new Dimension(2000, 2000);
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

    public void generateGraph(){
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
                    Edge edge = graph.addEdge(graphEdge.getName(),graphEdge.getSource().getName(), graphEdge.getTarget().getName(),true);
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
//            return graph;
        }
        catch(Exception e){
            System.out.println(" could not generateGraph");
            e.printStackTrace();
        }
//        return null;
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

    public void visualize(double zoomLevel){
        graphStreamViewer = new Viewer(graph,
                Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
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
//        viewPanel.setMinimumSize(new Dimension(1000,1000));
        viewPanel.setPreferredSize(viewSize);
        viewPanel.setMaximumSize(viewSize);
//        viewPanel.setEnabled(false);
        viewPanel.getCamera().setAutoFitView(true);
        viewPanel.getCamera().setViewPercent(zoomLevel);
        viewPanel.getCamera().setViewCenter(0,0,0);

        viewerPipe = graphStreamViewer.newViewerPipe();
        viewerPipe.addViewerListener(this);
        viewerPipe.addSink(graph);
//        viewPanel.resizeFrame(1500,1000);
//        viewPanel.setMouseManager(new GraphMouseManager(viewerPipe));
//        System.out.println(viewPanel.getCamera().getMetrics().getSize());
    }

    public ViewerPipe getViewerPipe() {
        return viewerPipe;
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
//            ((DefaultView)graphStreamViewer.getDefaultView()).freezeElement(node);
            double[] xyz = Toolkit.nodePosition(graph, node.getId());
            graphStreamViewer.getDefaultView().getCamera().setViewCenter(xyz[0],xyz[1],xyz[2]);
//            ((DefaultView)graphStreamViewer.getDefaultView()).(xyz[0],);
//            node.addAttribute("layout.frozen", new Object[0]);
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

//    public void componentHidden(ComponentEvent e) {
//        if(this.graphStreamViewer != null){
//            this.graphStreamViewer.getDefaultView().repaint();
//        }
//
//    }
//
//    public void componentMoved(ComponentEvent e) {
//        if(this.graphStreamViewer != null){
//            this.graphStreamViewer.getDefaultView().repaint();
//        }
//    }
//
//    public void componentResized(ComponentEvent e) {
//        if(this.graphStreamViewer != null){
//            this.graphStreamViewer.getDefaultView().repaint();
//        }
//
//    }
//
//    public void componentShown(ComponentEvent e) {
//        if(this.graphStreamViewer != null){
//            this.graphStreamViewer.getDefaultView().repaint();
//        }
//    }

    class GraphMouseListener implements MouseInputListener{

        @Override
        public void mouseClicked(MouseEvent e) {
//            System.out.println(" mouse mouseClicked");
//            viewerPipe.pump();

        }

        @Override
        public void mousePressed(MouseEvent e) {
//            System.out.println(" mouse mousePressed");
            try{
                graphStreamViewer.getDefaultView().requestFocus();
//                System.out.println(graphStreamViewer.getDefaultView().getSize());
                viewerPipe.pump();
            }
            catch (Exception ex){
//                ex.printStackTrace();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
//            System.out.println(" mouse released");
            try{
                graphStreamViewer.getDefaultView().requestFocus();
                viewerPipe.pump();
            }
            catch (Exception ex){
//                ex.printStackTrace();
            }

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





}
