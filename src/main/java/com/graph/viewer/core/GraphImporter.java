package com.graph.viewer.core;

import com.graph.viewer.model.GraphData;
import com.graph.viewer.model.GraphEdge;
import com.graph.viewer.model.GraphNode;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class GraphImporter {

    private final static Logger LOGGER = Logger.getLogger(GraphImporter.class.getName());
    private static final int BUFFER_SIZE = 4096;
    private static String COMMA = ",";

    private static GraphImporter INSTANCE = null;

    private GraphImporter(){

    }

    public static GraphImporter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new GraphImporter();
        }
        return INSTANCE;
    }

    public void importNodesFromCSV(String filePathName, GraphData data){
        Path filePath = null;
        Map<String, GraphNode> nodes = new HashMap<String, GraphNode>();
        try{
            filePath = Paths.get(filePathName);
        }
        catch (InvalidPathException ex){
            LOGGER.severe(ex.getLocalizedMessage());
        }
        try{


            InputStream inputFS = new FileInputStream(filePath.toFile());
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

            // skip the header of the csv
            List<String> properties = new ArrayList<>(Arrays.asList(br.readLine().split(COMMA)));
            boolean isFirst = true;
            br.lines().skip(1).forEach( (line) -> {
                String[] p = line.split(COMMA);// a CSV has comma separated lines

                GraphNode node = new GraphNode(p[0]);
//                System.out.println("adding " + node.getName());
                if(data.getFirstNodeName() == null){
                    data.setFirstNodeName(node.getName());
                }
                for(int i=1;i<p.length;i++){
                    node.addProperty(properties.get(i), p[i]);
                }
                nodes.put(node.getName(),node);

            });

//            for (GraphNode node: nodes.values()) {
//                System.out.println(node.getId());
//            }
            br.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
        System.out.println(nodes.size());
        data.setNodes(nodes);
    }

    public void importEdgesFromCSV(String filePathName, GraphData data){
        Path filePath = null;
        Map<String, GraphEdge>  edges = new HashMap<> ();
        try{
            filePath = Paths.get(filePathName);
            if(filePath != null){
                try{


                    InputStream inputFS = new FileInputStream(filePath.toFile());
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

                    // skip the header of the csv

                    List<String> properties = new ArrayList<>(Arrays.asList(br.readLine().split(COMMA)));
                    br.lines().skip(1).forEach( (line) -> {

                        String[] p = line.split(COMMA);// a CSV has comma separated lines
                        GraphNode source = data.getNodes().get(p[0]);
                        GraphNode target = data.getNodes().get(p[1]);
                        if(source != null && target != null){
                            String name = p[0] + "-" + p[1];
//                            System.out.println(" adding edge" + name);
                            GraphEdge edge = edges.get(name);
                            if(edge == null){
                                edge = new GraphEdge(name);
                                edge.setSource(source);
                                edge.setTarget(target);

                                edges.put(edge.getName(), edge);
                                for(int i=2;i<p.length;i++){
                                    edge.addProperty(properties.get(i), p[i]);
                                }
                            }
                            else{
                                for(int i=2;i<p.length;i++){
                                    edge.appendProperty(properties.get(i), p[i]);
                                }
                            }
                        }

                    });

                    br.close();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
        catch (InvalidPathException ex){
            LOGGER.severe(ex.getLocalizedMessage());
        }
        data.setEdges(edges);
    }
}
