package com.graphViewer.main.core;

import com.graphViewer.main.model.GraphData;
import com.graphViewer.main.model.GraphEdge;
import com.graphViewer.main.model.GraphNode;

import java.util.Map;

public class GraphController {

    private GraphData graphData;

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

}
