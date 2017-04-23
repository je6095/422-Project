/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edgeconvert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Hypemaster
 */
public class XMLConverFileParser {
    
   public static final String XML_ID = "XML File"; //first line of .edg files should be this
   public static final String SAVE_ID = "EdgeConvert Save File"; //first line of save files should be this
   
   private File parseFile;
   private FileReader fr;
   private BufferedReader br;
   private String currentLine;
  
    
    public void openFile(File inputFile) {
      
          parseXMLFile(inputFile);
   } // openFile()
    
    public void parseXMLFile(File inputFile){
        try{
            File fXmlFile = inputFile;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            
            doc.getDocumentElement().normalize();
            
            String rootName = doc.getDocumentElement().getAttribute("name");
            
            NodeList nList = doc.getElementsByTagName("table");
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                String tableName = nNode.getAttributes().getNamedItem("name").getNodeValue();
                System.out.println("\nCurrent Table :" + tableName);
            }
            
            System.out.println("Root element :" + rootName);
        }catch (Exception e) {
	e.printStackTrace();
    }
    }
}
