/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Aleksandra
 */
public class DiskElement implements Comparable<DiskElement>  {

    protected File file;
    public Boolean isDirectory;
    public String name;
    protected int depth;
    protected  long size;
    
    protected DiskElement(File f, int depth){
        file =f;
        name=file.getName();
        isDirectory=file.isDirectory();
        this.depth=depth;
    }

    protected String print(int level) {
        String list = "";
        if (this.depth <= level) {
            for (int i = 0; i < this.depth; i++) {
                list += "-";
            }

            list += name + "\n";
           /* for (int i = 40 - name.length() - this.depth; i > 0; i--) {
                list += " ";
            }

            for (int i = 10 - String.valueOf(size).length(); i > 0; i--) {
                list+= " ";
            }
             list += "bytes";*/
            
        }
        return list;
    }
    @Override
    public int hashCode() {
        return (367 * depth + this.file.hashCode()+Integer.parseInt(name));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (getClass() != obj.getClass()) return false;
        else return (this.compareTo((DiskElement) obj) == 0);
    }
    
    // compare by file's name
    @Override
    public int compareTo(DiskElement o1){
        String fName= o1.name;
        return name.compareTo(fName);
    }
    
}
