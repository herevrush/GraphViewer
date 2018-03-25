package com.graph.viewer.core;

import com.graph.viewer.model.GraphData;
import com.graph.viewer.model.GraphEdge;
import com.graph.viewer.model.GraphNode;
import com.graph.viewer.utils.ImportUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
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

    public  GraphNode getNode(String csvline, List<String> properties){
        String[] p = csvline.split(COMMA);// a CSV has comma separated lines

        GraphNode node = new GraphNode(p[0]);
        for(int i=1;i<p.length;i++){
            node.addProperty(properties.get(i), p[i]);
        }
        return node;
    }


    public void importNodesFromCSV(String filePathName, GraphData data){

        try{
            Path filePath = Paths.get(filePathName);
            try(BufferedReader br = Files.newBufferedReader(filePath, Charset.forName("UTF-8"))){
                List<String> properties = new ArrayList<>(Arrays.asList(br.readLine().split(COMMA)));

                // skip the header of the csv
                br.lines().forEach( (line) -> {
                    GraphNode node = getNode(line, properties);

                    if(data.getFirstNodeName() == null){
                        data.setFirstNodeName(node.getName());
                    }
                    data.getNodes().put(node.getName(),node);

                });

                br.close();

            }catch(IOException ex){
                ex.printStackTrace(); //handle an exception here
            }

        }
        catch (InvalidPathException ex){
            LOGGER.severe(ex.getLocalizedMessage());
        }

    }

    public void importEdgesFromCSV(String filePathName, GraphData data) throws Exception {

        try{
            if(data.getNodes().size() > 0)
            {
                Path filePath = Paths.get(filePathName);
                try(BufferedReader br = Files.newBufferedReader(filePath, Charset.forName("UTF-8"))){
                    List<String> properties = new ArrayList<>(Arrays.asList(br.readLine().split(COMMA)));
                    // skip the header of the csv
                    br.lines().forEach( (line) -> {

                        String[] p = line.split(COMMA);// a CSV has comma separated lines
                        GraphNode source = data.getNodes().get(p[0]);
                        GraphNode target = data.getNodes().get(p[1]);
                        if(source != null && target != null){
                            String name = p[0] + "-" + p[1];
                            GraphEdge edge = data.getEdges().get(name);
                            if(edge == null){
                                edge = new GraphEdge(name);
                                edge.setSource(source);
                                edge.setTarget(target);

                                data.getEdges().put(edge.getName(), edge);
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

                }catch(IOException ex){
                    ex.printStackTrace(); //handle an exception here
                }
            }
            else{
                throw  new Exception("Failed to load edges, nodes not available");
            }

        }
        catch (InvalidPathException ex){
            LOGGER.severe(ex.getLocalizedMessage());
        }

    }
}
