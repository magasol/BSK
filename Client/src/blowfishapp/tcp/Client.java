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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Aleksandra
 */
public class Client {

    Socket socket;
    
    private DataOutputStream out;
    private BufferedReader in;
    Encryption encryption;

    public Client(InetAddress serverAddress, int serverPort) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Klient został stworzony.");
    }

    public void send(byte[] cipherText) {
        try {
            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }
            this.out.write(cipherText);
            System.out.println("Aplikacja wysłała " + new String(cipherText));
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public byte[] receive() {
        try {
            //byte[] encryptedText = null;
            String text = "";
            String input = null;
            while ((input = in.readLine())!= null){
                text = text + "\n" + input;
            }
            return text.getBytes();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public void decrypt(String value, byte[] encryptedText,String outputFileName, KeysGenerator keysGenerator) throws IOException {
        if (encryptedText != null) {
            try {
                switch (value) {
                    case "CBC":
                        System.out.println("tryb szyfrowania cbc");
                        encryption = new EncryptionCBC(encryptedText, outputFileName, keysGenerator);
                        break;
                    case "CFB":
                        encryption = new EncryptionCFB(encryptedText, outputFileName, keysGenerator);
                        System.out.println("tryb szyfrowania cfb");
                        break;
                    case "ECB":
                        encryption = new EncryptionECB(encryptedText, outputFileName, keysGenerator);
                        System.out.println("tryb szyfrowania ecb");
                        break;
                    case "OFB":
                        encryption = new EncryptionOFB(encryptedText, outputFileName, keysGenerator);
                        System.out.println("tryb szyfrowania ofb");
                        break;
                    default:
                        encryption = new Encryption(encryptedText, outputFileName, keysGenerator);
                        System.out.println("brak trybu szyfrowania");
                }
                byte[] decryptedText = encryption.decryptText(encryptedText);
                encryption.writeFile("E:\\semestr 6\\bsk\\decrypted", decryptedText);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchPaddingException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stop() throws IOException {
        socket.close();
        in.close();
        out.close();
        System.out.println("Klient został zamknięty.");
    }



}
