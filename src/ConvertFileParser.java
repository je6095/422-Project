
import java.io.File;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hassan Jegan Ndow
 */
public abstract class ConvertFileParser {
    
    protected abstract boolean isTableDup(String testTableName);
    protected abstract void makeArrays();
    public abstract void openFile(File inputFile);
    public abstract void parseFile() throws Exception;
    protected  abstract void resolveConnectors();
}
