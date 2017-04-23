/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Hypemaster
 */
public class XmlConnector {
    private int numConnector, endPoint1, endPoint2;
    private String endStyle1, endStyle2;
    private boolean isEP1Field, isEP2Field, isEP1Table, isEP2Table;

    public XmlConnector(String numConnector, String endPoint1, String endPoint2, String endStyle1,
            String endStyle2) {
       this.numConnector = Integer.parseInt(numConnector);
       this.endPoint1 = Integer.parseInt(endPoint1);
       this.endPoint2 = Integer.parseInt(endPoint2);
       this.endStyle1 = endStyle1;
       this.endStyle2 = endStyle2;
       isEP1Field = false;
       isEP2Field = false;
       isEP1Table = false;
       isEP2Table = false;
    }

    public int getNumConnector() {
       return numConnector;
    }

    public int getEndPoint1() {
       return endPoint1;
    }

    public int getEndPoint2() {
       return endPoint2;
    }

    public String getEndStyle1() {
       return endStyle1;
    }

    public String getEndStyle2() {
       return endStyle2;
    }
    public boolean getIsEP1Field() {
       return isEP1Field;
    }

    public boolean getIsEP2Field() {
       return isEP2Field;
    }

    public boolean getIsEP1Table() {
       return isEP1Table;
    }

    public boolean getIsEP2Table() {
       return isEP2Table;
    }

    public void setIsEP1Field(boolean value) {
       isEP1Field = value;
    }

    public void setIsEP2Field(boolean value) {
       isEP2Field = value;
    }

    public void setIsEP1Table(boolean value) {
       isEP1Table = value;
    }

    public void setIsEP2Table(boolean value) {
       isEP2Table = value;
    }
}
