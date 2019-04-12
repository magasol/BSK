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
    private Socket connection;
    public ServerSocket serverSocket;

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

    public byte[] listen() {
        try {
            this.connection = serverSocket.accept();
            this.out = new ObjectOutputStream(connection.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(connection.getInputStream());
            
            String text = ""; //zmienic na byte
            String input; //zmienić na byte[]

                if ((input = (String)this.in.readObject()) != null) {
                    text = text + "\n" + input;
                }
            System.out.println("Serwer odebrał: " + text);

            return text.getBytes();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void send(int port, byte[] encryptedText) {
        try {
            this.out.writeObject(new String(encryptedText));
            this.out.flush();
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
