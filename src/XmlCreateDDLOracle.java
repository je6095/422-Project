
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hassan Jegan Ndow
 */
public class XmlCreateDDLOracle extends XmlConvertCreateDDL{
    protected String databaseName;
   //this array is for determining how MySQL refers to datatypes
   protected String[] strDataType = {"VARCHAR2", "CHAR(1)", "NUMBER(10)", "Binary_double"};
   protected StringBuffer sb;

   public XmlCreateDDLOracle(XmlTable[] inputTables, XmlField[] inputFields) {
      super(inputTables, inputFields);
      sb = new StringBuffer();
   } //CreateDDLMySQL(EdgeTable[], EdgeField[])
   
   public XmlCreateDDLOracle() { //default constructor with empty arg list for to allow output dir to be set before there are table and field objects
      
   }
   
   public void createDDL() {
      EdgeConvertGUI.setReadSuccess(true);
      databaseName = generateDatabaseName();
      sb.append("CREATE USER " + databaseName + " IDENTIFIED BY " + databaseName + ";\r\n");
      sb.append("ALTER SESSION SET CURRENT_SCHEMA = " + databaseName + ";\r\n");
      System.out.println("Table length: " + numBoundTables.length);
         for (int tableCount = 0; tableCount < numBoundTables.length; tableCount++) { //step through list of tables
               sb.append("CREATE TABLE " + tables[tableCount].getName() + " (\r\n");
               int[] nativeFields = tables[tableCount].getNativeFieldsArray();
               int[] relatedFields = tables[tableCount].getRelatedFieldsArray();
               boolean[] primaryKey = new boolean[nativeFields.length];
               int numPrimaryKey = 0;
               int numForeignKey = 0;
               for (int nativeFieldCount = 0; nativeFieldCount < nativeFields.length; nativeFieldCount++) { //print out the fields
                  XmlField currentField = getField(nativeFields[nativeFieldCount]);
                  sb.append("\t" + currentField.getName() + " " + strDataType[currentField.getDataType()]);
                  if (currentField.getDataType() == 0) { //varchar
                     sb.append("(" + currentField.getVarcharValue() + ")"); //append varchar length in () if data type is varchar
                  }
                  if (currentField.getDisallowNull()) {
                     sb.append(" NOT NULL");
                  }
                  if (!currentField.getDefaultValue().equals("")) {
                     if (currentField.getDataType() == 1) { //boolean data type
                        sb.append(" DEFAULT " + convertStrBooleanToInt(currentField.getDefaultValue()));
                     } else { //any other data type
                        sb.append(" DEFAULT " + currentField.getDefaultValue());
                     }
                  }
                  if (currentField.getIsPrimaryKey()) {
                     primaryKey[nativeFieldCount] = true;
                     numPrimaryKey++;
                  } else {
                     primaryKey[nativeFieldCount] = false;
                  }
                  if (currentField.getFieldBound() != 0) {
                     numForeignKey++;
                  }
                  sb.append(",\r\n"); //end of field
               }
               if (numPrimaryKey > 0) { //table has primary key(s)
                  sb.append("CONSTRAINT " + tables[tableCount].getName() + "_PK PRIMARY KEY (");
                  for (int i = 0; i < primaryKey.length; i++) {
                     if (primaryKey[i]) {
                        sb.append(getField(nativeFields[i]).getName());
                        numPrimaryKey--;
                        if (numPrimaryKey > 0) {
                           sb.append(", ");
                        }
                     }
                  }
                  sb.append(")");
                  if (numForeignKey > 0) {
                     sb.append(",");
                  }
                  sb.append("\r\n");
               }
               
               if (numForeignKey > 0) { //table has foreign keys
                  int currentFK = 1;
                  for (int i = 0; i < relatedFields.length; i++) {
                     if (relatedFields[i] != 0) {
                        sb.append("CONSTRAINT " + tables[tableCount].getName() + "_FK" + currentFK +
                                  " FOREIGN KEY(" + getField(nativeFields[i]).getName() + ") REFERENCES " +
                                  getTable(getField(nativeFields[i]).getTableBound()).getName() + "(" + getField(relatedFields[i]).getName() + ")");
                        if (currentFK < numForeignKey) {
                           sb.append(",\r\n");
                        }
                        currentFK++;
                     }
                  }
                  sb.append("\r\n");
               }
              
               sb.append(");\r\n\r\n"); //end of table
            
         }
      
   }

   protected int convertStrBooleanToInt(String input) { //MySQL uses '1' and '0' for boolean types
      if (input.equals("true")) {
         return 1;
      } else {
         return 0;
      }
   }
   
   public String generateDatabaseName() { //prompts user for database name
      String dbNameDefault = "MySQLDB";
      //String databaseName = "";

      do {
         databaseName = (String)JOptionPane.showInputDialog(
                       null,
                       "Enter the database name:",
                       "Database Name",
                       JOptionPane.PLAIN_MESSAGE,
                       null,
                       null,
                       dbNameDefault);
         if (databaseName == null) {
            EdgeConvertGUI.setReadSuccess(false);
            return "";
         }
         if (databaseName.equals("")) {
            JOptionPane.showMessageDialog(null, "You must select a name for your database.");
         }
      } while (databaseName.equals(""));
      return databaseName;
   }
   
   public String getDatabaseName() {
      return databaseName;
   }
   
   public String getProductName() {
      return "XML to Oracle";
   }

   public String getSQLString() {
      createDDL();
      return sb.toString();
   }
}
