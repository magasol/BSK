/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author Aleksandra
 */
public class Client {
    
    Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    public Client(InetAddress serverAddress, int serverPort) throws Exception{
        this.socket = new Socket(serverAddress, serverPort);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Klient został stworzony.");
    }
    
    public void send(String cipherText)
    {
        if (!socket.isConnected()) {
            System.out.println("Aplikacja nie połączyła się z serwerem/n");
        }
        
        out.write(cipherText);
        System.out.println("Aplikacja wysłała " + cipherText + "/n");
    }
    
    public void stop() throws IOException
    {
        socket.close();
        in.close();
        out.close();
        System.out.println("Klient został zamknięty./n");
    }
    
}