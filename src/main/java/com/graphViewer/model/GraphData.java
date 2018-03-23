package com.graphViewer.model;

import java.util.Map;

public class GraphData {

    private Map<String,GraphNode> nodes;

    private Map<String,GraphEdge> edges;

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
