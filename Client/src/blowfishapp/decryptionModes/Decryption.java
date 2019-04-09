/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.decryptionModes;

import blowfishapp.keys.KeysGenerator;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class Decryption {

    protected String outputFileName;
    //final protected String outputPathEncrypted = "D:\\STUDIA\\VI semestr\\BSK";
    //final protected String outputPathDecrypted = "D:\\STUDIA\\VI semestr\\BSK";
    final protected String outputPathEncrypted = "E:\\semestr 6\\bsk\\encrypted";
    final protected String outputPathDecrypted = "E:\\semestr 6\\bsk\\decrypted";
    protected SecretKey keySecret;
    protected Cipher cipher;
    protected String pswd;
    public byte[] encryptedText;

    public Decryption(byte[] encryptedText, String outputFileName, KeysGenerator keysGenerator) {
        try {
            this.encryptedText = encryptedText;
            cipher = Cipher.getInstance("Blowfish");
            this.outputFileName = outputFileName;
            keySecret = keysGenerator.getKeySecret();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeFile(String path, byte[] text) throws FileNotFoundException {
        try {
            FileOutputStream outputStream
                    = new FileOutputStream(path + "\\" + outputFileName);
            outputStream.write(text);
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] decryptText(byte[] encryptedText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, keySecret);
        byte[] decryptedText = cipher.doFinal(encryptedText);
        return decryptedText;

    }
}
