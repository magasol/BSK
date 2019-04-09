/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            String text = ""; //zmienic na byte

            final Socket clientSocket = serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String input; //zmienić na byte[]
            if (in.ready()) {
                while ((input = in.readLine()) != null) {
                    text = text + "\n" + input;
                }
            }

            System.out.println("Serwer odebrał: " + text);
            clientSocket.close();
            in.close();

            return text.getBytes();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void send(int port, byte[] encryptedText) {
        try {
            final Socket clientSocket = serverSocket.accept();
            ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
            os.write(encryptedText);
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            serverSocket.close();
            System.out.println("Serwer został zamknięty.");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
