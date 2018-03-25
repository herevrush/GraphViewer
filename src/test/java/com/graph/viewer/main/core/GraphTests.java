package com.graph.viewer.main.core;

import com.graph.viewer.core.GraphController;
import com.graph.viewer.core.GraphImporter;
import com.graph.viewer.model.GraphData;
import com.graph.viewer.ui.GraphViewer;
import com.graph.viewer.ui.graph.GraphPanel;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.File;
import java.util.Objects;

public class GraphTests {

    private GraphController graphController;
//    @Resource(name = "node.csv")
//    private File myNodesFile;
//
//    @Resource(name = "edges.csv")
//    private File myEdgeFile;


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        graphController = new GraphController(null);

        ClassLoader classLoader = this.getClass().getClassLoader();
        File myNodesFile = new File(Objects.requireNonNull(classLoader.getResource("node.csv")).getFile());
        Assert.assertTrue(myNodesFile.exists());
        File myEdgeFile = new File( classLoader.getResource("edges.csv").getFile());//resourcesDirectory.getAbsolutePath(),"edges.csv");
        GraphImporter importer = GraphImporter.getInstance();
        graphController.addNodes(myNodesFile.getAbsolutePath());
        graphController.addEdges(myEdgeFile.getAbsolutePath());
    }

    @Test
    public void testCreateGraph(){
         graphController.createNewGraph();
            Graph graph = graphController.getGraph();
        Assert.assertNotNull(graph);
        Assert.assertEquals("graph1", graph.getId());
        Assert.assertEquals(graph.getAttribute("ui.quality"),true);
        Assert.assertEquals(graph.getAttribute("ui.antialias"),true);
        Assert.assertEquals(graph.getAttribute("ui.stylesheet"),"url('style.css')");
        graph.getNodeCount();
        graph.getEdgeCount();


    }

    @Test
    public void testGraphViewer(){
        graphController.createNewGraph();
        graphController.visualize(0.8);
        Viewer viewer = graphController.getGraphStreamViewer();

        Assert.assertNotNull(viewer);
        Assert.assertNotNull(graphController.getLayout());
        ViewPanel vp = graphController.getGraphStreamViewer().getDefaultView();
        Assert.assertNotNull(vp);
        Assert.assertNotNull(graphController.getViewerPipe());
    }

    @Test
    public void testGraphLoading(){
        graphController.createNewGraph();
        graphController.visualize(0.8);
        graphController.generateGraph();
        Graph graph = graphController.getGraph();

        Assert.assertNotNull(graph);

        Assert.assertEquals(7, graph.getNodeCount());
        Assert.assertEquals(4, graph.getEdgeCount());

        Node node = graph.getNode("4891");
        Assert.assertNotNull(node);

        Assert.assertEquals(3, node.getDegree());
        node.getEachEnteringEdge().forEach(edge -> {
            System.out.println(edge.getSourceNode().getId());
            System.out.println(edge.getTargetNode().getId());
        });

        Edge edge = graph.getEdge("4818-4891");
        Assert.assertNotNull(edge);
        Assert.assertEquals("4818-4891", edge.getId());
        Assert.assertEquals("4818", edge.getSourceNode().getId());
        Assert.assertEquals("4891", edge.getTargetNode().getId());

//        Assert.assertEquals(3, node.getDegree());


    }
}
