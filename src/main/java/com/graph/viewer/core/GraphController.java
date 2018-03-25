package com.graph.viewer.core;


import com.graph.viewer.model.GraphData;
import com.graph.viewer.model.GraphEdge;
import com.graph.viewer.model.GraphNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;


public class GraphController {
    protected boolean loop = true;
    private GraphData graphData;
    private Viewer graphstreamViewer;

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

    public Graph generateGraph(Graph graph){
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

    public Graph createNewGraph(){
        try{
            Graph graph = new MultiGraph("graph1");
            graph.addAttribute("ui.stylesheet", "url('style.css')");

            graph.addAttribute("ui.quality");
            graph.addAttribute("ui.antialias");
            graph.setAutoCreate(true);
            graph.setStrict(true);
            return graph;
        }
        catch(Exception e){
            System.out.println(" could not create new graph");
            e.printStackTrace();
        }
        return null;
    }

//    private void addViewer(Graph graph){
//        graphstreamViewer = new Viewer(graph,
//                Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
//        graphstreamViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
//        graphstreamViewer.enableAutoLayout();
//
//    }

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
