/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Aleksandra
 */
public class Server {
    
    public ServerSocket socket;
    
    
    public Server(int port) throws IOException
    {
        this.socket = new ServerSocket(port);
        System.out.println("Serwer został stworzony.");
    }
    
    public String getAddress()
    {
        return socket.getInetAddress().toString();
    }
    
    public String listen() throws IOException
    {
        String encryptedFile = null;
        
        final Socket clientSocket = socket.accept();
                
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));        
        
        String input = null;
            
        while ( (input = in.readLine()) != null ) {
            encryptedFile = encryptedFile + "\n" + input;
        }
         
        System.out.println("Serwer odebrł: " + encryptedFile);
        clientSocket.close();
        in.close();
        
        return encryptedFile;
    }
    
    public void stop() throws IOException
    {
        socket.close();
        System.out.println("Serwer został zamknięty.");
    }
}
