package com.graph.viewer.utils;


import com.graph.viewer.core.GraphImporter;
import com.graph.viewer.model.GraphNode;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ImportUtils {
    private static ImportUtils INSTANCE ;
    private final static Logger LOGGER = Logger.getLogger(GraphImporter.class.getName());


    private ImportUtils(){

    }

    public static ImportUtils getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ImportUtils();
        }
        return INSTANCE;
    }

    public BufferedReader importFile(String filePathName){
        try{
            Path filePath = Paths.get(filePathName);
            try(BufferedReader reader = Files.newBufferedReader(filePath, Charset.forName("UTF-8"))){

                return reader;

            }catch(IOException ex){
                ex.printStackTrace(); //handle an exception here
            }

        }
        catch (InvalidPathException ex){
            LOGGER.severe(ex.getLocalizedMessage());
        }
        return null;
    }
}
