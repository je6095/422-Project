
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class XmlConvertFileParser extends ConvertFileParser{
    
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
   private int numConnector, numFields, numTables, numNativeRelatedFields;
   private XmlConnector[] connectors;
  
   public XmlConvertFileParser(File constructorFile) {
      numFigure = 0;
      numConnector = 0;
      alTables = new ArrayList();
      alFields = new ArrayList();
      alConnectors = new ArrayList();
      parseFile = constructorFile;
      String status = EdgeConvertGUI.getStatus();
      System.out.println("The status is: " + status);
      if(getFileExtension(parseFile).equals("xml") || getFileExtension(parseFile).equals("sav") ){
      
          this.openFile(parseFile);
      }   
      
      else {
        JOptionPane.showMessageDialog(null, "Unrecognized file format");
      }
   }
   
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
    
    public void openFile(File inputFile) {
      
       try {
           this.parseFile();
           this.makeArrays();
           this.resolveConnectors();
       } // openFile()
       catch (Exception ex) {
           Logger.getLogger(XmlConvertFileParser.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
    
    public void parseFile() throws Exception{
        
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(parseFile);
           
            
            doc.getDocumentElement().normalize();
            
            String rootName = doc.getDocumentElement().getAttribute("name");
            
            System.out.println("Root element :" + rootName);
            
            NodeList nList = doc.getElementsByTagName("table");
            
            NodeList relList = doc.getElementsByTagName("relations");
            
            for (int temp = 0; temp < relList.getLength(); temp++) {
                System.out.println("---------------------------------");
                Node nNode = relList.item(temp);
                           
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			
                        Element eElement = (Element) nNode;
                        NodeList fieldList = eElement.getElementsByTagName("relationship");
                        
                        for (int i=0; i<fieldList.getLength(); i++) {
                            Node fieldNode = fieldList.item(i);
                            
                            String relationId = fieldNode.getAttributes().getNamedItem("relationId").getNodeValue();
                            String table1 = fieldNode.getAttributes().getNamedItem("table1").getNodeValue();
                            String table2 = fieldNode.getAttributes().getNamedItem("table2").getNodeValue();
                            String rel1 = fieldNode.getAttributes().getNamedItem("rel1").getNodeValue();
                            String rel2 = fieldNode.getAttributes().getNamedItem("rel2").getNodeValue();
                            XmlConnector xmlConnector = new XmlConnector(relationId, table1, table2, rel1, rel2);
                            alConnectors.add(xmlConnector);
                            System.out.println("\nRelation id :" + relationId);
                            
                          }
                
                        
                        

		}
            }
            
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
                            tempField.setTableBound(0);
                            tempField.setFieldBound(0);
                           
                           
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
            
           
    }
    
    protected void makeArrays() { //convert ArrayList objects into arrays of the appropriate Class type
      if (alTables != null) {
         tables = (XmlTable[])alTables.toArray(new XmlTable[alTables.size()]);
      }
      if (alFields != null) {
         fields = (XmlField[])alFields.toArray(new XmlField[alFields.size()]);
      }
      
      if (alConnectors != null) {
         connectors = (XmlConnector[])alConnectors.toArray(new XmlConnector[alConnectors.size()]);
      }
   }
    
    protected void resolveConnectors() { //Identify nature of Connector endpoints
      int endPoint1, endPoint2;
      int fieldIndex = 0, table1Index = 0, table2Index = 0;
      for (int cIndex = 0; cIndex < connectors.length; cIndex++) {
         endPoint1 = connectors[cIndex].getEndPoint1();
         endPoint2 = connectors[cIndex].getEndPoint2();
         fieldIndex = -1;
         for (int fIndex = 0; fIndex < fields.length; fIndex++) { //search fields array for endpoints
            if (endPoint1 == fields[fIndex].getNumFigure()) { //found endPoint1 in fields array
               connectors[cIndex].setIsEP1Field(true); //set appropriate flag
               fieldIndex = fIndex; //identify which element of the fields array that endPoint1 was found in
            }
            if (endPoint2 == fields[fIndex].getNumFigure()) { //found endPoint2 in fields array
               connectors[cIndex].setIsEP2Field(true); //set appropriate flag
               fieldIndex = fIndex; //identify which element of the fields array that endPoint2 was found in
            }
         }
         for (int tIndex = 0; tIndex < tables.length; tIndex++) { //search tables array for endpoints
            if (endPoint1 == tables[tIndex].getNumFigure()) { //found endPoint1 in tables array
               connectors[cIndex].setIsEP1Table(true); //set appropriate flag
               table1Index = tIndex; //identify which element of the tables array that endPoint1 was found in
            }
            if (endPoint2 == tables[tIndex].getNumFigure()) { //found endPoint1 in tables array
               connectors[cIndex].setIsEP2Table(true); //set appropriate flag
               table2Index = tIndex; //identify which element of the tables array that endPoint2 was found in
            }
         }
         
         if (connectors[cIndex].getIsEP1Field() && connectors[cIndex].getIsEP2Field()) { //both endpoints are fields, implies lack of normalization
            System.out.println("Connectors:");
             for(XmlConnector connector : connectors){
                 System.out.println("id: " +connector.getNumConnector());
                 System.out.println("endpoint1: " +connector.getEndPoint1());
                 System.out.println("endpoint2: " +connector.getEndPoint2());
                 System.out.println("endstyle1: " +connector.getEndStyle1());
                 System.out.println("endstyle2: " +connector.getEndStyle2());
             }
             JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + parseFile + "\ncontains composite attributes. Please resolve them and try again.");
            EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components
            break; //stop processing list of Connectors
         }

         if (connectors[cIndex].getIsEP1Table() && connectors[cIndex].getIsEP2Table()) { //both endpoints are tables
            if ((connectors[cIndex].getEndStyle1().indexOf("many") >= 0) &&
                (connectors[cIndex].getEndStyle2().indexOf("many") >= 0)) { //the connector represents a many-many relationship, implies lack of normalization
               JOptionPane.showMessageDialog(null, "There is a many-many relationship between tables\n\"" + tables[table1Index].getName() + "\" and \"" + tables[table2Index].getName() + "\"" + "\nPlease resolve this and try again.");
               EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components
               break; //stop processing list of Connectors
            } else { //add Figure number to each table's list of related tables
               tables[table1Index].addRelatedTable(tables[table2Index].getNumFigure());
               tables[table2Index].addRelatedTable(tables[table1Index].getNumFigure());
               continue; //next Connector
            }
         }
         
         if (fieldIndex >=0 && fields[fieldIndex].getTableID() == 0) { //field has not been assigned to a table yet
            if (connectors[cIndex].getIsEP1Table()) { //endpoint1 is the table
               tables[table1Index].addNativeField(fields[fieldIndex].getNumFigure()); //add to the appropriate table's field list
               fields[fieldIndex].setTableID(tables[table1Index].getNumFigure()); //tell the field what table it belongs to
            } else { //endpoint2 is the table
               tables[table2Index].addNativeField(fields[fieldIndex].getNumFigure()); //add to the appropriate table's field list
               fields[fieldIndex].setTableID(tables[table2Index].getNumFigure()); //tell the field what table it belongs to
            }
         } else if (fieldIndex >=0) { //field has already been assigned to a table
            JOptionPane.showMessageDialog(null, "The attribute " + fields[fieldIndex].getName() + " is connected to multiple tables.\nPlease resolve this and try again.");
            EdgeConvertGUI.setReadSuccess(false); //this tells GUI not to populate JList components
            break; //stop processing list of Connectors
         }
      } // connectors for() loop
   } // resolveConnectors()
    
    protected boolean isTableDup(String testTableName) {
      for (int i = 0; i < alTables.size(); i++) {
         XmlTable tempTable = (XmlTable)alTables.get(i);
         if (tempTable.getName().equals(testTableName)) {
            return true;
         }
      }
      return false;
   }
    
    public XmlTable[] getXmlTables() {
      return tables;
   }
   
   public XmlField[] getXmlFields() {
      return fields;
   }
}
