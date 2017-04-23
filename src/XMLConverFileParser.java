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
import org.w3c.dom.Element;
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
   private ArrayList <XmlTable> alTables, alFields, alConnectors;
   private XmlTable[] tables;
  
   public XMLConverFileParser(File constructorFile) {
      alTables = new ArrayList<XmlTable>();
      alFields = new ArrayList();
      alConnectors = new ArrayList();
      parseFile = constructorFile;
      this.openFile(parseFile);
   }
    
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
            
            System.out.println("Root element :" + rootName);
            
            NodeList nList = doc.getElementsByTagName("table");
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                System.out.println("---------------------------------");
                Node nNode = nList.item(temp);
                String tableName = nNode.getAttributes().getNamedItem("name").getNodeValue();
                alTables.add(new XmlTable(tableName));
                System.out.println("\nCurrent Table :" + alTables.get(temp).getName());
                
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			
                        Element eElement = (Element) nNode;
                        NodeList fieldList = eElement.getElementsByTagName("field");
                        
                        for (int i=0; i<fieldList.getLength(); i++) {
                            Node fieldNode = fieldList.item(i);
                            String fieldName = fieldNode.getAttributes().getNamedItem("name").getNodeValue();
                            String datatype = fieldNode.getAttributes().getNamedItem("datatype").getNodeValue();
                            String primaryKey = fieldNode.getAttributes().getNamedItem("primary_key").getNodeValue();
                            String notNull = fieldNode.getAttributes().getNamedItem("not_null").getNodeValue();
                            String unsigned = fieldNode.getAttributes().getNamedItem("unsigned").getNodeValue();
                            String binary = fieldNode.getAttributes().getNamedItem("binary").getNodeValue();
                            String autoIncrement = fieldNode.getAttributes().getNamedItem("auto_increment").getNodeValue();
                            String generated = fieldNode.getAttributes().getNamedItem("primary_key").getNodeValue();
                            String defaultVal = fieldNode.getAttributes().getNamedItem("default").getNodeValue();
                            
                            System.out.println("\nField :" + fieldName);
                            System.out.println("\nDatatype :" + datatype);
                            System.out.println("\nPrimary Key :" + primaryKey);
                            System.out.println("\nNot Null :" + notNull);
                            System.out.println("\nUnsigned :" + unsigned);
                            System.out.println("\nBinary :" + binary);
                            System.out.println("\nAuto Increment :" + autoIncrement);
                            System.out.println("\nGenerated :" + generated);
                            System.out.println("\nDefault :" + defaultVal);
                          }
                        
                        

		}
            }
            
            
        }catch (Exception e) {
	e.printStackTrace();
    }
    }
}
