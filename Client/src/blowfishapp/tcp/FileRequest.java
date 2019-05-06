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
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksandra
 */
public class FileRequest implements Callable {

    public String files;
    private ObjectOutputStream out;
    private int PORT;
    private InetAddress serverAddress;

    public FileRequest(InetAddress serverAddress, int serverPort) {
        this.PORT = serverPort;
        this.serverAddress = serverAddress;
    }

    @Override
    public String call() throws Exception {

        byte[] text = "list".getBytes();

        Socket socket = new Socket(serverAddress, PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();

        try {
            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }

            this.out.writeInt(text.length);
            this.out.write(text, 0, text.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała prosbe o liste plikow");

            receiveFiles(socket);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.files;
    }

    public void receiveFiles(Socket socket) throws IOException {
        boolean flag = true;
        byte[] files = null;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();

        if (!socket.isConnected()) {
            System.out.println("Aplikacja nie połączyła się z serwerem");
        }
        while (flag) {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                int len = in.readInt();
                files = new byte[len];
                if (len > 0) {
                    in.readFully(files);
                    System.out.println("Aplikacja odebrała liste plikow");
                    flag = false;
                }

            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        flag = true;
        this.files = new String(files);
    }
}
