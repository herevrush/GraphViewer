package com.graph.viewer.main.model;

import com.graph.viewer.core.GraphImporter;
import com.graph.viewer.model.GraphNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class GraphNodeTest {
    GraphNode node;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        node = Mockito.mock(GraphNode.class);
        when(node.getName()).thenReturn("4421");
        Map<String,String> props = new HashMap<String,String>();
        props.put("dataset","ACM");
        props.put("title","Query by diagram: a graphical environment for querying databases");
        props.put("venue","International Conference on Management of Data");
        props.put("year","1994");
        when(node.getProperties()).thenReturn(props);

    }

    @Test
    public void testNodesImport(){
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

    }

    @After
    public void teardown() {
        node = null;
    }
}
