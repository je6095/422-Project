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
 * @author Hassan Jegan Ndow
 */
public class XmlConvertFileParser {
    
   public static final String XML_ID = "XML File"; //first line of .edg files should be this
   public static final String SAVE_ID = "Xml Save File"; //first line of save files should be this
   
   private File parseFile;
   private FileReader fr;
   private BufferedReader br;
   private ArrayList alTables, alFields, alConnectors;
   private XmlTable[] tables;
   private XmlField[] fields;
   private XmlField tempField;
   private int numFigure;
  
   public XmlConvertFileParser(File constructorFile) {
       numFigure = 0;
      alTables = new ArrayList();
      alFields = new ArrayList();
      alConnectors = new ArrayList();
      parseFile = constructorFile;
      this.openFile(parseFile);
   }
    
    public void openFile(File inputFile) {
      
          parseXMLFile(inputFile);
          this.makeArrays();
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
                int id = Integer.parseInt(nNode.getAttributes().getNamedItem("id").getNodeValue());
                XmlTable xmlTable = new XmlTable(tableName, id);
                alTables.add(xmlTable);
                System.out.println("\nCurrent Table :" + tableName);
                
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			
                        Element eElement = (Element) nNode;
                        NodeList fieldList = eElement.getElementsByTagName("field");
                        
                        for (int i=0; i<fieldList.getLength(); i++) {
                            Node fieldNode = fieldList.item(i);
                            String fieldName = fieldNode.getAttributes().getNamedItem("name").getNodeValue();
                            int tableId = Integer.parseInt(fieldNode.getAttributes().getNamedItem("tableId").getNodeValue());
                            int fieldId = Integer.parseInt(fieldNode.getAttributes().getNamedItem("fieldId").getNodeValue());
                            String datatype = fieldNode.getAttributes().getNamedItem("datatype").getNodeValue();
                            int varcharVal = Integer.parseInt(fieldNode.getAttributes().getNamedItem("varchar_val").getNodeValue());
                            String primaryKey = fieldNode.getAttributes().getNamedItem("primary_key").getNodeValue();
                            boolean notNull = Boolean.parseBoolean(fieldNode.getAttributes().getNamedItem("not_null").getNodeValue());
                            String defaultVal = fieldNode.getAttributes().getNamedItem("default").getNodeValue();
                            
                            tempField = new XmlField(fieldName, fieldId, tableId);
                            tempField.setIsPrimaryKey(Boolean.parseBoolean(primaryKey));
                            tempField.setDataType(Integer.parseInt(datatype));
                            tempField.setDefaultValue(defaultVal);
                            tempField.setDisallowNull(notNull);
                            tempField.setVarcharValue(varcharVal);
                            tempField.setTableID(tableId);
                           
                            System.out.println(tempField.getIsPrimaryKey());
                            alFields.add(tempField);
                            xmlTable.addNativeField(fieldId);
                            
                                                      
                            System.out.println("\nField :" + fieldName);
                            System.out.println("\nDatatype :" + datatype);
                            System.out.println("\nPrimary Key :" + primaryKey);
                            System.out.println("\nNot Null :" + notNull);
                            System.out.println("\nDefault :" + defaultVal);
                            System.out.println("\nDefault :" + tableId);
                          }
                
                        
                        

		}
            }
            
            
        }catch (Exception e) {
	e.printStackTrace();
    }
    }
    
    private void makeArrays() { //convert ArrayList objects into arrays of the appropriate Class type
      if (alTables != null) {
         tables = (XmlTable[])alTables.toArray(new XmlTable[alTables.size()]);
      }
      if (alFields != null) {
         fields = (XmlField[])alFields.toArray(new XmlField[alFields.size()]);
      }
   }
    
    private boolean isTableDup(String testTableName) {
      for (int i = 0; i < alTables.size(); i++) {
         XmlTable tempTable = (XmlTable)alTables.get(i);
         if (tempTable.getName().equals(testTableName)) {
            return true;
         }
      }
      return false;
   }
    
    public XmlTable[] getEdgeTables() {
      return tables;
   }
   
   public XmlField[] getEdgeFields() {
      return fields;
   }
}
