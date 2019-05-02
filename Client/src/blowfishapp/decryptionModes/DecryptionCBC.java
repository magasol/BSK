/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.decryptionModes;

import blowfishapp.keys.KeysGenerator;
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
public class DecryptionCBC extends Decryption {

    public DecryptionCBC(byte[] fullFileName, String outputFileName, KeysGenerator keysGenerator) {
        super(fullFileName, outputFileName, keysGenerator);
        try {
            cipher = Cipher.getInstance("Blowfish/CBC/ISO10126Padding");

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] encryptKey(byte[] text, SecretKey key) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(text);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(DecryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(DecryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(DecryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] decryptKey(byte[] encryptedText, SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedText = cipher.doFinal(encryptedText);
        return decryptedText;
    }
}
