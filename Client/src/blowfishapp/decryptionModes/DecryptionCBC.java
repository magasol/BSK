/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.decryptionModes;

import blowfishapp.keys.KeysGenerator;
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

/**
 *
 * @author Magdalena
 */
public class DecryptionCBC extends Decryption {

    private byte[] ivBytes;

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

    @Override
    public byte[] decryptText(byte[] encryptedText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySecret, iv);
            byte[] decryptedText = cipher.doFinal(encryptedText);
            return decryptedText;
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] encryptKey(byte[] text, SecretKey key) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            this.ivBytes = cipher.getIV();
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

    public byte[] getIvBytes() {
        return this.ivBytes;
    }
}
