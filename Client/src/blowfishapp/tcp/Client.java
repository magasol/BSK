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
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksandra
 */
public class Client implements Callable {

    //final private String outputPathEncrypted = "D:\\STUDIA\\VI semestr\\BSK";
    //final private String outputPathDecrypted = "D:\\STUDIA\\VI semestr\\BSK";
    final private String outputPathEncrypted = "E:\\semestr 6\\bsk\\encrypted";
    final private int PORT;
    private InetAddress serverAddress;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean flag = true;
    private Decryption decryption;
    private KeysGenerator keysGenerator;
    private String pswd;
    private byte[] type;
    private byte[] fileName;
    private Socket socket;

    public Client(InetAddress serverAddress, int serverPort,
            byte[] mode, byte[] inputFileName, String pswd) {
        this.PORT = serverPort;
        this.serverAddress = serverAddress;
        this.pswd = pswd;
        this.type = mode;
        this.fileName = inputFileName;
    }

    @Override
    public Decryption call() throws Exception {

        socket = new Socket(serverAddress, PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
           
        try {
            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }
            generateKeys(pswd);
            byte[] request = "file".getBytes();
            this.out.writeInt(request.length);
            this.out.write(request, 0, request.length);
            this.out.flush();

            this.out.writeInt(fileName.length);
            this.out.write(fileName, 0, fileName.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała ścieżke: " + new String(fileName));

            this.out.writeInt(type.length);
            this.out.write(type, 0, type.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała tryb kodowania: " + new String(type));

            byte[] keySecretBytes = this.keysGenerator.readPublicKey();
            this.out.writeInt(keySecretBytes.length);
            this.out.write(keySecretBytes, 0, keySecretBytes.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała klucz publiczny");

            receive(socket);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.decryption;
    }

    private void receive(Socket socket) {
        while (flag) {
            try {
                this.in = new ObjectInputStream(socket.getInputStream());

                int len = in.readInt();
                byte[] iv = new byte[len];
                if (len > 0) {
                    in.readFully(iv);
                    System.out.println("Aplikacja odebrała: iv od serwera");
                    flag = false;
                }

                len = in.readInt();
                byte[] encryptedSessionKeyBytes = new byte[len];
                if (len > 0) {
                    in.readFully(encryptedSessionKeyBytes);
                    //System.out.println("Aplikacja odebrała: " + new String(encryptedSessionKeyBytes));
                    System.out.println("Aplikacja odebrała: zaszyfrowany klucz sesyjny od serwera");
                    flag = false;
                }

                len = in.readInt();
                byte[] encryptedText = new byte[len];
                if (len > 0) {
                    in.readFully(encryptedText);
                    //System.out.println("Aplikacja odebrała: " + new String(encryptedText));
                    System.out.println("Aplikacja odebrała: plik od serwera");
                    flag = false;
                }
                this.keysGenerator.encryptedSessionKeyBytes = encryptedSessionKeyBytes;
                prepareForDecryption(encryptedText, iv);              
                System.out.println("KONIEC");
                stop();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        flag = true;
    }

    private void prepareForDecryption(byte[] encryptedText, byte[] ivBytes) throws IOException {
        String value = new String(this.type);
        if (encryptedText != null) {
                switch (value) {
                    case "CBC":
                        System.out.println("tryb szyfrowania cbc");
                        decryption = new DecryptionCBC(encryptedText, this.keysGenerator);
                        break;
                    case "CFB":
                        decryption = new DecryptionCFB(encryptedText, this.keysGenerator);
                        System.out.println("tryb szyfrowania cfb");
                        break;
                    case "ECB":
                        decryption = new DecryptionECB(encryptedText, this.keysGenerator);
                        System.out.println("tryb szyfrowania ecb");
                        break;
                    case "OFB":
                        decryption = new DecryptionOFB(encryptedText, this.keysGenerator);
                        System.out.println("tryb szyfrowania ofb");
                        break;
                    default:
                        decryption = new Decryption(encryptedText, this.keysGenerator);
                        System.out.println("brak trybu szyfrowania");
                }
                if (!"ecb".equals(new String(type))) {
                    decryption.setIvParameterSpec(ivBytes);
                }           
                decryption.writeFile(this.outputPathEncrypted,new String(this.fileName), encryptedText);
                this.decryption.encryptedText = encryptedText;
        }
    }

    private void generateKeys(String pswd) {
        this.keysGenerator = new KeysGenerator(pswd);
    }

    private void stop() throws IOException {
        socket.close();
        in.close();
        out.close();
        System.out.println("Klient został zamknięty.");
    }
}
