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
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author Aleksandra
 */
public class Login extends Task<String> {

    public String result;
    private ObjectOutputStream out;
    private int PORT;
    private InetAddress serverAddress;
    String login;
    String password;

    public Login(InetAddress serverAddress, int serverPort, String login, String password) {
        this.PORT = serverPort;
        this.serverAddress = serverAddress;
        this.login = login;
        this.password = password;
    }

    @Override
    public String call() throws Exception {
        byte[] text = "login".getBytes();
        byte[] passwordBytes = password.getBytes();
        byte[] LoginBytes = login.getBytes();
        Socket socket = new Socket(this.serverAddress, this.PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();

        try {
            if (!socket.isConnected()) {
                //System.out.println("Aplikacja nie połączyła się z serwerem");
            }

            this.out.writeInt(text.length);
            this.out.write(text, 0, text.length);
            this.out.flush();
            //System.out.println("Aplikacja wysłała prosbe o sprawdzenie logowania");

            this.out.writeInt(LoginBytes.length);
            this.out.write(LoginBytes, 0, LoginBytes.length);
            this.out.flush();
            //System.out.println("Aplikacja wysłała login");

            byte[] passwordHashBytes = hashPassword(passwordBytes);

            this.out.writeInt(passwordHashBytes.length);
            this.out.write(passwordHashBytes, 0, passwordHashBytes.length);
            this.out.flush();
            //System.out.println("Aplikacja wysłała hasło");

            receiveResult(socket);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.result;
    }

    private byte[] hashPassword(byte[] passwordBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // wszystie inne dają wiecej niz 16 bajtów
            md.update(passwordBytes);
            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void receiveResult(Socket socket) throws IOException {
        boolean flag = true;
        byte[] result = null;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();

        if (!socket.isConnected()) {
            System.out.println("Aplikacja nie połączyła się z serwerem");
        }
        while (flag) {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                int len = in.readInt();
                result = new byte[len];
                if (len > 0) {
                    in.readFully(result);
                    //System.out.println("Aplikacja odebrała wynik logowania");
                    flag = false;
                }

            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        flag = true;
        this.result = new String(result);
    }
}
