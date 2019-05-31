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
        byte[] bPassword = password.getBytes();
        byte[] bLogin = login.getBytes();
        Socket socket = new Socket(this.serverAddress, this.PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();

        try {
            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }

            this.out.writeInt(text.length);
            this.out.write(text, 0, text.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała prosbe o sprawdzenie logowania");
            
            this.out.writeInt(bLogin.length);
            this.out.write(bLogin, 0, bLogin.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała login");
            
            this.out.writeInt(bPassword.length);
            this.out.write(bPassword, 0, bPassword.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała hasło");

            receiveResult(socket);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.result;
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
                    System.out.println("Aplikacja odebrała wynik logowania");
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
