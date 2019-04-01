/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.encryptionModes;

import blowfishapp.keys.KeysGenerator;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Magdalena
 */
public class EncryptionCBC extends Encryption {

    private IvParameterSpec iv;
    private SecretKeySpec secretKeySpec;

    public EncryptionCBC(String fullFileName, KeysGenerator keysGenerator) {
        super(fullFileName, keysGenerator);
        try {
            cipher = Cipher.getInstance("Blowfish/CBC/ISO10126Padding");

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EncryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void encryptFile() throws IOException {

        System.out.println("szyfruj plik " + this.fullFileName + " w trybie CBC");
        byte[] fileText = this.readFile();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySecret);
            byte[] cipherText = cipher.doFinal(fileText);
            this.encryptedText = cipherText;
            
            byte[] ivBytes = cipher.getIV();
            if (ivBytes != null) {
                iv = new IvParameterSpec(ivBytes);
            }

            byte[] keyBytes = keySecret.getEncoded();
            secretKeySpec = new SecretKeySpec(keyBytes, "Blowfish");

            byte[] decryptedText = this.decryptText(cipherText);

            //System.out.println("\n\nZASZYFROWANY TEKST:\n" + new String(cipherText, "UTF8"));
            //this.writeFile("E:\\semestr 6\\test_kot.jpg", decryptedText);
            System.out.println("KONIEC");

        } catch (InvalidKeyException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] encryptText(byte[] text, SecretKey key) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(text);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public byte[] decryptText(byte[] encryptedText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
            byte[] decryptedText = cipher.doFinal(encryptedText);
            return decryptedText;
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(EncryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
