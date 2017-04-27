

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EdgeConvertGUI {
   
   public String status; 
   public static final int HORIZ_SIZE = 635;
   public static final int VERT_SIZE = 400;
   public static final int HORIZ_LOC = 100;
   public static final int VERT_LOC = 100;
   public static final String DEFINE_TABLES = "Define Tables";
   public static final String DEFINE_RELATIONS = "Define Relations";
   public static final String CANCELLED = "CANCELLED";
   private static JFileChooser jfcEdge, jfcXml, jfcGetClass, jfcOutputDir; //refac
   private static ExampleFileFilter effEdge,effXml, effSave, effClass;
   private File parseFile, saveFile, outputFile, outputDir, outputDirOld;
   private String truncatedFilename;
   private String sqlString;
   private String databaseName;
   EdgeMenuListener menuListener;
   EdgeRadioButtonListener radioListener;
   EdgeWindowListener edgeWindowListener;
   CreateDDLButtonListener createDDLListener;
   private EdgeConvertFileParser ecfp;
   private XmlConvertFileParser xcfp; //refac
   private EdgeConvertCreateDDL eccd;
   private static PrintWriter pw;
   private EdgeTable[] tables; //master copy of EdgeTable objects
   private EdgeField[] fields; //master copy of EdgeField objects
   private XmlTable[] xmlTables; //refac
   private XmlField[] xmlFields; //refac
   private EdgeTable currentDTTable, currentDRTable1, currentDRTable2; //pointers to currently selected table(s) on Define Tables (DT) and Define Relations (DR) screens
   private EdgeField currentDTField, currentDRField1, currentDRField2; //pointers to currently selected field(s) on Define Tables (DT) and Define Relations (DR) screens
   private XmlTable xmlCurrentDTTable, xmlCurrentDRTable1, xmlCurrentDRTable2; //refac
   private XmlField xmlCurrentDTField, xmlCurrentDRField1, xmlCurrentDRField2; //refac
   private static boolean readSuccess = true; //this tells GUI whether to populate JList components or not
   private boolean dataSaved = true;
   private ArrayList alSubclasses, alProductNames;
   private String[] productNames;
   private Object[] objSubclasses;

   //Define Tables screen objects
   static JFrame jfDT;
   static JPanel jpDTBottom, jpDTCenter, jpDTCenter1, jpDTCenter2, jpDTCenterRight, jpDTCenterRight1, jpDTCenterRight2, jpDTMove;
   static JButton jbDTCreateDDL, jbDTDefineRelations, jbDTVarchar, jbDTDefaultValue, jbDTMoveUp, jbDTMoveDown;
   static ButtonGroup bgDTDataType;
   static JRadioButton[] jrbDataType;
   static String[] strDataType;
   static JCheckBox jcheckDTDisallowNull, jcheckDTPrimaryKey;
   static JTextField jtfDTVarchar, jtfDTDefaultValue;
   static JLabel jlabDTTables, jlabDTFields;
   static JScrollPane jspDTTablesAll, jspDTFieldsTablesAll;
   static JList jlDTTablesAll, jlDTFieldsTablesAll;
   static DefaultListModel dlmDTTablesAll, dlmDTFieldsTablesAll;
   static JMenuBar jmbDTMenuBar;
   static JMenu jmDTFile, jmDTOptions, jmDTHelp;
   static JMenuItem jmiDTOpenEdge, jmiDTOpenXml, jmiDTOpenSave, jmiDTOpenSaveXml, jmiDTSave, jmiDTSaveAs, jmiDTExit, jmiDTOptionsOutputLocation, jmiDTOptionsShowProducts, jmiDTHelpAbout; //refac
   
   //Define Relations screen objects
   static JFrame jfDR;
   static JPanel jpDRBottom, jpDRCenter, jpDRCenter1, jpDRCenter2, jpDRCenter3, jpDRCenter4;
   static JButton jbDRCreateDDL, jbDRDefineTables, jbDRBindRelation;
   static JList jlDRTablesRelations, jlDRTablesRelatedTo, jlDRFieldsTablesRelations, jlDRFieldsTablesRelatedTo;
   static DefaultListModel dlmDRTablesRelations, dlmDRTablesRelatedTo, dlmDRFieldsTablesRelations, dlmDRFieldsTablesRelatedTo;
   static JLabel jlabDRTablesRelations, jlabDRTablesRelatedTo, jlabDRFieldsTablesRelations, jlabDRFieldsTablesRelatedTo;
   static JScrollPane jspDRTablesRelations, jspDRTablesRelatedTo, jspDRFieldsTablesRelations, jspDRFieldsTablesRelatedTo;
   static JMenuBar jmbDRMenuBar;
   static JMenu jmDRFile, jmDROptions, jmDRHelp;
   static JMenuItem jmiDROpenEdge, jmiDROpenXml, jmiDROpenSave, jmiDROpenSaveXml, jmiDRSave, jmiDRSaveAs, jmiDRExit, jmiDROptionsOutputLocation, jmiDROptionsShowProducts, jmiDRHelpAbout;
   
   public EdgeConvertGUI() {
      menuListener = new EdgeMenuListener();
      radioListener = new EdgeRadioButtonListener();
      edgeWindowListener = new EdgeWindowListener();
      createDDLListener = new CreateDDLButtonListener();
      status = "";
      this.showGUI();
   } // EdgeConvertGUI.EdgeConvertGUI()
   
   public void showGUI() {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //use the OS native LAF, as opposed to default Java LAF
      } catch (Exception e) {
         System.out.println("Error setting native LAF: " + e);
      }
      createDTScreen();
      createDRScreen();
   } //showGUI()

   public void createDTScreen() {//create Define Tables screen
      jfDT = new JFrame(DEFINE_TABLES);
      jfDT.setLocation(HORIZ_LOC, VERT_LOC);
      Container cp = jfDT.getContentPane();
      jfDT.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      jfDT.addWindowListener(edgeWindowListener);
      jfDT.getContentPane().setLayout(new BorderLayout());
      jfDT.setVisible(true);
      jfDT.setSize(HORIZ_SIZE + 150, VERT_SIZE);

      //setup menubars and menus
      jmbDTMenuBar = new JMenuBar();
      jfDT.setJMenuBar(jmbDTMenuBar);

      jmDTFile = new JMenu("File");
      jmDTFile.setMnemonic(KeyEvent.VK_F);
      jmbDTMenuBar.add(jmDTFile);
      jmiDTOpenEdge = new JMenuItem("Open Edge File");
      jmiDTOpenEdge.setMnemonic(KeyEvent.VK_E);
      jmiDTOpenEdge.addActionListener(menuListener);
      //feat: initializes jmiDTOpenXml
      jmiDTOpenXml = new JMenuItem("Open XML File"); //refac
      jmiDTOpenXml.setMnemonic(KeyEvent.VK_X); //refac
      jmiDTOpenXml.addActionListener(menuListener); //refac
      jmiDTOpenSave = new JMenuItem("Open Save File");
      jmiDTOpenSave.setMnemonic(KeyEvent.VK_V);
      jmiDTOpenSave.addActionListener(menuListener);
      jmiDTOpenSaveXml = new JMenuItem("Open XML Save File"); //refac
      jmiDTOpenSaveXml.setMnemonic(KeyEvent.VK_Y); //refac
      jmiDTOpenSaveXml.addActionListener(menuListener); //refac
      jmiDTSave = new JMenuItem("Save");
      jmiDTSave.setMnemonic(KeyEvent.VK_S);
      jmiDTSave.setEnabled(false);
      jmiDTSave.addActionListener(menuListener);
      jmiDTSaveAs = new JMenuItem("Save As...");
      jmiDTSaveAs.setMnemonic(KeyEvent.VK_A);
      jmiDTSaveAs.setEnabled(false);
      jmiDTSaveAs.addActionListener(menuListener);
      jmiDTExit = new JMenuItem("Exit");
      jmiDTExit.setMnemonic(KeyEvent.VK_X);
      jmiDTExit.addActionListener(menuListener);
      jmDTFile.add(jmiDTOpenEdge);
      jmDTFile.add(jmiDTOpenXml); //refac
      jmDTFile.addSeparator();
      jmDTFile.add(jmiDTOpenSave);
      jmDTFile.add(jmiDTOpenSaveXml);
      jmDTFile.addSeparator();
      jmDTFile.add(jmiDTSave);
      jmDTFile.add(jmiDTSaveAs);
      jmDTFile.add(jmiDTExit);
      
      jmDTOptions = new JMenu("Options");
      jmDTOptions.setMnemonic(KeyEvent.VK_O);
      jmbDTMenuBar.add(jmDTOptions);
      jmiDTOptionsOutputLocation = new JMenuItem("Set Output File Definition Location");
      jmiDTOptionsOutputLocation.setMnemonic(KeyEvent.VK_S);
      jmiDTOptionsOutputLocation.addActionListener(menuListener);
      jmiDTOptionsShowProducts = new JMenuItem("Show Database Products Available");
      jmiDTOptionsShowProducts.setMnemonic(KeyEvent.VK_H);
      jmiDTOptionsShowProducts.setEnabled(false);
      jmiDTOptionsShowProducts.addActionListener(menuListener);
      jmDTOptions.add(jmiDTOptionsOutputLocation);
      jmDTOptions.add(jmiDTOptionsShowProducts);
      
      jmDTHelp = new JMenu("Help");
      jmDTHelp.setMnemonic(KeyEvent.VK_H);
      jmbDTMenuBar.add(jmDTHelp);
      jmiDTHelpAbout = new JMenuItem("About");
      jmiDTHelpAbout.setMnemonic(KeyEvent.VK_A);
      jmiDTHelpAbout.addActionListener(menuListener);
      jmDTHelp.add(jmiDTHelpAbout);
      
      jfcEdge = new JFileChooser();
      jfcXml = new JFileChooser();
      jfcOutputDir = new JFileChooser();
      effEdge = new ExampleFileFilter("edg", "Edge Diagrammer Files");
      effXml = new ExampleFileFilter("xml", "XML Files"); //refac
      effSave = new ExampleFileFilter("sav", "Edge Convert Save Files");
      jfcOutputDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      jpDTBottom = new JPanel(new GridLayout(1, 2));

      jbDTCreateDDL = new JButton("Create DDL");
      jbDTCreateDDL.setEnabled(false);
      jbDTCreateDDL.addActionListener(createDDLListener);

      jbDTDefineRelations = new JButton (DEFINE_RELATIONS);
      jbDTDefineRelations.setEnabled(false);
      jbDTDefineRelations.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               jfDT.setVisible(false);
               jfDR.setVisible(true); //show the Define Relations screen
               clearDTControls();
               dlmDTFieldsTablesAll.removeAllElements();
            }
         }
      );

      jpDTBottom.add(jbDTDefineRelations);
      jpDTBottom.add(jbDTCreateDDL);
      jfDT.getContentPane().add(jpDTBottom, BorderLayout.SOUTH);
      
      jpDTCenter = new JPanel(new GridLayout(1, 3));
      jpDTCenterRight = new JPanel(new GridLayout(1, 2));
      dlmDTTablesAll = new DefaultListModel();
      jlDTTablesAll = new JList(dlmDTTablesAll);
      jlDTTablesAll.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDTTablesAll.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDTTablesAll.getElementAt(selIndex).toString();
                  setCurrentDTTable(selText); //set pointer to the selected table
                  
                  //refac
                  int[] currentNativeFields = null;
                  
                  if(status.equals("edge")){
                     currentNativeFields = currentDTTable.getNativeFieldsArray();
                  }
                  
                  if(status.equals("xml")){
                     currentNativeFields = xmlCurrentDTTable.getNativeFieldsArray();
                  }
                  
                  jlDTFieldsTablesAll.clearSelection();
                  dlmDTFieldsTablesAll.removeAllElements();
                  jbDTMoveUp.setEnabled(false);
                  jbDTMoveDown.setEnabled(false);
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                
                     if(status.equals("edge")){ 
                        dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));
                     }
                     
                     else if(status.equals("xml")){ 
                        dlmDTFieldsTablesAll.addElement(getXmlFieldName(currentNativeFields[fIndex]));
                     }
                     
                  }
               }
               disableControls();
            }
         }
      );
      
      dlmDTFieldsTablesAll = new DefaultListModel();
      jlDTFieldsTablesAll = new JList(dlmDTFieldsTablesAll);
      jlDTFieldsTablesAll.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
               int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
               if (selIndex >= 0) {
                  if (selIndex == 0) {
                     jbDTMoveUp.setEnabled(false);
                  } else {
                     jbDTMoveUp.setEnabled(true);
                  }
                  if (selIndex == (dlmDTFieldsTablesAll.getSize() - 1)) {
                     jbDTMoveDown.setEnabled(false);
                  } else {
                     jbDTMoveDown.setEnabled(true);
                  }
                  String selText = dlmDTFieldsTablesAll.getElementAt(selIndex).toString();
                  setCurrentDTField(selText); //set pointer to the selected field
                  enableControls();
                  if(status.equals("edge")){
                    jrbDataType[currentDTField.getDataType()].setSelected(true); //select the appropriate radio button, based on value of dataType
                  }
                  else if(status.equals("xml")){
                    jrbDataType[xmlCurrentDTField.getDataType()].setSelected(true); //select the appropriate radio button, based on value of dataType
                  }
                  if (jrbDataType[0].isSelected()) { //this is the Varchar radio button
                     jbDTVarchar.setEnabled(true); //enable the Varchar button
                     if(status.equals("edge")){
                        jtfDTVarchar.setText(Integer.toString(currentDTField.getVarcharValue())); //fill text field with varcharValue
                     }
                     
                     else if(status.equals("xml")){
                        jtfDTVarchar.setText(Integer.toString(xmlCurrentDTField.getVarcharValue())); //fill text field with varcharValue
                     }
                  } else { //some radio button other than Varchar is selected
                     jtfDTVarchar.setText(""); //clear the text field
                     jbDTVarchar.setEnabled(false); //disable the button
                  }
                  
                  if(status.equals("edge")){
                    jcheckDTPrimaryKey.setSelected(currentDTField.getIsPrimaryKey()); //clear or set Primary Key checkbox
                    jcheckDTDisallowNull.setSelected(currentDTField.getDisallowNull()); //clear or set Disallow Null checkbox
                    jtfDTDefaultValue.setText(currentDTField.getDefaultValue()); //fill text field with defaultValue
                  }
                  
                  else if(status.equals("xml")){
                    jcheckDTPrimaryKey.setSelected(xmlCurrentDTField.getIsPrimaryKey()); //clear or set Primary Key checkbox
                    jcheckDTDisallowNull.setSelected(xmlCurrentDTField.getDisallowNull()); //clear or set Disallow Null checkbox
                    jtfDTDefaultValue.setText(xmlCurrentDTField.getDefaultValue()); //fill text field with defaultValue
                  }
               }
            }
         }
      );
      
      jpDTMove = new JPanel(new GridLayout(2, 1));
      jbDTMoveUp = new JButton("^");
      jbDTMoveUp.setEnabled(false);
      jbDTMoveUp.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int selection = jlDTFieldsTablesAll.getSelectedIndex();
               if (status.equals("edge")){currentDTTable.moveFieldUp(selection);}
               else if (status.equals("xml")){xmlCurrentDTTable.moveFieldUp(selection);}
               //repopulate Fields List
               int[] currentNativeFields = null;
               
               if (status.equals("edge")){ currentNativeFields = currentDTTable.getNativeFieldsArray();}
               else if (status.equals("xml")){ currentNativeFields = xmlCurrentDTTable.getNativeFieldsArray();}
               
               jlDTFieldsTablesAll.clearSelection();
               dlmDTFieldsTablesAll.removeAllElements();
               for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                  if (status.equals("edge")){dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));}
                  if (status.equals("xml")){dlmDTFieldsTablesAll.addElement(getXmlFieldName(currentNativeFields[fIndex]));}
               }
               jlDTFieldsTablesAll.setSelectedIndex(selection - 1);
               dataSaved = false;
            }
         }
      );
      jbDTMoveDown = new JButton("v");
      jbDTMoveDown.setEnabled(false);
      jbDTMoveDown.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int selection = jlDTFieldsTablesAll.getSelectedIndex(); //the original selected index
               if (status.equals("edge")){currentDTTable.moveFieldDown(selection);}
               else if (status.equals("xml")){xmlCurrentDTTable.moveFieldDown(selection);}
               //repopulate Fields List
               
               int[] currentNativeFields = null;
               
               if (status.equals("edge")){ currentNativeFields = currentDTTable.getNativeFieldsArray();}
               else if (status.equals("xml")){ currentNativeFields = xmlCurrentDTTable.getNativeFieldsArray();}

               jlDTFieldsTablesAll.clearSelection();
               dlmDTFieldsTablesAll.removeAllElements();
               for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                  if (status.equals("edge")){dlmDTFieldsTablesAll.addElement(getFieldName(currentNativeFields[fIndex]));}
                  else if (status.equals("xml")){dlmDTFieldsTablesAll.addElement(getXmlFieldName(currentNativeFields[fIndex]));}
               }
               jlDTFieldsTablesAll.setSelectedIndex(selection + 1);
               dataSaved = false;
            }
         }
      );
      jpDTMove.add(jbDTMoveUp);
      jpDTMove.add(jbDTMoveDown);

      jspDTTablesAll = new JScrollPane(jlDTTablesAll);
      jspDTFieldsTablesAll = new JScrollPane(jlDTFieldsTablesAll);
      jpDTCenter1 = new JPanel(new BorderLayout());
      jpDTCenter2 = new JPanel(new BorderLayout());
      jlabDTTables = new JLabel("All Tables", SwingConstants.CENTER);
      jlabDTFields = new JLabel("Fields List", SwingConstants.CENTER);
      jpDTCenter1.add(jlabDTTables, BorderLayout.NORTH);
      jpDTCenter2.add(jlabDTFields, BorderLayout.NORTH);
      jpDTCenter1.add(jspDTTablesAll, BorderLayout.CENTER);
      jpDTCenter2.add(jspDTFieldsTablesAll, BorderLayout.CENTER);
      jpDTCenter2.add(jpDTMove, BorderLayout.EAST);
      jpDTCenter.add(jpDTCenter1);
      jpDTCenter.add(jpDTCenter2);
      jpDTCenter.add(jpDTCenterRight);

      strDataType = EdgeField.getStrDataType(); //get the list of currently supported data types
      jrbDataType = new JRadioButton[strDataType.length]; //create array of JRadioButtons, one for each supported data type
      bgDTDataType = new ButtonGroup();
      jpDTCenterRight1 = new JPanel(new GridLayout(strDataType.length, 1));
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i] = new JRadioButton(strDataType[i]); //assign label for radio button from String array
         jrbDataType[i].setEnabled(false);
         jrbDataType[i].addActionListener(radioListener);
         bgDTDataType.add(jrbDataType[i]);
         jpDTCenterRight1.add(jrbDataType[i]);
      }
      jpDTCenterRight.add(jpDTCenterRight1);
      
      jcheckDTDisallowNull = new JCheckBox("Disallow Null");
      jcheckDTDisallowNull.setEnabled(false);
      jcheckDTDisallowNull.addItemListener(
         new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if(status.equals("edge")){
                    currentDTField.setDisallowNull(jcheckDTDisallowNull.isSelected());
                }
                
                else if(status.equals("xml")){
                    xmlCurrentDTField.setDisallowNull(jcheckDTDisallowNull.isSelected());
                }
               dataSaved = false;
            }
         }
      );
      
      jcheckDTPrimaryKey = new JCheckBox("Primary Key");
      jcheckDTPrimaryKey.setEnabled(false);
      jcheckDTPrimaryKey.addItemListener(
         new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if(status.equals("edge")){
                        currentDTField.setIsPrimaryKey(jcheckDTPrimaryKey.isSelected());
                }
                
                else if(status.equals("xml")){
                        xmlCurrentDTField.setIsPrimaryKey(jcheckDTPrimaryKey.isSelected());
                }
               
               dataSaved = false;
            }
         }
      );
      
      jbDTDefaultValue = new JButton("Set Default Value");
      jbDTDefaultValue.setEnabled(false);
      jbDTDefaultValue.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               String prev = jtfDTDefaultValue.getText();
               boolean goodData = false;
               int i = 0;
               if(status.equals("edge")){i = currentDTField.getDataType();}
               else if(status.equals("xml")){i = xmlCurrentDTField.getDataType();}
               do {
                  String result = (String)JOptionPane.showInputDialog(
                       null,
                       "Enter the default value:",
                       "Default Value",
                       JOptionPane.PLAIN_MESSAGE,
                       null,
                       null,
                       prev);

                  if ((result == null)) {
                     jtfDTDefaultValue.setText(prev);
                     return;
                  }
                  switch (i) {
                     case 0: //varchar
                        if (result.length() <= Integer.parseInt(jtfDTVarchar.getText())) {
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                        } else {
                           JOptionPane.showMessageDialog(null, "The length of this value must be less than or equal to the Varchar length specified.");
                        }
                        break;
                     case 1: //boolean
                        String newResult = result.toLowerCase();
                        if (newResult.equals("true") || newResult.equals("false")) {
                           jtfDTDefaultValue.setText(newResult);
                           goodData = true;
                        } else {
                           JOptionPane.showMessageDialog(null, "You must input a valid boolean value (\"true\" or \"false\").");
                        }
                        break;
                     case 2: //Integer
                        try {
                           int intResult = Integer.parseInt(result);
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                        } catch (NumberFormatException nfe) {
                           JOptionPane.showMessageDialog(null, "\"" + result + "\" is not an integer or is outside the bounds of valid integer values.");
                        }
                        break;
                     case 3: //Double
                        try {
                           double doubleResult = Double.parseDouble(result);
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                        } catch (NumberFormatException nfe) {
                           JOptionPane.showMessageDialog(null, "\"" + result + "\" is not a double or is outside the bounds of valid double values.");
                        }
                        break;
                     case 4: //Timestamp
                        try {
                           jtfDTDefaultValue.setText(result);
                           goodData = true;
                        }
                        catch (Exception e) {
                           
                        }
                        break;
                  }
               } while (!goodData);
               int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDTFieldsTablesAll.getElementAt(selIndex).toString();
                  setCurrentDTField(selText);
                  if(status.equals("edge")){currentDTField.setDefaultValue(jtfDTDefaultValue.getText());}
                  else if(status.equals("xml")){xmlCurrentDTField.setDefaultValue(jtfDTDefaultValue.getText());}
               }
               dataSaved = false;
            }
         }
      ); //jbDTDefaultValue.addActionListener
      jtfDTDefaultValue = new JTextField();
      jtfDTDefaultValue.setEditable(false);

      jbDTVarchar = new JButton("Set Varchar Length");
      jbDTVarchar.setEnabled(false);
      jbDTVarchar.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               String prev = jtfDTVarchar.getText();
               String result = (String)JOptionPane.showInputDialog(
                    null,
                    "Enter the varchar length:",
                    "Varchar Length",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    prev);
               if ((result == null)) {
                  jtfDTVarchar.setText(prev);
                  return;
               }
               int selIndex = jlDTFieldsTablesAll.getSelectedIndex();
               int varchar;
               try {
                  if (result.length() > 5) {
                     JOptionPane.showMessageDialog(null, "Varchar length must be greater than 0 and less than or equal to 65535.");
                     jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                     return;
                  }
                  varchar = Integer.parseInt(result);
                  if (varchar > 0 && varchar <= 65535) { // max length of varchar is 255 before v5.0.3
                     jtfDTVarchar.setText(Integer.toString(varchar));
                     if(status.equals("edge")){currentDTField.setVarcharValue(varchar);}
                     else if(status.equals("xmk")){xmlCurrentDTField.setVarcharValue(varchar);}
                  } else {
                     JOptionPane.showMessageDialog(null, "Varchar length must be greater than 0 and less than or equal to 65535.");
                     jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                     return;
                  }
               } catch (NumberFormatException nfe) {
                  JOptionPane.showMessageDialog(null, "\"" + result + "\" is not a number");
                  jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
                  return;
               }
               dataSaved = false;
            }
         }
      );
      jtfDTVarchar = new JTextField();
      jtfDTVarchar.setEditable(false);
      
      jpDTCenterRight2 = new JPanel(new GridLayout(6, 1));
      jpDTCenterRight2.add(jbDTVarchar);
      jpDTCenterRight2.add(jtfDTVarchar);
      jpDTCenterRight2.add(jcheckDTPrimaryKey);
      jpDTCenterRight2.add(jcheckDTDisallowNull);
      jpDTCenterRight2.add(jbDTDefaultValue);
      jpDTCenterRight2.add(jtfDTDefaultValue);
      jpDTCenterRight.add(jpDTCenterRight1);
      jpDTCenterRight.add(jpDTCenterRight2);
      jpDTCenter.add(jpDTCenterRight);
      jfDT.getContentPane().add(jpDTCenter, BorderLayout.CENTER);
      jfDT.validate();
   } //createDTScreen

   public void createDRScreen() {
      //create Define Relations screen
      jfDR = new JFrame(DEFINE_RELATIONS);
      jfDR.setSize(HORIZ_SIZE, VERT_SIZE);
      jfDR.setLocation(HORIZ_LOC, VERT_LOC);
      jfDR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      jfDR.addWindowListener(edgeWindowListener);
      jfDR.getContentPane().setLayout(new BorderLayout());

      //setup menubars and menus
      jmbDRMenuBar = new JMenuBar();
      jfDR.setJMenuBar(jmbDRMenuBar);
      jmDRFile = new JMenu("File");
      jmDRFile.setMnemonic(KeyEvent.VK_F);
      jmbDRMenuBar.add(jmDRFile);
      jmiDROpenEdge = new JMenuItem("Open Edge File");
      jmiDROpenEdge.setMnemonic(KeyEvent.VK_E);
      jmiDROpenEdge.addActionListener(menuListener);
      jmiDROpenSave = new JMenuItem("Open Save File");
      jmiDROpenSave.setMnemonic(KeyEvent.VK_V);
      jmiDROpenSave.addActionListener(menuListener);
      jmiDROpenXml = new JMenuItem("Open XML File");
      jmiDROpenXml.setMnemonic(KeyEvent.VK_X);
      jmiDROpenXml.addActionListener(menuListener);
      jmiDROpenSaveXml = new JMenuItem("Open XMLSave File");
      jmiDROpenSaveXml.addActionListener(menuListener);
      jmiDRSave = new JMenuItem("Save");
      jmiDRSave.setMnemonic(KeyEvent.VK_S);
      jmiDRSave.setEnabled(false);
      jmiDRSave.addActionListener(menuListener);
      jmiDRSaveAs = new JMenuItem("Save As...");
      jmiDRSaveAs.setMnemonic(KeyEvent.VK_A);
      jmiDRSaveAs.setEnabled(false);
      jmiDRSaveAs.addActionListener(menuListener);
      jmiDRExit = new JMenuItem("Exit");
      jmiDRExit.setMnemonic(KeyEvent.VK_X);
      jmiDRExit.addActionListener(menuListener);
      jmDRFile.add(jmiDROpenEdge);
      jmDRFile.add(jmiDROpenXml);
      jmDRFile.addSeparator();
      jmDRFile.add(jmiDROpenSave);
      jmDRFile.add(jmiDROpenSaveXml);
      jmDTFile.addSeparator();
      jmDRFile.add(jmiDRSave);
      jmDRFile.add(jmiDRSaveAs);
      jmDRFile.add(jmiDRExit);

      jmDROptions = new JMenu("Options");
      jmDROptions.setMnemonic(KeyEvent.VK_O);
      jmbDRMenuBar.add(jmDROptions);
      jmiDROptionsOutputLocation = new JMenuItem("Set Output File Definition Location");
      jmiDROptionsOutputLocation.setMnemonic(KeyEvent.VK_S);
      jmiDROptionsOutputLocation.addActionListener(menuListener);
      jmiDROptionsShowProducts = new JMenuItem("Show Database Products Available");
      jmiDROptionsShowProducts.setMnemonic(KeyEvent.VK_H);
      jmiDROptionsShowProducts.setEnabled(false);
      jmiDROptionsShowProducts.addActionListener(menuListener);
      jmDROptions.add(jmiDROptionsOutputLocation);
      jmDROptions.add(jmiDROptionsShowProducts);

      jmDRHelp = new JMenu("Help");
      jmDRHelp.setMnemonic(KeyEvent.VK_H);
      jmbDRMenuBar.add(jmDRHelp);
      jmiDRHelpAbout = new JMenuItem("About");
      jmiDRHelpAbout.setMnemonic(KeyEvent.VK_A);
      jmiDRHelpAbout.addActionListener(menuListener);
      jmDRHelp.add(jmiDRHelpAbout);

      jpDRCenter = new JPanel(new GridLayout(2, 2));
      jpDRCenter1 = new JPanel(new BorderLayout());
      jpDRCenter2 = new JPanel(new BorderLayout());
      jpDRCenter3 = new JPanel(new BorderLayout());
      jpDRCenter4 = new JPanel(new BorderLayout());

      dlmDRTablesRelations = new DefaultListModel();
      jlDRTablesRelations = new JList(dlmDRTablesRelations);
      jlDRTablesRelations.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRTablesRelations.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDRTablesRelations.getElementAt(selIndex).toString();
                  setCurrentDRTable1(selText);
                  int[] currentNativeFields = null;
                  int[]currentRelatedTables = null;
                  int[] currentRelatedFields;
                  if(status.equals("edge")){
                      currentNativeFields = currentDRTable1.getNativeFieldsArray();
                      currentRelatedTables = currentDRTable1.getRelatedTablesArray();
                  }
                  if(status.equals("xml")){
                      currentNativeFields = xmlCurrentDRTable1.getNativeFieldsArray();
                      currentRelatedTables = xmlCurrentDRTable1.getRelatedTablesArray();
                  }
                  
                  jlDRFieldsTablesRelations.clearSelection();
                  jlDRTablesRelatedTo.clearSelection();
                  jlDRFieldsTablesRelatedTo.clearSelection();
                  dlmDRFieldsTablesRelations.removeAllElements();
                  dlmDRTablesRelatedTo.removeAllElements();
                  dlmDRFieldsTablesRelatedTo.removeAllElements();
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     dlmDRFieldsTablesRelations.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
                  for (int rIndex = 0; rIndex < currentRelatedTables.length; rIndex++) {
                     dlmDRTablesRelatedTo.addElement(getTableName(currentRelatedTables[rIndex]));
                  }
               }
            }
         }
      );

      dlmDRFieldsTablesRelations = new DefaultListModel();
      jlDRFieldsTablesRelations = new JList(dlmDRFieldsTablesRelations);
      jlDRFieldsTablesRelations.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRFieldsTablesRelations.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDRFieldsTablesRelations.getElementAt(selIndex).toString();
                  setCurrentDRField1(selText);
                  if(status.equals("edge")){
                    if (currentDRField1.getFieldBound() == 0) {
                       jlDRTablesRelatedTo.clearSelection();
                       jlDRFieldsTablesRelatedTo.clearSelection();
                       dlmDRFieldsTablesRelatedTo.removeAllElements();
                    } else {
                       jlDRTablesRelatedTo.setSelectedValue(getTableName(currentDRField1.getTableBound()), true);
                       jlDRFieldsTablesRelatedTo.setSelectedValue(getFieldName(currentDRField1.getFieldBound()), true);
                    }
                  }
                  
                  if(status.equals("xml")){
                      System.out.println("Hadasad");
                      //System.out.println(xmlCurrentDRField1.getName());
//                    if (xmlCurrentDRField1.getFieldBound() == 0) {
//                       jlDRTablesRelatedTo.clearSelection();
//                       jlDRFieldsTablesRelatedTo.clearSelection();
//                       dlmDRFieldsTablesRelatedTo.removeAllElements();
//                    } else {
//                       jlDRTablesRelatedTo.setSelectedValue(getTableName(xmlCurrentDRField1.getTableBound()), true);
//                       jlDRFieldsTablesRelatedTo.setSelectedValue(getFieldName(xmlCurrentDRField1.getFieldBound()), true);
//                    }
                  }
               }
            }
         }
      );

      dlmDRTablesRelatedTo = new DefaultListModel();
      jlDRTablesRelatedTo = new JList(dlmDRTablesRelatedTo);
      jlDRTablesRelatedTo.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRTablesRelatedTo.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDRTablesRelatedTo.getElementAt(selIndex).toString();
                  setCurrentDRTable2(selText);
                  int[] currentNativeFields = null;
                  if(status.equals("edge")){currentNativeFields = currentDRTable2.getNativeFieldsArray();}
                  if(status.equals("xml")){currentNativeFields = xmlCurrentDRTable2.getNativeFieldsArray();}
                  dlmDRFieldsTablesRelatedTo.removeAllElements();
                  for (int fIndex = 0; fIndex < currentNativeFields.length; fIndex++) {
                     dlmDRFieldsTablesRelatedTo.addElement(getFieldName(currentNativeFields[fIndex]));
                  }
               }
            }
         }
      );

      dlmDRFieldsTablesRelatedTo = new DefaultListModel();
      jlDRFieldsTablesRelatedTo = new JList(dlmDRFieldsTablesRelatedTo);
      jlDRFieldsTablesRelatedTo.addListSelectionListener(
         new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse)  {
               int selIndex = jlDRFieldsTablesRelatedTo.getSelectedIndex();
               if (selIndex >= 0) {
                  String selText = dlmDRFieldsTablesRelatedTo.getElementAt(selIndex).toString();
                  setCurrentDRField2(selText);
                  jbDRBindRelation.setEnabled(true);
               } else {
                  jbDRBindRelation.setEnabled(false);
               }
            }
         }
      );

      jspDRTablesRelations = new JScrollPane(jlDRTablesRelations);
      jspDRFieldsTablesRelations = new JScrollPane(jlDRFieldsTablesRelations);
      jspDRTablesRelatedTo = new JScrollPane(jlDRTablesRelatedTo);
      jspDRFieldsTablesRelatedTo = new JScrollPane(jlDRFieldsTablesRelatedTo);
      jlabDRTablesRelations = new JLabel("Tables With Relations", SwingConstants.CENTER);
      jlabDRFieldsTablesRelations = new JLabel("Fields in Tables with Relations", SwingConstants.CENTER);
      jlabDRTablesRelatedTo = new JLabel("Related Tables", SwingConstants.CENTER);
      jlabDRFieldsTablesRelatedTo = new JLabel("Fields in Related Tables", SwingConstants.CENTER);
      jpDRCenter1.add(jlabDRTablesRelations, BorderLayout.NORTH);
      jpDRCenter2.add(jlabDRFieldsTablesRelations, BorderLayout.NORTH);
      jpDRCenter3.add(jlabDRTablesRelatedTo, BorderLayout.NORTH);
      jpDRCenter4.add(jlabDRFieldsTablesRelatedTo, BorderLayout.NORTH);
      jpDRCenter1.add(jspDRTablesRelations, BorderLayout.CENTER);
      jpDRCenter2.add(jspDRFieldsTablesRelations, BorderLayout.CENTER);
      jpDRCenter3.add(jspDRTablesRelatedTo, BorderLayout.CENTER);
      jpDRCenter4.add(jspDRFieldsTablesRelatedTo, BorderLayout.CENTER);
      jpDRCenter.add(jpDRCenter1);
      jpDRCenter.add(jpDRCenter2);
      jpDRCenter.add(jpDRCenter3);
      jpDRCenter.add(jpDRCenter4);
      jfDR.getContentPane().add(jpDRCenter, BorderLayout.CENTER);
      jpDRBottom = new JPanel(new GridLayout(1, 3));

      jbDRDefineTables = new JButton(DEFINE_TABLES);
      jbDRDefineTables.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               jfDT.setVisible(true); //show the Define Tables screen
               jfDR.setVisible(false);
               clearDRControls();
               depopulateLists();
               if(status.equals("edge")){populateLists();}
               if(status.equals("xml")){populateXmlLists();}
            }
         }
      );

      jbDRBindRelation = new JButton("Bind/Unbind Relation");
      jbDRBindRelation.setEnabled(false);
      jbDRBindRelation.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               int nativeIndex = jlDRFieldsTablesRelations.getSelectedIndex();
               int relatedField = currentDRField2.getNumFigure();
               if (currentDRField1.getFieldBound() == relatedField) { //the selected fields are already bound to each other
                  int answer = JOptionPane.showConfirmDialog(null, "Do you wish to unbind the relation on field " +
                                                             currentDRField1.getName() + "?",
                                                             "Are you sure?", JOptionPane.YES_NO_OPTION);
                  if (answer == JOptionPane.YES_OPTION) {
                     currentDRTable1.setRelatedField(nativeIndex, 0); //clear the related field
                     currentDRField1.setTableBound(0); //clear the bound table
                     currentDRField1.setFieldBound(0); //clear the bound field
                     jlDRFieldsTablesRelatedTo.clearSelection(); //clear the listbox selection
                  }
                  return;
               }
               if (currentDRField1.getFieldBound() != 0) { //field is already bound to a different field
                  int answer = JOptionPane.showConfirmDialog(null, "There is already a relation defined on field " +
                                                             currentDRField1.getName() + ", do you wish to overwrite it?",
                                                             "Are you sure?", JOptionPane.YES_NO_OPTION);
                  if (answer == JOptionPane.NO_OPTION || answer == JOptionPane.CLOSED_OPTION) {
                     jlDRTablesRelatedTo.setSelectedValue(getTableName(currentDRField1.getTableBound()), true); //revert selections to saved settings
                     jlDRFieldsTablesRelatedTo.setSelectedValue(getFieldName(currentDRField1.getFieldBound()), true); //revert selections to saved settings
                     return;
                  }
               }
               if (currentDRField1.getDataType() != currentDRField2.getDataType()) {
                  JOptionPane.showMessageDialog(null, "The datatypes of " + currentDRTable1.getName() + "." +
                                                currentDRField1.getName() + " and " + currentDRTable2.getName() +
                                                "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                  return;
               }
               if ((currentDRField1.getDataType() == 0) && (currentDRField2.getDataType() == 0)) {
                  if (currentDRField1.getVarcharValue() != currentDRField2.getVarcharValue()) {
                     JOptionPane.showMessageDialog(null, "The varchar lengths of " + currentDRTable1.getName() + "." +
                                                   currentDRField1.getName() + " and " + currentDRTable2.getName() +
                                                   "." + currentDRField2.getName() + " do not match.  Unable to bind this relation.");
                     return;
                  }
               }
               currentDRTable1.setRelatedField(nativeIndex, relatedField);
               currentDRField1.setTableBound(currentDRTable2.getNumFigure());
               currentDRField1.setFieldBound(currentDRField2.getNumFigure());
               JOptionPane.showMessageDialog(null, "Table " + currentDRTable1.getName() + ": native field " +
                                             currentDRField1.getName() + " bound to table " + currentDRTable2.getName() +
                                             " on field " + currentDRField2.getName());
               dataSaved = false;
            }
         }
      );

      jbDRCreateDDL = new JButton("Create DDL");
      jbDRCreateDDL.setEnabled(false);
      jbDRCreateDDL.addActionListener(createDDLListener);

      jpDRBottom.add(jbDRDefineTables);
      jpDRBottom.add(jbDRBindRelation);
      jpDRBottom.add(jbDRCreateDDL);
      jfDR.getContentPane().add(jpDRBottom, BorderLayout.SOUTH);
   } //createDRScreen
   
   public static void setReadSuccess(boolean value) {
      readSuccess = value;
   }
   
   public static boolean getReadSuccess() {
      return readSuccess;
   }
   
   private void setCurrentDTTable(String selText) {
       //refac
        if(status.equals("edge")){
            for (int tIndex = 0; tIndex < tables.length; tIndex++) {
                if (selText.equals(tables[tIndex].getName())) {
                    currentDTTable = tables[tIndex];
                    return;
                }
            }
       }
        
        else if(status.equals("xml")){
            for (int tIndex = 0; tIndex < xmlTables.length; tIndex++) {
                if (selText.equals(xmlTables[tIndex].getName())) {
                    xmlCurrentDTTable = xmlTables[tIndex];
                    return;
                }
            }
       }
   }

   private void setCurrentDTField(String selText) {
      //refac
        if(status.equals("edge")){  
            for (int fIndex = 0; fIndex < fields.length; fIndex++) {
               if (selText.equals(fields[fIndex].getName()) && fields[fIndex].getTableID() == currentDTTable.getNumFigure()) {
                  currentDTField = fields[fIndex];
                  return;
               }
            }
        }
        
        else if(status.equals("xml")){  
            for (int fIndex = 0; fIndex < xmlFields.length; fIndex++) {
               if (selText.equals(xmlFields[fIndex].getName())) {
                  xmlCurrentDTField = xmlFields[fIndex];
                  return;
               }
            }
        }
   }

   private void setCurrentDRTable1(String selText) {
       if(status.equals("edge")){  
            for (int tIndex = 0; tIndex < tables.length; tIndex++) {
                if (selText.equals(tables[tIndex].getName())) {
                currentDRTable1 = tables[tIndex];
                return;
                }
            }
       }
       else if(status.equals("xml")){  
            for (int tIndex = 0; tIndex < xmlTables.length; tIndex++) {
                if (selText.equals(xmlTables[tIndex].getName())) {
                xmlCurrentDRTable1 = xmlTables[tIndex];
                return;
                }
            }
       }
   }

   private void setCurrentDRTable2(String selText) {
       if(status.equals("edge")){  
            for (int tIndex = 0; tIndex < tables.length; tIndex++) {
               if (selText.equals(tables[tIndex].getName())) {
                  currentDRTable2 = tables[tIndex];
                  return;
               }
            }
       }
       
       if(status.equals("xml")){  
            for (int tIndex = 0; tIndex < xmlTables.length; tIndex++) {
               if (selText.equals(xmlTables[tIndex].getName())) {
                  xmlCurrentDRTable2 = xmlTables[tIndex];
                  return;
               }
            }
       }
   }

   private void setCurrentDRField1(String selText) {
       if(status.equals("edge")){  
            for (int fIndex = 0; fIndex < fields.length; fIndex++) {
               if (selText.equals(fields[fIndex].getName()) &&
                   fields[fIndex].getTableID() == currentDRTable1.getNumFigure()) {
                  currentDRField1 = fields[fIndex];
                  return;
               }
            }
       }
       
       if(status.equals("xml")){  
            for (int fIndex = 0; fIndex < xmlFields.length; fIndex++) {
               if (selText.equals(xmlFields[fIndex].getName()) && xmlFields[fIndex].getTableID() == xmlCurrentDRTable1.getNumFigure()) {
                  xmlCurrentDRField1 = xmlFields[fIndex];
                  return;
               }
            }
       }
   }

   private void setCurrentDRField2(String selText) {
       if(status.equals("edge")){  
            for (int fIndex = 0; fIndex < fields.length; fIndex++) {
               if (selText.equals(fields[fIndex].getName()) &&
                   fields[fIndex].getTableID() == currentDRTable2.getNumFigure()) {
                  currentDRField2 = fields[fIndex];
                  return;
               }
            }
       }
       
       if(status.equals("xml")){  
            for (int fIndex = 0; fIndex < xmlFields.length; fIndex++) {
               if (selText.equals(xmlFields[fIndex].getName()) &&
                   xmlFields[fIndex].getTableID() == xmlCurrentDRTable2.getNumFigure()) {
                  xmlCurrentDRField2 = xmlFields[fIndex];
                  return;
               }
            }
       }
   }
   
   private String getTableName(int numFigure) {
       if(status.equals("edge")){  
            for (int tIndex = 0; tIndex < tables.length; tIndex++) {
               if (tables[tIndex].getNumFigure() == numFigure) {
                  return tables[tIndex].getName();
               }
            }
       }
       
       if(status.equals("xml")){  
            for (int tIndex = 0; tIndex < xmlTables.length; tIndex++) {
               if (xmlTables[tIndex].getNumFigure() == numFigure) {
                  return xmlTables[tIndex].getName();
               }
            }
       }
      return "";
   }
   
   private String getFieldName(int numFigure) {
       if(status.equals("edge")){  
            for (int fIndex = 0; fIndex < fields.length; fIndex++) {
               if (fields[fIndex].getNumFigure() == numFigure) {
                  return fields[fIndex].getName();
               }
            }
            
       }
       
       if(status.equals("xml")){  
            for (int fIndex = 0; fIndex < xmlFields.length; fIndex++) {
               if (xmlFields[fIndex].getNumFigure() == numFigure) {
                  return xmlFields[fIndex].getName();
               }
            }
            
       }
      return "";
   }
   
   //refac
   private String getXmlFieldName(int numFigure) {
      
         for (int fIndex = 0; fIndex < xmlFields.length; fIndex++) {
         if (xmlFields[fIndex].getNumFigure() == numFigure) {
            return xmlFields[fIndex].getName();
         }
      }
      return "";
     
   }
   
   private void enableControls() {
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i].setEnabled(true);
      }
      jcheckDTPrimaryKey.setEnabled(true);
      jcheckDTDisallowNull.setEnabled(true);
      jbDTVarchar.setEnabled(true);
      jbDTDefaultValue.setEnabled(true);
   }
   
   private void disableControls() {
      for (int i = 0; i < strDataType.length; i++) {
         jrbDataType[i].setEnabled(false);
      }
      jcheckDTPrimaryKey.setEnabled(false);
      jcheckDTDisallowNull.setEnabled(false);
      jbDTDefaultValue.setEnabled(false);
      jtfDTVarchar.setText("");
      jtfDTDefaultValue.setText("");
   }
   
   private void clearDTControls() {
      jlDTTablesAll.clearSelection();
      jlDTFieldsTablesAll.clearSelection();
   }
   
   private void clearDRControls() {
      jlDRTablesRelations.clearSelection();
      jlDRTablesRelatedTo.clearSelection();
      jlDRFieldsTablesRelations.clearSelection();
      jlDRFieldsTablesRelatedTo.clearSelection();
   }
   
   private void depopulateLists() {
      dlmDTTablesAll.clear();
      dlmDTFieldsTablesAll.clear();
      dlmDRTablesRelations.clear();
      dlmDRFieldsTablesRelations.clear();
      dlmDRTablesRelatedTo.clear();
      dlmDRFieldsTablesRelatedTo.clear();
   }
   
   private void populateLists() {
      if (readSuccess) {
         jfDT.setVisible(true);
         jfDR.setVisible(false);
         disableControls();
         depopulateLists();
         for (int tIndex = 0; tIndex < tables.length; tIndex++) {
            String tempName = tables[tIndex].getName();
            dlmDTTablesAll.addElement(tempName);
            int[] relatedTables = tables[tIndex].getRelatedTablesArray();
            if (relatedTables.length > 0) {
               dlmDRTablesRelations.addElement(tempName);
            }
         }
      }
      readSuccess = true;
   }
   
   //refac
   private void populateXmlLists() {
      if (readSuccess) {
         jfDT.setVisible(true);
         jfDR.setVisible(false);
         disableControls();
         depopulateLists();
         for (int tIndex = 0; tIndex < xmlTables.length; tIndex++) {
            String tempName = xmlTables[tIndex].getName();
            dlmDTTablesAll.addElement(tempName);
            int[] relatedTables = xmlTables[tIndex].getRelatedTablesArray();
                if (relatedTables.length > 0) {
                   dlmDRTablesRelations.addElement(tempName);
                }
         }
      }
      readSuccess = true;
   }
   
   private void saveAs() {
      int returnVal;
      jfcEdge.addChoosableFileFilter(effSave);
      returnVal = jfcEdge.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         saveFile = jfcEdge.getSelectedFile();
         if (saveFile.exists ()) {
             int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
             if (response == JOptionPane.CANCEL_OPTION) {
                return;
             }
         }
         if (!saveFile.getName().endsWith("sav")) {
            String temp = saveFile.getAbsolutePath() + ".sav";
            saveFile = new File(temp);
         }
         jmiDTSave.setEnabled(true);
         truncatedFilename = saveFile.getName().substring(saveFile.getName().lastIndexOf(File.separator) + 1);
         jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
         jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
      } else {
         return;
      }
      writeSave();
   }
   
   private void writeSave() {
      if (saveFile != null) {
         try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(saveFile, false)));
            //write the identification line
            pw.println(EdgeConvertFileParser.SAVE_ID);
            //write the tables 
            pw.println("#Tables#");
            for (int i = 0; i < tables.length; i++) {
               pw.println(tables[i]);
            }
            //write the fields
            pw.println("#Fields#");
            for (int i = 0; i < fields.length; i++) {
               pw.println(fields[i]);
            }
            //close the file
            pw.close();
         } catch (IOException ioe) {
            System.out.println(ioe);
         }
         dataSaved = true;
      }
   }
   
   private void xmlSaveAs() {
      int returnVal;
      jfcXml.addChoosableFileFilter(effSave);
      returnVal = jfcXml.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         saveFile = jfcXml.getSelectedFile();
         if (saveFile.exists ()) {
             int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
             if (response == JOptionPane.CANCEL_OPTION) {
                return;
             }
         }
         if (!saveFile.getName().endsWith("sav")) {
            String temp = saveFile.getAbsolutePath() + ".sav";
            saveFile = new File(temp);
         }
         jmiDTSave.setEnabled(true);
         truncatedFilename = saveFile.getName().substring(saveFile.getName().lastIndexOf(File.separator) + 1);
         jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
         jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
      } else {
         return;
      }
      xmlWriteSave();
   }
   
   private void xmlWriteSave() {
      if (saveFile != null) {
         try {
            File fXmlFile = saveFile;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            
            Element rootElement = doc.createElement("database");
            Attr dbName = doc.createAttribute("dbName");
            rootElement.setAttributeNode(dbName);
            doc.appendChild(rootElement);
            
            //write the tables 
            for (int i = 0; i < xmlTables.length; i++) {
                Element tableElement = doc.createElement("table");
               
                Attr tableName = doc.createAttribute("name");
                tableName.setValue(xmlTables[i].getName());
		tableElement.setAttributeNode(tableName);
                Attr tableId = doc.createAttribute("id");
                tableId.setValue(Integer.toString(xmlTables[i].getNumFigure()));
		tableElement.setAttributeNode(tableId);
               rootElement.appendChild(tableElement);
               
               for (int j = 0; j < xmlFields.length; j++) {
                   if(xmlFields[j].getTableID()== xmlTables[i].getNumFigure()){
                       Element fieldElement = doc.createElement("field");
                       
                       Attr fName = doc.createAttribute("name");
                       fName.setValue(xmlFields[j].getName());
                       fieldElement.setAttributeNode(fName);
                       
                       Attr fieldId = doc.createAttribute("fieldId");
                       fieldId.setValue(Integer.toString(xmlFields[j].getNumFigure()));
                       fieldElement.setAttributeNode(fieldId);
                       
                       Attr fTableId = doc.createAttribute("tableId");
                       fTableId.setValue(Integer.toString(xmlFields[j].getTableID()));
                       fieldElement.setAttributeNode(fTableId);
                       
                       Attr fDatatype = doc.createAttribute("datatype");
                       fDatatype.setValue(Integer.toString(xmlFields[j].getDataType()));
                       fieldElement.setAttributeNode(fDatatype);
                       
                       Attr fVarcharVval = doc.createAttribute("varchar_val");
                       fVarcharVval.setValue(Integer.toString(xmlFields[j].getVarcharValue()));
                       fieldElement.setAttributeNode(fVarcharVval);
                       
                       Attr fPrimaryKey = doc.createAttribute("primary_key");
                       fPrimaryKey.setValue(String.valueOf(xmlFields[j].getIsPrimaryKey()));
                       fieldElement.setAttributeNode(fPrimaryKey);
                       
                       Attr fNotNull = doc.createAttribute("not_null");
                       fNotNull.setValue(String.valueOf(xmlFields[j].getDisallowNull()));
                       fieldElement.setAttributeNode(fNotNull);
                       
                       Attr fDefault = doc.createAttribute("default");
                       fDefault.setValue(String.valueOf(xmlFields[j].getDefaultValue()));
                       fieldElement.setAttributeNode(fDefault);
                       
                       tableElement.appendChild(fieldElement);
                   }
                }
            }
            
            
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(saveFile);
                
                transformer.transform(source, result);
//            //write the fields
//            pw.println("#Fields#");
//            for (int i = 0; i < xmlFields.length; i++) {
//               pw.println(xmlFields[i]);
//            }
            //close the file
         } catch (Exception e) {
            System.out.println(e);
         }
         dataSaved = true;
      }
   }

   private void setOutputDir() {
      int returnVal;
      outputDirOld = outputDir;
      alSubclasses = new ArrayList();
      alProductNames = new ArrayList();

      returnVal = jfcOutputDir.showOpenDialog(null);
      
      if (returnVal == JFileChooser.CANCEL_OPTION) {
         return;
      }

      if (returnVal == JFileChooser.APPROVE_OPTION) {
         outputDir = jfcOutputDir.getSelectedFile();
      }
      
      getOutputClasses();

      if (alProductNames.size() == 0) {
         JOptionPane.showMessageDialog(null, "The path:\n" + outputDir + "\ncontains no valid output definition files.");
         outputDir = outputDirOld;
         return;
      }
      
      if ((parseFile != null || saveFile != null) && outputDir != null) {
         jbDTCreateDDL.setEnabled(true);
         jbDRCreateDDL.setEnabled(true);
      }

      JOptionPane.showMessageDialog(null, "The available products to create DDL statements are:\n" + displayProductNames());
      jmiDTOptionsShowProducts.setEnabled(true);
      jmiDROptionsShowProducts.setEnabled(true);
   }
   
   private String displayProductNames() {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < productNames.length; i++) {
         sb.append(productNames[i] + "\n");
      }
      return sb.toString();
   }
   
   private void getOutputClasses() {
      File[] resultFiles;
      Class resultClass = null;
      Class[] paramTypes = {EdgeTable[].class, EdgeField[].class};
      Class[] paramTypesXml = {XmlTable[].class, XmlField[].class};
      Class[] paramTypesNull = {};
      Constructor conResultClass;
      Object[] argsXml = {xmlTables, xmlFields};
      Object[] args = {tables, fields};
      Object objOutput = null;

      resultFiles = outputDir.listFiles();
      alProductNames.clear();
      alSubclasses.clear();
      try {
         for (int i = 0; i < resultFiles.length; i++) {
         System.out.println(resultFiles[i].getName());
            if (!resultFiles[i].getName().endsWith(".class")) {
               continue; //ignore all files that are not .class files
            }
            resultClass = Class.forName(resultFiles[i].getName().substring(0, resultFiles[i].getName().lastIndexOf(".")));
            if (resultClass.getSuperclass().getName().equals("EdgeConvertCreateDDL") && status.equals("edge")) { //only interested in classes that extend EdgeConvertCreateDDL
               if (parseFile == null && saveFile == null) {
                  conResultClass = resultClass.getConstructor(paramTypesNull);
                  } else {
                  conResultClass = resultClass.getConstructor(paramTypes);
                  objOutput = conResultClass.newInstance(args);
               }
               alSubclasses.add(objOutput);
               Method getProductName = resultClass.getMethod("getProductName", null);
               String productName = (String)getProductName.invoke(objOutput, null);
               alProductNames.add(productName);
            }
            
            if (resultClass.getSuperclass().getName().equals("XmlConvertCreateDDL") && status.equals("xml")) { //only interested in classes that extend EdgeConvertCreateDDL
               if (parseFile == null && saveFile == null) {
                  conResultClass = resultClass.getConstructor(paramTypesNull);
                  } else {
                  conResultClass = resultClass.getConstructor(paramTypesXml);
                  objOutput = conResultClass.newInstance(argsXml);
               }
               alSubclasses.add(objOutput);
               Method getProductName = resultClass.getMethod("getProductName", null);
               String productName = (String)getProductName.invoke(objOutput, null);
               alProductNames.add(productName);
            }
            
    
         }
      } catch (InstantiationException ie) {
         ie.printStackTrace();
      } catch (ClassNotFoundException cnfe) {
         cnfe.printStackTrace();
      } catch (IllegalAccessException iae) {
         iae.printStackTrace();
      } catch (NoSuchMethodException nsme) {
         nsme.printStackTrace();
      } catch (InvocationTargetException ite) {
         ite.printStackTrace();
      }
      if (alProductNames.size() > 0 && alSubclasses.size() > 0) { //do not recreate productName and objSubClasses arrays if the new path is empty of valid files
         productNames = (String[])alProductNames.toArray(new String[alProductNames.size()]);
         objSubclasses = (Object[])alSubclasses.toArray(new Object[alSubclasses.size()]);
      }
   }
   
   private String getSQLStatements() {
      String strSQLString = "";
      String response = (String)JOptionPane.showInputDialog(
                    null,
                    "Select a product:",
                    "Create DDL",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    productNames,
                    null);
                    
      if (response == null) {
         return EdgeConvertGUI.CANCELLED;
      }
      
      int selected;
      for (selected = 0; selected < productNames.length; selected++) {
         if (response.equals(productNames[selected])) {
            break;
         }
      }

      try {
         Class selectedSubclass = objSubclasses[selected].getClass();
         Method getSQLString = selectedSubclass.getMethod("getSQLString", null);
         Method getDatabaseName = selectedSubclass.getMethod("getDatabaseName", null);
         strSQLString = (String)getSQLString.invoke(objSubclasses[selected], null);
         databaseName = (String)getDatabaseName.invoke(objSubclasses[selected], null);
      } catch (IllegalAccessException iae) {
         iae.printStackTrace();
      } catch (NoSuchMethodException nsme) {
         nsme.printStackTrace();
      } catch (InvocationTargetException ite) {
         ite.printStackTrace();
      }

      return strSQLString;
   }

   private void writeSQL(String output) {
      if(status.equals("edge")){jfcEdge.resetChoosableFileFilters();}
      if(status.equals("xml")){jfcXml.resetChoosableFileFilters();}
      
      String str;
      if (parseFile != null) {
         outputFile = new File(parseFile.getAbsolutePath().substring(0, (parseFile.getAbsolutePath().lastIndexOf(File.separator) + 1)) + databaseName + ".sql");
      } else {
         outputFile = new File(saveFile.getAbsolutePath().substring(0, (saveFile.getAbsolutePath().lastIndexOf(File.separator) + 1)) + databaseName + ".sql");
      }
      if (databaseName.equals("")) {
         return;
      }
      if(status.equals("edge")){jfcEdge.setSelectedFile(outputFile);}
      if(status.equals("xml")){jfcXml.setSelectedFile(outputFile);}
      
      int returnVal = 0;
      if(status.equals("edge")){returnVal = jfcEdge.showSaveDialog(null);}
      if(status.equals("xml")){returnVal = jfcXml.showSaveDialog(null);}
      
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         if(status.equals("edge")){outputFile = jfcEdge.getSelectedFile();}
         if(status.equals("xml")){outputFile = jfcXml.getSelectedFile();}
         
         if (outputFile.exists ()) {
             int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
                                                         JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
             if (response == JOptionPane.CANCEL_OPTION) {
                return;
             }
         }
         try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, false)));
            //write the SQL statements
            pw.println(output);
            //close the file
            pw.close();
         } catch (IOException ioe) {
            System.out.println(ioe);
         }
      }
   }
   
   class EdgeRadioButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         for (int i = 0; i < jrbDataType.length; i++) {
            if (jrbDataType[i].isSelected()) {
                
                //refac
                
               if(status.equals("edge")){
                    currentDTField.setDataType(i);
                } 
               
               else if(status.equals("xml")){
                    xmlCurrentDTField.setDataType(i);
                } 
               
               break;
            }
         }
         if (jrbDataType[0].isSelected()) {
            jtfDTVarchar.setText(Integer.toString(EdgeField.VARCHAR_DEFAULT_LENGTH));
            jbDTVarchar.setEnabled(true);
         } else {
            jtfDTVarchar.setText("");
            jbDTVarchar.setEnabled(false);
         }
         jtfDTDefaultValue.setText("");
         if(status.equals("edge")){
                    currentDTField.setDefaultValue("");
         } 
         
         else if(status.equals("xml")){
                    xmlCurrentDTField.setDefaultValue("");
         } 
         
         dataSaved = false;
      }
   }
   
   class EdgeWindowListener implements WindowListener {
      public void windowActivated(WindowEvent we) {}
      public void windowClosed(WindowEvent we) {}
      public void windowDeactivated(WindowEvent we) {}
      public void windowDeiconified(WindowEvent we) {}
      public void windowIconified(WindowEvent we) {}
      public void windowOpened(WindowEvent we) {}
      
      public void windowClosing(WindowEvent we) {
         if (!dataSaved) {
            int answer = JOptionPane.showOptionDialog(null,
                "You currently have unsaved data. Would you like to save?",
                "Are you sure?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null);
            if (answer == JOptionPane.YES_OPTION) {
               if (saveFile == null) {
                  if(status.equals("edge")){saveAs();}
                  if(status.equals("xml")){xmlSaveAs();}
               }
               if(status.equals("edge")){writeSave();}
               if(status.equals("xml")){xmlWriteSave();}
            }
            if ((answer == JOptionPane.CANCEL_OPTION) || (answer == JOptionPane.CLOSED_OPTION)) {
               if (we.getSource() == jfDT) {
                  jfDT.setVisible(true);
               }
               if (we.getSource() == jfDR) {
                  jfDR.setVisible(true);
               }
               return;
            }
         }
         System.exit(0); //No was selected
      }
   }
   
   class CreateDDLButtonListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         while (outputDir == null) {
            JOptionPane.showMessageDialog(null, "You have not selected a path that contains valid output definition files yet.\nPlease select a path now.");
            setOutputDir();
         }
         getOutputClasses(); //in case outputDir was set before a file was loaded and EdgeTable/EdgeField objects created
         sqlString = getSQLStatements();
         if (sqlString.equals(EdgeConvertGUI.CANCELLED)) {
            return;
         }
         writeSQL(sqlString);
      }
   }

   class EdgeMenuListener implements ActionListener {
      public void actionPerformed(ActionEvent ae) {
         int returnVal;
         if ((ae.getSource() == jmiDTOpenEdge) || (ae.getSource() == jmiDROpenEdge)) {
            if (!dataSaved) {
               int answer = JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                                                          "Are you sure?", JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                  return;
               }
            }
            status = "edge";
            System.out.println(status);
            
            jfcEdge.addChoosableFileFilter(effEdge);
            returnVal = jfcEdge.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               parseFile = jfcEdge.getSelectedFile();
               ecfp = new EdgeConvertFileParser(parseFile);
               tables = ecfp.getEdgeTables();
               for (int i = 0; i < tables.length; i++) {
                  tables[i].makeArrays();
               }
               fields = ecfp.getEdgeFields();
               ecfp = null;
               populateLists();
               saveFile = null;
               jmiDTSave.setEnabled(false);
               jmiDRSave.setEnabled(false);
               jmiDTSaveAs.setEnabled(true);
               jmiDRSaveAs.setEnabled(true);
               jbDTDefineRelations.setEnabled(true);

               jbDTCreateDDL.setEnabled(true);
               jbDRCreateDDL.setEnabled(true);
               
               truncatedFilename = parseFile.getName().substring(parseFile.getName().lastIndexOf(File.separator) + 1);
               jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
               jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
            } else {
               return;
            }
            dataSaved = true;
         }
         //feat
         if ((ae.getSource() == jmiDTOpenXml) || (ae.getSource() == jmiDROpenXml)) {
            if (!dataSaved) {
               int answer = JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                                                          "Are you sure?", JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                  return;
               }
            }
            
            status = "xml";
            System.out.println(status);
            jfcXml.addChoosableFileFilter(effXml);
            returnVal = jfcXml.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               parseFile = jfcXml.getSelectedFile();
               xcfp = new XmlConvertFileParser(parseFile);
               xmlTables = xcfp.getXmlTables();
               for (int i = 0; i < xmlTables.length; i++) {
                  xmlTables[i].makeArrays();
               }
               xmlFields = xcfp.getXmlFields();
               xcfp = null;
               populateXmlLists();
               saveFile = null;
               jmiDTSave.setEnabled(false);
               jmiDRSave.setEnabled(false);
               jmiDTSaveAs.setEnabled(true);
               jmiDRSaveAs.setEnabled(true);
               jbDTDefineRelations.setEnabled(true);

               jbDTCreateDDL.setEnabled(true);
               jbDRCreateDDL.setEnabled(true);
               
               truncatedFilename = parseFile.getName().substring(parseFile.getName().lastIndexOf(File.separator) + 1);
               jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
               jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
            } else {
               return;
            }
            dataSaved = true;
         }
         
         if ((ae.getSource() == jmiDTOpenSaveXml) || (ae.getSource() == jmiDROpenSaveXml)) {
            if (!dataSaved) {
               int answer = JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                                                          "Are you sure?", JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                  return;
               }
            }
            
            status = "xml";
            System.out.println(status);
            jfcXml.addChoosableFileFilter(effSave);
            returnVal = jfcXml.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               saveFile = jfcXml.getSelectedFile();
               xcfp = new XmlConvertFileParser(saveFile);
               xmlTables = xcfp.getXmlTables();
               xmlFields = xcfp.getXmlFields();
               for (int i = 0; i < xmlTables.length; i++) {
                  xmlTables[i].makeArrays();
               }
               xcfp = null;
               populateXmlLists();
               parseFile = null;
               jmiDTSave.setEnabled(true);
               jmiDRSave.setEnabled(true);
               jmiDTSaveAs.setEnabled(true);
               jmiDRSaveAs.setEnabled(true);
               jbDTDefineRelations.setEnabled(true);

               jbDTCreateDDL.setEnabled(true);
               jbDRCreateDDL.setEnabled(true);

               truncatedFilename = saveFile.getName().substring(saveFile.getName().lastIndexOf(File.separator) + 1);
               jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
               jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
            } else {
               return;
            }
            dataSaved = true;
         }
         
         if ((ae.getSource() == jmiDTOpenSave) || (ae.getSource() == jmiDROpenSave)) {
            if (!dataSaved) {
               int answer = JOptionPane.showConfirmDialog(null, "You currently have unsaved data. Continue?",
                                                          "Are you sure?", JOptionPane.YES_NO_OPTION);
               if (answer != JOptionPane.YES_OPTION) {
                  return;
               }
            }
            status = "edge";
            System.out.println(status);
            jfcEdge.addChoosableFileFilter(effSave);
            returnVal = jfcEdge.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               saveFile = jfcEdge.getSelectedFile();
               ecfp = new EdgeConvertFileParser(saveFile);
               tables = ecfp.getEdgeTables();
               fields = ecfp.getEdgeFields();
               ecfp = null;
               populateLists();
               parseFile = null;
               jmiDTSave.setEnabled(true);
               jmiDRSave.setEnabled(true);
               jmiDTSaveAs.setEnabled(true);
               jmiDRSaveAs.setEnabled(true);
               jbDTDefineRelations.setEnabled(true);

               jbDTCreateDDL.setEnabled(true);
               jbDRCreateDDL.setEnabled(true);

               truncatedFilename = saveFile.getName().substring(saveFile.getName().lastIndexOf(File.separator) + 1);
               jfDT.setTitle(DEFINE_TABLES + " - " + truncatedFilename);
               jfDR.setTitle(DEFINE_RELATIONS + " - " + truncatedFilename);
            } else {
               return;
            }
            dataSaved = true;
         }
         
         if ((ae.getSource() == jmiDTSaveAs) || (ae.getSource() == jmiDRSaveAs) ||
             (ae.getSource() == jmiDTSave) || (ae.getSource() == jmiDRSave)) {
            if ((ae.getSource() == jmiDTSaveAs) || (ae.getSource() == jmiDRSaveAs)) {
               if(status.equals("edge")){saveAs();}
               else if(status.equals("xml")){xmlSaveAs();}
            } else {
               if(status.equals("edge")){writeSave();}
               if(status.equals("xml")){xmlWriteSave();}
            }
         }
         
         if ((ae.getSource() == jmiDTExit) || (ae.getSource() == jmiDRExit)) {
            if (!dataSaved) {
               int answer = JOptionPane.showOptionDialog(null,
                   "You currently have unsaved data. Would you like to save?",
                   "Are you sure?",
                   JOptionPane.YES_NO_CANCEL_OPTION,
                   JOptionPane.QUESTION_MESSAGE,
                   null, null, null);
               if (answer == JOptionPane.YES_OPTION) {
                  if (saveFile == null) {
                     if(status.equals("edge")){saveAs();}
                     else if(status.equals("xml")){xmlSaveAs();} 
                  }
               }
               if ((answer == JOptionPane.CANCEL_OPTION) || (answer == JOptionPane.CLOSED_OPTION)) {
                  return;
               }
            }
            System.exit(0); //No was selected
         }
         
         if ((ae.getSource() == jmiDTOptionsOutputLocation) || (ae.getSource() == jmiDROptionsOutputLocation)) {
            setOutputDir();
         }

         if ((ae.getSource() == jmiDTOptionsShowProducts) || (ae.getSource() == jmiDROptionsShowProducts)) {
            JOptionPane.showMessageDialog(null, "The available products to create DDL statements are:\n" + displayProductNames());
         }
         
         if ((ae.getSource() == jmiDTHelpAbout) || (ae.getSource() == jmiDRHelpAbout)) {
            JOptionPane.showMessageDialog(null, "EdgeConvert ERD To DDL Conversion Tool\n" +
                                                "by Stephen A. Capperell\n" +
                                                "� 2007-2008");
         }
      } // EdgeMenuListener.actionPerformed()
   } // EdgeMenuListener
} // EdgeConvertGUI
