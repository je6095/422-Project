/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hassan Jegan Ndow
 */
public abstract class Field {
    
    public abstract int getNumFigure();
    public abstract String getName();
    public abstract int getTableID();
    public abstract void setTableID(int value);
    public abstract int getTableBound();
    public abstract void setTableBound(int value);
    public abstract int getFieldBound();
    public abstract void setFieldBound(int value);
    public abstract boolean getDisallowNull();
    public abstract void setDisallowNull(boolean value);
    public abstract boolean getIsPrimaryKey();
    public abstract void setIsPrimaryKey(boolean value);
    public abstract String getDefaultValue();
    public abstract void setDefaultValue(String value);
    public abstract int getVarcharValue();
    public abstract void setVarcharValue(int value);
    public abstract int getDataType();
    public abstract void setDataType(int value);
    public abstract String toString();
}
