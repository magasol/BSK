/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.decryptionModes;

import blowfishapp.keys.KeysGenerator;
import java.io.File;
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
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author Magdalena
 */
public class Decryption {

    public byte[] encryptedText;
    public KeysGenerator keysGenerator;
    protected Cipher cipher;
    protected IvParameterSpec iv;

    public Decryption(byte[] encryptedText, KeysGenerator keysGenerator) {
        try {
            this.encryptedText = encryptedText;
            this.cipher = Cipher.getInstance("Blowfish");
            this.keysGenerator = keysGenerator;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeFile(String path,String outputFileName, byte[] text) throws FileNotFoundException {
        try {
            if (!new File(path).exists()) {
                new File(path).mkdir();
            }
            FileOutputStream outputStream
                    = new FileOutputStream(path + "\\" + outputFileName);
            outputStream.write(text);
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] decryptText() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        long start = System.nanoTime(); 
        this.cipher.init(Cipher.DECRYPT_MODE, this.keysGenerator.getKeySecret());
        byte[] decryptedText = this.cipher.doFinal(encryptedText);
        System.out.println("czas deszyfrowania"+(System.nanoTime() - start));
        return decryptedText;
    }

    public void setIvParameterSpec(byte[] ivBytes) {
        iv = new IvParameterSpec(ivBytes);
    }
}
