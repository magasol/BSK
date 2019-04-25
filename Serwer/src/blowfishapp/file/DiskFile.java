/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.file;

import java.io.File;

/**
 *
 * @author Aleksandra
 */
public class DiskFile extends DiskElement  {
    public DiskFile(File f, int depth){
        super(f,depth);
        isDirectory=false;
        size=f.length();
    }
    
}
