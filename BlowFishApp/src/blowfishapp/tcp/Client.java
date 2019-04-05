/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksandra
 */
public class Client {

    Socket socket;
    private DataOutputStream out;
    private BufferedReader in;

    public Client(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        //this.out = new PrintWriter(socket.getOutputStream(), true);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Klient został stworzony.");
    }

    public void send(String cipherText) {
        try {
            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }

            //out.write(cipherText);
            this.out.writeBytes(cipherText);
            System.out.println("Aplikacja wysłała " + cipherText);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() throws IOException {
        socket.close();
        in.close();
        out.close();
        System.out.println("Klient został zamknięty.");
    }

}
