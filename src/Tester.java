

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;

/**
 *
 * @author Hassan Jegan Ndow
 */
public class Tester {
    
    public static void main(String[] args) {
        
        File file = new File("testXml.xml");
        
        XmlConvertFileParser xml = new XmlConvertFileParser(file);
    }
    
}
