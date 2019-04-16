/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.tcp;

import blowfishapp.encryptionModes.Encryption;
import blowfishapp.encryptionModes.EncryptionCBC;
import blowfishapp.encryptionModes.EncryptionCFB;
import blowfishapp.encryptionModes.EncryptionECB;
import blowfishapp.encryptionModes.EncryptionOFB;
import blowfishapp.keys.KeysGenerator;
import java.io.File;
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
    public ServerSocket serverSocket;
    private KeysGenerator keysGenerator;
    Encryption encryption = null;

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
            this.out = new ObjectOutputStream(connection.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(connection.getInputStream());
            int len = in.readInt();
            byte[] encryptedText = new byte[len];
            if (len > 0) {
                in.readFully(encryptedText);
            }
            //System.out.println("Serwer odebrał: " + new String(encryptedText));
            System.out.println("Serwer odebrał: prosbe o plik");
            return encryptedText;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void send(int port, String mode, File file) {
        try {
            encrypt(mode, file);
            this.out.writeInt(encryption.encryptedText.length);
            this.out.write(encryption.encryptedText, 0, encryption.encryptedText.length);
            this.out.flush();
            //System.out.println("serwer wysłał " + new String(encryptedText));
            System.out.println("serwer wysłał plik");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void encrypt(String value, File file) throws IOException {
        if (file != null) {
            String pswd = "key";
            generateKeys(pswd);
            switch (value) {
                case "CBC":
                    System.out.println("tryb szyfrowania cbc");
                    encryption = new EncryptionCBC(file.getPath(), this.keysGenerator);
                    break;
                case "CFB":
                    encryption = new EncryptionCFB(file.getPath(), this.keysGenerator);
                    System.out.println("tryb szyfrowania cfb");
                    break;
                case "ECB":
                    encryption = new EncryptionECB(file.getPath(), this.keysGenerator);
                    System.out.println("tryb szyfrowania ecb");
                    break;
                case "OFB":
                    encryption = new EncryptionOFB(file.getPath(), this.keysGenerator);
                    System.out.println("tryb szyfrowania ofb");
                    break;
                default:
                    encryption = new Encryption(file.getPath(), this.keysGenerator);
                    System.out.println("brak trybu szyfrowania");
            }

            if (encryption != null) {
                encryption.encryptFile();
            }
        }
    }

    private void generateKeys(String pswd) {
        this.keysGenerator = new KeysGenerator(pswd);
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
