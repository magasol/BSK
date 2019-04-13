/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.tcp;

import blowfishapp.decryptionModes.Decryption;
import blowfishapp.decryptionModes.DecryptionCBC;
import blowfishapp.decryptionModes.DecryptionCFB;
import blowfishapp.decryptionModes.DecryptionECB;
import blowfishapp.decryptionModes.DecryptionOFB;
import blowfishapp.keys.KeysGenerator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksandra
 */
public class Client {

    Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    Decryption decryption;

    public Client(InetAddress serverAddress, int serverPort) throws Exception {
        this.connection = new Socket(serverAddress, serverPort);
        this.out = new ObjectOutputStream(connection.getOutputStream());
        this.out.flush();
        
        System.out.println("Klient został stworzony.");
    }

    /*public void send(byte[] cipherText) {
        try {
            if (!connection.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }
            this.out.writeInt(cipherText.length);
            this.out.write(cipherText,0,cipherText.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała " + new String(cipherText));
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    public byte[] receive() {
        try {
            this.in = new ObjectInputStream(connection.getInputStream());
            int len = in.readInt();
            byte[] encryptedText = new byte[len];
            if (len > 0) {
                in.readFully(encryptedText);
            }
            System.out.println("Aplikacja odebrała: " + new String(encryptedText));
            return encryptedText;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void decrypt(String value, byte[] encryptedText, String outputFileName, KeysGenerator keysGenerator) throws IOException {
        if (encryptedText != null) {
            switch (value) {
                case "CBC":
                    System.out.println("tryb szyfrowania cbc");
                    decryption = new DecryptionCBC(encryptedText, outputFileName, keysGenerator);
                    break;
                case "CFB":
                    decryption = new DecryptionCFB(encryptedText, outputFileName, keysGenerator);
                    System.out.println("tryb szyfrowania cfb");
                    break;
                case "ECB":
                    decryption = new DecryptionECB(encryptedText, outputFileName, keysGenerator);
                    System.out.println("tryb szyfrowania ecb");
                    break;
                case "OFB":
                    decryption = new DecryptionOFB(encryptedText, outputFileName, keysGenerator);
                    System.out.println("tryb szyfrowania ofb");
                    break;
                default:
                    decryption = new Decryption(encryptedText, outputFileName, keysGenerator);
                    System.out.println("brak trybu szyfrowania");
            }
           // decryption.writeFile("E:\\semestr 6\\bsk\\encrypted", encryptedText);
            decryption.writeFile("D:\\STUDIA\\VI semestr\\BSK", encryptedText);
            //byte[] decryptedText = encryption.decryptText(encryptedText);
            //decryption.writeFile("E:\\semestr 6\\bsk\\decrypted", encryptedText);
            decryption.writeFile("D:\\STUDIA\\VI semestr\\BSK", encryptedText);
        }
    }

    public void stop() throws IOException {
        connection.close();
        in.close();
        //out.close();
        System.out.println("Klient został zamknięty.");
    }

}
