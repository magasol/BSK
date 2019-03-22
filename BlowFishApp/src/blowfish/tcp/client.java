/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfish.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import javax.crypto.SecretKey;

/**
 *
 * @author Aleksandra
 */
public class client {
    
    Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    public client(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public void send(int port, String cipherText)
    {
        if (!socket.isConnected()) {
            System.out.println("Aplikacja nie połączyła się z serwerem");
        }
        
        out.write(cipherText);
    }
    
    public void stop() throws IOException
    {
        socket.close();
        in.close();
        out.close();
    }
    
}