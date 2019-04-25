/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.file;

import java.util.Comparator;

/**
 *
 * @author Aleksandra
 */
public class MyComparator implements Comparator {


    // compare by file's name
    @Override
    public int compare(Object o1, Object o2) {

        String fName= ((DiskElement) o1).name;
        String sName=((DiskElement)o2).name;
        return fName.compareTo(sName);

    }
    
}
