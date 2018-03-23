package com.graphViewer.model;

public class GraphEdge extends ElementImpl {

    private GraphNode source;
    private GraphNode target;

    public GraphEdge(String name){
        super(name);
    }

    public GraphNode getSource() {
        return source;
    }

    public GraphNode getTarget() {
        return target;
    }

    public void setSource(GraphNode source) {
        this.source = source;
    }

    public void setTarget(GraphNode target) {
        this.target = target;
    }
}
