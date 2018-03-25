package com.graph.viewer.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphData {

    private String firstNodeName;

    private String selectedNode;

//    private List<String> nodeHeaders;

    private Map<String,GraphNode> nodes;

    private Map<String,GraphEdge> edges;


    public GraphData(){
        nodes = new HashMap<String, GraphNode>();
        edges = new HashMap<String, GraphEdge>();
    }
//    public List<String> getNodeHeaders() {
//        return nodeHeaders;
//    }

//    public void setNodeHeaders(List<String> nodeHeaders) {
//        this.nodeHeaders = nodeHeaders;
//    }

    public String getFirstNodeName() {
        return firstNodeName;
    }

    public void setFirstNodeName(String firstNodeName) {
        this.firstNodeName = firstNodeName;
    }

    public String getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(String selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Map<String,GraphEdge> getEdges() {
        return edges;
    }

    public Map<String, GraphNode> getNodes() {
        return nodes;
    }

    public void setEdges(Map<String,GraphEdge> edges) {
        this.edges = edges;
    }

    public void setNodes(Map<String,GraphNode> nodes) {
        this.nodes = nodes;
    }

}
