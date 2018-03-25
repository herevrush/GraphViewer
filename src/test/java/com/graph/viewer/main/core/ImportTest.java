package com.graph.viewer.main.core;

import com.graph.viewer.core.GraphImporter;
import com.graph.viewer.model.GraphData;
import com.graph.viewer.model.GraphEdge;
import com.graph.viewer.model.GraphNode;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;
import java.util.Objects;



public class ImportTest {

    private GraphImporter importer ;

    @Resource(name = "node.csv")
    private File myNodesFile;

    @Resource(name = "edges.csv")
    private File myEdgeFile;

    private GraphData graphData;



    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        File resourcesDirectory = new File("src/test/resources");
        ClassLoader classLoader = this.getClass().getClassLoader();
        myNodesFile = new File(Objects.requireNonNull(classLoader.getResource("node.csv")).getFile());
        Assert.assertTrue(myNodesFile.exists());
        myEdgeFile = new File( classLoader.getResource("edges.csv").getFile());//resourcesDirectory.getAbsolutePath(),"edges.csv");
        importer = GraphImporter.getInstance();
        graphData = new GraphData();

    }

    @Test
    public void test1NodesImport(){

        importer.importNodesFromCSV(myNodesFile.getAbsolutePath(),graphData);

        Assert.assertNotNull(graphData.getNodes());
        Assert.assertEquals(7, graphData.getNodes().size());

        Assert.assertNotNull(graphData.getNodes().get("0"));


        GraphNode node = graphData.getNodes().get("4421");

        Assert.assertEquals("4421",node.getName());
        Assert.assertNotNull(node.getProperties());

        Assert.assertEquals(4,node.getProperties().size());

        Map properties = node.getProperties();
        Assert.assertTrue(properties.containsKey("dataset"));
        Assert.assertTrue(properties.containsKey("title"));
        Assert.assertTrue(properties.containsKey("venue"));
        Assert.assertTrue(properties.containsKey("year"));

        Assert.assertEquals("ACM",properties.get("dataset"));
                Assert.assertEquals("Query by diagram: a graphical environment for querying databases",properties.get("title"));
                Assert.assertEquals("International Conference on Management of Data",properties.get("venue"));
                Assert.assertEquals("1994",properties.get("year"));


        Assert.assertNotNull(graphData.getNodes().get("3"));
        Assert.assertNotNull(graphData.getNodes().get("6"));
        Assert.assertNotNull(graphData.getNodes().get("5302"));

    }
    @Test
    public void test2EdgesImport() throws Exception {
        importer.importNodesFromCSV(myNodesFile.getAbsolutePath(),graphData);
        importer.importEdgesFromCSV(myEdgeFile.getAbsolutePath(),graphData);

        Assert.assertNotNull(graphData.getEdges());
        Assert.assertEquals(4, graphData.getEdges().size());
        Assert.assertNotNull(graphData.getEdges().get("0-5302"));


        GraphEdge edge = graphData.getEdges().get("0-5302");
        Assert.assertEquals("0-5302",edge.getName());
        Assert.assertNotNull(edge.getProperties());
        Assert.assertEquals(1,edge.getProperties().size());

        Assert.assertEquals("0",edge.getSource().getName());
        Assert.assertEquals("5302",edge.getTarget().getName());

        Map properties = edge.getProperties();
        Assert.assertTrue(properties.containsKey("Author"));

        Assert.assertEquals("Gottfried Vossen",properties.get("Author"));


        Assert.assertNotNull(graphData.getEdges().get("4891-4818"));
        Assert.assertNotNull(graphData.getEdges().get("4818-4891"));
        Assert.assertNotNull(graphData.getEdges().get("4891-4421"));

    }


    @Test
    public void testEdgesImportFailure(){
        try{
            importer.importEdgesFromCSV(myEdgeFile.getAbsolutePath(),graphData);
        }
        catch (Exception e){
            Assert.assertEquals("Failed to load edges, nodes not available",e.getMessage());
        }



    }

    @Test
    public void test3GraphData() throws Exception {
        importer.importNodesFromCSV(myNodesFile.getAbsolutePath(),graphData);
        importer.importEdgesFromCSV(myEdgeFile.getAbsolutePath(),graphData);

        Assert.assertNotNull(graphData.getNodes());
        Assert.assertNotNull(graphData.getEdges());
        Assert.assertNotNull(graphData.getFirstNodeName());
        Assert.assertEquals("0",graphData.getFirstNodeName());
        Assert.assertNull(graphData.getSelectedNode());
    }
    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
//        Assert.assertNotNull(graphData);

    }

}
