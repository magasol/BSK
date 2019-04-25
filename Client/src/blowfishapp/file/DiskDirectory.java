/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.file;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Aleksandra
 */
public class DiskDirectory extends DiskElement {
    private Set<DiskElement> children;
    private boolean isSort = true;
    private MyComparator comparator;


    public DiskDirectory(File f, int depth, MyComparator comparator){
       super(f,depth);

       size=folderSize(f);
       this.comparator=comparator;
       if(isSort) {
          if(comparator==null) {
              children = new TreeSet<>();
          }
          else{
              children = new TreeSet<DiskElement>(comparator);
          }
       }
       else{
           children = new HashSet<>();
       }
      addChildren(f,depth);

    }

    public  long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public void print(int level){
        super.print(level);
       for(DiskElement de : children){
          if( de.depth<=level){
              de.print(level);
          }
       }

    }

void addChildren(File f,int depth )
{
    File[] files = f.listFiles();

    for(int i=0; i<files.length; i++){
        DiskElement newEl;
        if(files[i].isDirectory()){
          newEl =new DiskDirectory(files[i],depth+1, comparator);
            children.add(newEl);

        }
        else if(files[i].isFile()){
            newEl=new DiskFile(files[i],depth+1);
            children.add(newEl);
        }
        else {
        System.out.println("Undefinrd object.");
        }

    }
}
    
}
