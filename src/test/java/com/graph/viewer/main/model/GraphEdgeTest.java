package com.graph.viewer.main.model;

import com.graph.viewer.model.GraphEdge;
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

public class GraphEdgeTest {
    GraphEdge edge;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        GraphNode node1 = Mockito.mock(GraphNode.class);
        when(node1.getName()).thenReturn("0");

        GraphNode node2 = Mockito.mock(GraphNode.class);
        when(node2.getName()).thenReturn("5302");

        edge = Mockito.mock(GraphEdge.class);
        when(edge.getName()).thenReturn("0-5302");
        when(edge.getSource()).thenReturn(node1);
        when(edge.getTarget()).thenReturn(node2);

        Map<String,String> props = new HashMap<String,String>();
        props.put("Author","Gottfried Vossen");

        when(edge.getProperties()).thenReturn(props);

    }

    @Test
    public void testEdgeImport(){
        Assert.assertEquals("0-5302",edge.getName());
        Assert.assertNotNull(edge.getProperties());
        Assert.assertEquals(1,edge.getProperties().size());

        Assert.assertEquals("0",edge.getSource().getName());
        Assert.assertEquals("5302",edge.getTarget().getName());

        Map properties = edge.getProperties();
        Assert.assertTrue(properties.containsKey("Author"));

        Assert.assertEquals("Gottfried Vossen",properties.get("Author"));

    }

    @After
    public void teardown() {
        edge = null;
    }
}
