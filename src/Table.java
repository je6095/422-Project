/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hassan Jegan Ndow
 */
public abstract class Table {
    public abstract int getNumFigure();
    public abstract String getName();
    public abstract void addRelatedTable(int relatedTable);
    public abstract int[] getRelatedTablesArray();
    public abstract int[] getRelatedFieldsArray();
    public abstract void setRelatedField(int index, int relatedValue);
    public abstract int[] getNativeFieldsArray();
    public abstract void addNativeField(int value);
    public abstract void moveFieldUp(int index);
    public abstract void moveFieldDown(int index);
    public abstract void makeArrays();
    public abstract String toString();
    
}
