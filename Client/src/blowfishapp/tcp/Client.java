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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Aleksandra
 */
public class Client extends Task<Void> {

    //final private String outputPathEncrypted = "D:\\STUDIA\\VI semestr\\BSK";
    //final private String outputPathDecrypted = "D:\\STUDIA\\VI semestr\\BSK";
    final private String outputPathDecrypted = "..\\..\\decrypted";
    final private String outputPathEncrypted = "..\\..\\encrypted";
    final private int PORT;
    private InetAddress serverAddress;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean flag = true;
    private Decryption decryption;
    private KeysGenerator keysGenerator;
    private String pswd;
    private byte[] mode;
    private byte[] fileName;
    private String outputFileName;
    private Socket socket;
    private ProgressBar progressBar;

    public Client(InetAddress serverAddress, int serverPort,
            byte[] mode, byte[] inputFileName, String outputFileName, String pswd, KeysGenerator keysGenerator,
            ProgressBar progressBar) {
        this.PORT = serverPort;
        this.serverAddress = serverAddress;
        this.pswd = pswd;
        this.mode = mode;
        this.fileName = inputFileName;
        this.outputFileName = outputFileName;
        this.keysGenerator = keysGenerator;
        this.progressBar = progressBar;
    }

    @Override
    protected Void call() throws Exception {
        socket = new Socket(serverAddress, PORT);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();

        try {
            if (!socket.isConnected()) {
                System.out.println("Aplikacja nie połączyła się z serwerem");
            }
            byte[] request = "file".getBytes();
            this.out.writeInt(request.length);
            this.out.write(request, 0, request.length);
            this.out.flush();

            progressBar.setProgress(0.1);

            this.out.writeInt(fileName.length);
            this.out.write(fileName, 0, fileName.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała ścieżke: " + new String(fileName));

            progressBar.setProgress(0.2);

            this.out.writeInt(mode.length);
            this.out.write(mode, 0, mode.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała tryb kodowania: " + new String(mode));

            progressBar.setProgress(0.3);

            byte[] keySecretBytes = this.keysGenerator.readPublicKey();
            this.out.writeInt(keySecretBytes.length);
            this.out.write(keySecretBytes, 0, keySecretBytes.length);
            this.out.flush();
            System.out.println("Aplikacja wysłała klucz publiczny");

            progressBar.setProgress(0.4);

            receive(socket);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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

                progressBar.setProgress(0.5);

                len = in.readInt();
                byte[] encryptedSessionKeyBytes = new byte[len];
                if (len > 0) {
                    in.readFully(encryptedSessionKeyBytes);
                    //System.out.println("Aplikacja odebrała: " + new String(encryptedSessionKeyBytes));
                    System.out.println("Aplikacja odebrała: zaszyfrowany klucz sesyjny od serwera");
                    flag = false;
                }

                progressBar.setProgress(0.6);

                len = in.readInt();
                byte[] encryptedText = new byte[len];
                if (len > 0) {
                    in.readFully(encryptedText);
                    //System.out.println("Aplikacja odebrała: " + new String(encryptedText));
                    System.out.println("Aplikacja odebrała: zaszyfrowany plik od serwera");
                    flag = false;
                }

                progressBar.setProgress(0.7);

                this.keysGenerator.encryptedSessionKeyBytes = encryptedSessionKeyBytes;

                progressBar.setProgress(0.8);

                prepareForDecryption(encryptedText, iv);

                progressBar.setProgress(1);

                System.out.println("KONIEC");
                stop();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        flag = true;
    }

    private void prepareForDecryption(byte[] encryptedText, byte[] ivBytes) throws IOException {
        String value = new String(this.mode);
        if (encryptedText != null) {
            try {
                switch (value) {
                    case "CBC":
                        System.out.println("tryb szyfrowania cbc");
                        this.decryption = new DecryptionCBC(encryptedText, this.keysGenerator);
                        break;
                    case "CFB":
                        this.decryption = new DecryptionCFB(encryptedText, this.keysGenerator);
                        System.out.println("tryb szyfrowania cfb");
                        break;
                    case "ECB":
                        this.decryption = new DecryptionECB(encryptedText, this.keysGenerator);
                        System.out.println("tryb szyfrowania ecb");
                        break;
                    case "OFB":
                        this.decryption = new DecryptionOFB(encryptedText, this.keysGenerator);
                        System.out.println("tryb szyfrowania ofb");
                        break;
                    default:
                        this.decryption = new Decryption(encryptedText, this.keysGenerator);
                        System.out.println("brak trybu szyfrowania");
                }
                if (!"ecb".equals(new String(this.mode))) {
                    this.decryption.setIvParameterSpec(ivBytes);
                }
                this.decryption.writeFile(this.outputPathEncrypted, new String(this.fileName), encryptedText);
                this.decryption.encryptedText = encryptedText;

                this.decryption.keysGenerator.decryptSecretKey(this.pswd);
                byte[] decryptedText = this.decryption.decryptText();
                String outputFileNameMatched = matchOutputFileNameExtension();
                this.decryption.writeFile(this.outputPathDecrypted, outputFileNameMatched, decryptedText);
                System.out.println("Plik odszyfrowany");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchPaddingException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                genarateAndSaveFakeFile(encryptedText.length);
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String matchOutputFileNameExtension() {
        String inputFileNameString = (new String(this.fileName));
        String[] inputFileNameElements = inputFileNameString.split("\\.", 2);
        String inputFileExtension = inputFileNameElements[1];
        if (this.outputFileName.contains(".")) {
            String outputFileNameString = new String(this.outputFileName);
            String[] outputFileNameElements = outputFileNameString.split("\\.", 2);
            outputFileNameElements[1] = inputFileExtension;
            return outputFileNameElements[0] + "." + outputFileNameElements[1];
        } else {
            return (this.outputFileName + "." + inputFileExtension);
        }
    }

    private void genarateAndSaveFakeFile(int length) {
        try {
            byte[] decryptedText = generateText(length);
            String outputFileNameMatched = matchOutputFileNameExtension();
            this.decryption.writeFile(this.outputPathDecrypted, outputFileNameMatched, decryptedText);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private byte[] generateText(int length) {
        List<Byte> textBytesList = new ArrayList<Byte>();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            textBytesList.add((byte) random.nextInt(255));
        }
        Byte[] textBigBytes = textBytesList.toArray(new Byte[textBytesList.size()]);
        byte[] textBytes = new byte[textBigBytes.length];
        for (int i = 0; i < textBigBytes.length; i++) {
            textBytes[i] = textBigBytes[i];
        }
        return textBytes;
    }

    private void stop() throws IOException {
        this.socket.close();
        this.in.close();
        this.out.close();
        System.out.println("Klient został zamknięty.");
    }
}
