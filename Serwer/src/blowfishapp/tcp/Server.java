/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksandra
 */
public class Server {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    //private Socket connection;
    public ServerSocket serverSocket;
    public byte[] type;
    public byte[] filePath;

    public Server(int port, InetAddress serverAddress) {
        try {
            int backlog = 0;
            this.serverSocket = new ServerSocket(port, backlog, serverAddress);
            
            System.out.println("Serwer został stworzony.");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getAddress() {
        return serverSocket.getInetAddress().toString();
    }

    public byte[] listen(Socket connection) {
        try {
            //this.connection = serverSocket.accept();
            this.out = new ObjectOutputStream(connection.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(connection.getInputStream());
            int len = in.readInt(); 
            byte[] path = new byte[len];
            if(len > 0){
                in.readFully(path);
            }
            System.out.println("Serwer odebrał ścieżke do pliku: " + new String(path));
            this.filePath = path;
            
            len = in.readInt(); 
            byte[] type = new byte[len];
            if(len > 0)
            {
                in.readFully(type);
            }
            System.out.println("Serwer odebrał tryb kodowania: " + new String(type));
            this.type = type;
            
            len = in.readInt(); 
            byte[] encryptedText = new byte[len];
            if (len > 0) {
                in.readFully(encryptedText);
            }
            System.out.println("Serwer odebrał: " + new String(encryptedText));

            return encryptedText;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void send(int port, byte[] encryptedText) {
        try {
            this.out.writeInt(encryptedText.length);
            this.out.write(encryptedText,0,encryptedText.length);
            this.out.flush();
            System.out.println("serwer wysłał " + new String(encryptedText));
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
            this.in.close();
            this.out.close();
            System.out.println("Serwer został zamknięty.");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
