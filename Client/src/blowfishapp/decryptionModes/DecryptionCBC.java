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

/**
 *
 * @author Magdalena
 */
public class DecryptionCBC extends Decryption {

    private byte[] ivBytes;

    public DecryptionCBC(byte[] fullFileName, KeysGenerator keysGenerator) {
        super(fullFileName, keysGenerator);
        try {
            cipher = Cipher.getInstance("Blowfish/CBC/ISO10126Padding");

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public byte[] decryptText() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            long start = System.nanoTime(); 
            cipher.init(Cipher.DECRYPT_MODE, this.keysGenerator.getKeySecret(), this.iv);
            byte[] decryptedText = this.cipher.doFinal(this.encryptedText);
            System.out.println("czas deszyfrowania"+(System.nanoTime() - start));
            return decryptedText;
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] decryptKey(byte[] encryptedText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.keysGenerator.getKeySecret(), this.iv);
            byte[] decryptedText = this.cipher.doFinal(encryptedText);
            return decryptedText;
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] encryptKey(byte[] text) {
        try {
            System.out.println(Cipher.getMaxAllowedKeyLength("Blowfish/CBC/ISO10126Padding"));
            this.cipher.init(Cipher.ENCRYPT_MODE, this.keysGenerator.getKeySecret());
            this.ivBytes = this.cipher.getIV();
            return cipher.doFinal(text);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(DecryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(DecryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(DecryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DecryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] getIvBytes() {
        return this.ivBytes;
    }
}
