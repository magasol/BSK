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
import blowfishapp.file.DiskDirectory;
import blowfishapp.file.MyComparator;
import blowfishapp.keys.KeysGenerator;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aleksandra
 */
public class Server {

    public ServerSocket serverSocket;
    public byte[] type;
    public byte[] filePath;
    public Encryption encryption = null;
    private byte[] publicKey;
    private KeysGenerator keysGenerator;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    //String path = "C:\\Users\\Aleksandra\\Desktop";
    String path = "E:\\semestr 6\\bsk\\test";
    List<String> users = new ArrayList<String>();
    List<String> passwords = new ArrayList<String>();

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

    public void listen(int port) {

        try {
            while (!serverSocket.isClosed()) {
                final Socket connection = this.serverSocket.accept();
                this.out = new ObjectOutputStream(connection.getOutputStream());
                this.out.flush();
                this.in = new ObjectInputStream(connection.getInputStream());

                int len = in.readInt();
                byte[] requestFor = new byte[len];
                if (len > 0) {
                    in.readFully(requestFor);
                }

                if ("file".equals(new String(requestFor))) {
                    manageSendingEncryptedFile(port);
                } else if ("list".equals(new String(requestFor))) {
                    manageFilesListRequest(port);
                } else if ("login".equals(new String(requestFor))) {
                    manageLoginRequest();
                }
            }
        } catch (IOException ex) {
            System.out.println("Server socket jest zamknięty.");
        }

    }

    private void manageSendingEncryptedFile(int port) {
        try {
            int len = in.readInt();
            byte[] pathF = new byte[len];
            if (len > 0) {
                in.readFully(pathF);
            }
            System.out.println("Serwer odebrał ścieżke do pliku: " + new String(pathF));
            String tmp = path + "\\" + new String(pathF);
            this.filePath = tmp.getBytes();

            len = in.readInt();
            byte[] type = new byte[len];
            if (len > 0) {
                in.readFully(type);
            }
            System.out.println("Serwer odebrał tryb kodowania: " + new String(type));
            this.type = type;

            len = in.readInt();

            byte[] publicKey = new byte[len];
            if (len > 0) {
                in.readFully(publicKey);
                this.publicKey = publicKey;
            }
            //System.out.println("Serwer odebrał: " + new String(publicKey));
            System.out.println("Serwer odebrał: klucz publiczny");

            send(port);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void send(int port) {
        try {
            this.keysGenerator = new KeysGenerator();

            File file = new File(new String(this.filePath));
            encrypt(new String(this.type), file);
            this.keysGenerator.encryptSessionKey(this.publicKey);

            byte[] ivBytes = encryption.getIvBytes();
            if (ivBytes == null) {
                ivBytes = "null".getBytes();
            }
            this.out.writeInt(ivBytes.length);
            this.out.write(ivBytes, 0, ivBytes.length);
            this.out.flush();
            System.out.println("serwer wysłał wektor");

            byte[] keySecretBytes = this.keysGenerator.getKeySecret().getEncoded();
            this.out.writeInt(keySecretBytes.length);
            this.out.write(keySecretBytes, 0, keySecretBytes.length);
            this.out.flush();
            System.out.println("serwer wysłał zaszyfrowany klucz sesyjny");

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

    private void manageFilesListRequest(int port) {
        try {
            System.out.println("Serwer odebrał prosbe o wyswietlenie listy plikow");
            sendFilesList();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendFilesList() throws IOException {
        String files = chooseFile(path);

        byte[] bList = files.getBytes();
        this.out.writeInt(bList.length);
        this.out.write(bList, 0, bList.length);
        this.out.flush();

        System.out.println("serwer wysłał liste plikow");
    }

    private void manageLoginRequest() {
        try {
            System.out.println("Serwer odebrał prosbe o sprawdzenie wyniku logowania");

            int len = in.readInt();
            byte[] login = new byte[len];
            if (len > 0) {
                in.readFully(login);
            }

            len = in.readInt();
            byte[] password = new byte[len];
            if (len > 0) {
                in.readFully(password);
            }
            if (userExists(new String(login))) {
                checkPasswordCorrection(new String(login), new String(password));
            } else {
                addNewUser(new String(login), new String(password));
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean userExists(String login) {
        for (String user : users) {
            if (user.equals(login)) {
                return true;
            }
        }
        return false;
    }

    public void checkPasswordCorrection(String login, String password) throws IOException {
        String result = "failed";

        for (int i = 0; i < users.size(); i++) {
            if (login.contentEquals(users.get(i)) && password.contentEquals(passwords.get(i))) {
                result = "success";
            }
        }
        sendLoginResult(result);
    }

    private void addNewUser(String login, String password) {
        this.users.add(login);
        this.passwords.add(password);
        sendLoginResult("success");
    }
    
    private void sendLoginResult(String result)
    {
        try {
            byte[] resultBytes = result.getBytes();
            this.out.writeInt(resultBytes.length);
            this.out.write(resultBytes, 0, resultBytes.length);
            this.out.flush();
            
            System.out.println("serwer wysłał rezultat logowania");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String chooseFile(String path) {
        String list = "";
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new Exception("The file does not exist or the path is incorrect.");
            }

            MyComparator comparator = new MyComparator();
            DiskDirectory myDiskDirectory = new DiskDirectory(file, 1, comparator);
            list = myDiskDirectory.print(100);

            return list;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void stop() {
        try {
            if (!serverSocket.isClosed()) {
                if (this.in != null) {
                    this.in.close();
                }
                if (this.out != null) {
                    this.out.close();
                }

                serverSocket.close();
                System.out.println("Serwer został zamknięty.");
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
