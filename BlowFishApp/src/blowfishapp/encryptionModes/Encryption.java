/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.encryptionModes;

import blowfishapp.keys.KeysGenerator;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author Magdalena
 */
public class Encryption {

    protected String fullFileName;
    protected String outputFileName;
    protected SecretKey keySecret;
    protected Cipher cipher;
    protected String pswd;
    public byte[] encryptedText;

    public Encryption(String fullFileName, String outputFileName, KeysGenerator keysGenerator) {
        try {
            cipher = Cipher.getInstance("Blowfish");
            this.fullFileName = fullFileName;
            this.outputFileName = outputFileName;
            keySecret = keysGenerator.getKeySecret();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] readFile() throws FileNotFoundException, IOException {
        Path path = Paths.get(this.fullFileName);
        return Files.readAllBytes(path);
    }

    public void writeFile(String path, byte[] text) throws FileNotFoundException {
        try {
            FileOutputStream outputStream
                    = new FileOutputStream(path + "\\" + outputFileName);
            outputStream.write(text);
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void encryptFile() throws IOException {
        System.out.println("szyfruj plik " + this.fullFileName);
        byte[] fileText = this.readFile();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySecret);
            byte[] cipherText = cipher.doFinal(fileText);
            this.encryptedText = cipherText;
            this.writeFile("E:\\semestr 6\\bsk\\encrypted", cipherText);

            byte[] decryptedText = this.decryptText(cipherText);

            //System.out.println("\n\nZASZYFROWANY TEKST:\n" + new String(cipherText, "UTF8"));
            this.writeFile("E:\\semestr 6\\bsk\\decrypted", decryptedText);
            System.out.println("KONIEC");

        } catch (InvalidKeyException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EncryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] decryptText(byte[] encryptedText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, keySecret);
        byte[] decryptedText = cipher.doFinal(encryptedText);
        return decryptedText;

    }
}
