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
    
    public Send(InetAddress serverAddress, int serverPort, byte[] cipherText) {
        this.PORT = serverPort;
        this.serverAddress = serverAddress;
        this.cipherText = cipherText;
    } 
    
     
     @Override protected Void call() throws Exception {         

         
        Socket socket = new Socket(serverAddress, PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        
        try {
            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }
            this.out.writeInt(cipherText.length);
            this.out.write(cipherText,0,cipherText.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała " + new String(cipherText));
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        //out.close();
        return null;
    }

}