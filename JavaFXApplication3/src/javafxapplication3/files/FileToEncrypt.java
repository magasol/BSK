/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3.files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Magdalena
 */
public class FileToEncrypt {

    protected String fullFileName;
    protected SecretKey keySecret;
    protected Cipher cipher;

    public FileToEncrypt(String fullFileName) {
        try {
            this.fullFileName = fullFileName;
            KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
            keyGenerator.init(128);
            keySecret = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileToEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String readFile() throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader(this.fullFileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String text, tmp;

        try {
            text = bufferedReader.readLine();
            do {
                tmp = bufferedReader.readLine();
                if (tmp != null) {
                    text = text + "\n" + tmp;
                }
            } while (tmp != null);
        } finally {
            bufferedReader.close();
        }
        System.out.println("\n\nTresc wcztanego pliku:\n" + text);
        return text;
    }

    public void writeFile(String path, String text) throws FileNotFoundException {
        PrintWriter file = new PrintWriter(path);
        file.println(text);
        file.close();
    }

    public void encryptFile() throws IOException {
        System.out.print("szyfruj plik");

        String key = "Key";
        String fileText = this.readFile();
        SecretKey keySecret = new SecretKeySpec(key.getBytes(), "Blowfish");
        Cipher cipher;

        try {

            cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, keySecret);
            byte[] cipherText = cipher.doFinal(fileText.getBytes());
            String encryptedText = Base64.getEncoder().encodeToString(cipherText);

            System.out.print("Plik zaszyfrowany metodą Blowfish");

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileToEncryptCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(FileToEncryptCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(FileToEncryptCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(FileToEncryptCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(FileToEncryptCBC.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String encrypted(String fileText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String key = "Key";
        SecretKey keySecret = new SecretKeySpec(key.getBytes(), "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, keySecret);

        byte[] cipherText = cipher.doFinal(fileText.getBytes());
        String encryptedText = Base64.getEncoder().encodeToString(cipherText);

        return new String(encryptedText);
    }
}
