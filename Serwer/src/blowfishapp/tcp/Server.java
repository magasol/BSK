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
import blowfishapp.file.DiskElement;
import blowfishapp.file.MyComparator;
import blowfishapp.keys.KeysGenerator;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
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
    private KeysGenerator keysGenerator;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    String path = "C:\\Users\\Aleksandra\\Desktop";
   // String path = "E:\\semestr 6\\bsk";
    

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
    
    public void listen(Socket connection, int port) {
        try {
            this.out = new ObjectOutputStream(connection.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(connection.getInputStream());
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

            byte[] keySecret = new byte[len];
            if (len > 0) {
                in.readFully(keySecret);
                generateKeys(keySecret);
            }

            //System.out.println("Serwer odebrał: " + new String(encryptedText));
            System.out.println("Serwer odebrał: prosbe o plik");
            
            send(port);
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void listenFiles(Socket connection, int port) {
        try {
            this.out = new ObjectOutputStream(connection.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(connection.getInputStream());
            int len = in.readInt();
            byte[] request = new byte[len];
            if (len > 0) {
                in.readFully(request);
            }
            System.out.println("Serwer odebrał prosbe o wyswietlenie listy plikow");
            
            filesList();
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void send(int port) {
        try {
            encrypt(new String(this.type), new File(new String(this.filePath)));
            byte[] ivBytes = encryption.getIvBytes();
            if(ivBytes==null)
                ivBytes="null".getBytes();
            this.out.writeInt(ivBytes.length);
            this.out.write(ivBytes, 0, ivBytes.length);
            this.out.flush();
            System.out.println("serwer wysłał wektor");

            this.out.writeInt(encryption.encryptedText.length);
            this.out.write(encryption.encryptedText, 0, encryption.encryptedText.length);
            this.out.flush();
            //System.out.println("serwer wysłał " + new String(encryptedText));
            System.out.println("serwer wysłał plik");
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void filesList() throws IOException
    {
        String files = chooseFile(path);
        
        byte[] bList = files.getBytes();
        this.out.writeInt(bList.length);
        this.out.write(bList, 0, bList.length);
        this.out.flush();
        System.out.println("serwer wysłał liste plikow");
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

    private void generateKeys(byte[] keySecret) {
        this.keysGenerator = new KeysGenerator(keySecret);
    }
}
