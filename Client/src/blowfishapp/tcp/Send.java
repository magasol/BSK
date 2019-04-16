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
import javafx.concurrent.Task;

/**
 *
 * @author Aleksandra
 */
public class Send extends Task<Void> {

    private int PORT;
    byte[] cipherText;
    InetAddress serverAddress;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    boolean flag = true;
    private Decryption decryption;
    private String outputFileName;
    private KeysGenerator keysGenerator;
    String pswd;
    final private String outputPathEncrypted = "D:\\STUDIA\\VI semestr\\BSK";
    final private String outputPathDecrypted = "D:\\STUDIA\\VI semestr\\BSK";
    //final private String outputPathEncrypted = "E:\\semestr 6\\bsk\\encrypted";
    //final private String outputPathDecrypted = "E:\\semestr 6\\bsk\\decrypted";
    
    public Send(InetAddress serverAddress, int serverPort, byte[] cipherText, String outputFile, String pswd) {
        this.PORT = serverPort;
        this.serverAddress = serverAddress;
        this.cipherText = cipherText;
        this.outputFileName = outputFile;
        this.pswd = pswd;
    }

    @Override
    protected Void call() throws Exception {

        Socket socket = new Socket(serverAddress, PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();

        try {
            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }
            generateKeys(pswd);
            this.out.writeInt(cipherText.length);
            this.out.write(cipherText, 0, cipherText.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała " + new String(cipherText));
            byte[] encryptedText = receive(socket);
            decrypt("ECB",encryptedText);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        //out.close();
        return null;
    }

    private byte[] receive(Socket socket) {
        while (flag) {
            try {
                this.in = new ObjectInputStream(socket.getInputStream());
                int len = in.readInt();
                byte[] encryptedText = new byte[len];
                if (len > 0) {
                    in.readFully(encryptedText);
                    System.out.println("Aplikacja odebrała: " + new String(encryptedText));
                    flag = false;
                }

                return encryptedText;
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        flag = true;
        return null;
    }

    private void decrypt(String value, byte[] encryptedText) throws IOException {
        if (encryptedText != null) {
            switch (value) {
                case "CBC":
                    System.out.println("tryb szyfrowania cbc");
                    decryption = new DecryptionCBC(encryptedText, this.outputFileName, this.keysGenerator);
                    break;
                case "CFB":
                    decryption = new DecryptionCFB(encryptedText, this.outputFileName, this.keysGenerator);
                    System.out.println("tryb szyfrowania cfb");
                    break;
                case "ECB":
                    decryption = new DecryptionECB(encryptedText, this.outputFileName, this.keysGenerator);
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
            decryption.writeFile(outputPathEncrypted, encryptedText);
            //byte[] decryptedText = encryption.decryptText(encryptedText);
            decryption.writeFile(outputPathDecrypted, encryptedText);
        }
    }
    
    private void generateKeys(String pswd) {
        this.keysGenerator = new KeysGenerator(pswd);
    }
}
