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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 *
 * @author Magdalena
 */
public class KeyBinding extends Task<ObservableList<String>> {

    private ObjectOutputStream out;
    private int PORT;
    private InetAddress serverAddress;
    byte[] login;
    byte[] publicKey;

    public KeyBinding(InetAddress serverAddress, int serverPort, byte[] login, byte[] publicKey) {
        this.PORT = serverPort;
        this.serverAddress = serverAddress;
        this.login = login;
        this.publicKey = publicKey;
    }

    @Override
    protected ObservableList<String> call() throws Exception {
        Socket socket = new Socket(this.serverAddress, this.PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();

        if (!socket.isConnected()) {
            System.out.println("Aplikacja nie połączyła się z serwerem");
        }

        byte[] text = "key binding".getBytes();
        this.out.writeInt(text.length);
        this.out.write(text, 0, text.length);
        this.out.flush();
        //System.out.println("Aplikacja wysłała prosbe o sprawdzenie logowania");

        this.out.writeInt(this.login.length);
        this.out.write(this.login, 0, this.login.length);
        this.out.flush();
        //System.out.println("Aplikacja wysłała prosbe o sprawdzenie logowania");

        this.out.writeInt(this.publicKey.length);
        this.out.write(this.publicKey, 0, this.publicKey.length);
        this.out.flush();
        //System.out.println("Aplikacja wysłała prosbe o sprawdzenie logowania");

        return receiveListOfReceivers(socket);
    }

    private ObservableList<String> receiveListOfReceivers(Socket socket) {
        try {
            boolean flag = true;
            byte[] result = " ".getBytes();
            ObservableList<String> listOfReceivers = FXCollections.observableArrayList();
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }
            while (flag) {
                try {
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    while (!"stop".equals(new String(result))) {
                        int len = in.readInt();
                        result = new byte[len];
                        if (len > 0) {
                            in.readFully(result);
                            listOfReceivers.add(new String(result));
                            flag = false;
                        }
                    }
                    listOfReceivers.remove(listOfReceivers.size()-1);
                    //System.out.println("Aplikacja odebrała listę odbiorców");
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            flag = true;
            return listOfReceivers;
        } catch (IOException ex) {
            Logger.getLogger(KeyBinding.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
