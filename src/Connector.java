/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hassan Jegan Ndow
 */
public abstract class Connector {
    
    public abstract int getNumConnector();
    public abstract int getEndPoint1();
    public abstract int getEndPoint2();
    public abstract String getEndStyle1();
    public abstract String getEndStyle2();
    public abstract boolean getIsEP1Field();
    public abstract boolean getIsEP2Field();
    public abstract boolean getIsEP1Table();
    public abstract boolean getIsEP2Table();
    public abstract void setIsEP1Field(boolean value);
    public abstract void setIsEP2Field(boolean value);
    public abstract void setIsEP1Table(boolean value);
    public abstract void setIsEP2Table(boolean value);
    
}
