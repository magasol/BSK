/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.tcp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author Aleksandra
 */
public class Send extends Task<Void> {
    
    private int PORT;
    byte[] cipherText;
    InetAddress serverAddress;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    boolean flag = true;
    byte[] type;
    byte[] path;
    
    public Send(InetAddress serverAddress, int serverPort, byte[] cipherText, byte[] type, byte[] path) {
        this.PORT = serverPort;
        this.serverAddress = serverAddress;
        this.cipherText = cipherText;
        this.type = type;
        this.path = path;
    } 
    
     
     @Override protected Void call() throws Exception {         
         
        Socket socket = new Socket(serverAddress, PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        
        try {
            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }
            
            this.out.writeInt(path.length);
            this.out.write(path,0,path.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała ścieżke: " + new String(path));
            
            this.out.writeInt(type.length);
            this.out.write(type,0,type.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała tryb kodowania: " + new String(type));
            
            this.out.writeInt(cipherText.length);
            this.out.write(cipherText,0,cipherText.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała " + new String(cipherText));
            receive(socket);
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        //out.close();
        return null;
    }

    public byte[] receive(Socket socket) {
        while(flag)
        {
            try {
                this.in = new ObjectInputStream(socket.getInputStream());
                int len = in.readInt();
                byte[] encryptedText = new byte[len];
                
                if (len > 0) {
                    in.readFully(encryptedText);
                    System.out.println("Aplikacja odebrała: " + new String(encryptedText));
                    flag = false;
                }                    
                
                return encryptedText;
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        flag = true;
        return null;
    }
     
}