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
    protected Boolean isDirectory;
    protected String name;
    protected int depth;
    protected  long size;
    
    protected DiskElement(File f, int depth){
        file =f;
        name=file.getName();
        isDirectory=file.isDirectory();
        this.depth=depth;
    }

    protected void print(int level) {
        if (this.depth <= level) {
            for (int i = 0; i < this.depth; i++) {
                System.out.print("-");
            }
            System.out.print(name);
            for (int i = 40 - name.length() - this.depth; i > 0; i--) {
                System.out.print(" ");
            }

            for (int i = 10 - String.valueOf(size).length(); i > 0; i--) {
                System.out.print(" ");
            }
             System.out.println("bytes");
        }
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
