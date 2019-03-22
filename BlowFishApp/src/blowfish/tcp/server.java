/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfish.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Aleksandra
 */
public class server {
    
    ServerSocket socket;
    
    public server(int port) throws IOException
    {
        this.socket = new ServerSocket(port);
    }
    
    public String listen(int port) throws IOException
    {
        String encryptedFile = null;
        
        final Socket clientSocket = socket.accept();
                
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));        
        
        String input = null;
            
        while ( (input = in.readLine()) != null ) {
            encryptedFile = encryptedFile + "/n" + input;
        }
               
        clientSocket.close();
        in.close();
        
        return encryptedFile;
    }
    
    public void stop() throws IOException
    {
        socket.close();
    }
}
