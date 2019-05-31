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
public class DecryptionOFB extends Decryption {

    public DecryptionOFB(byte[] fullFileName, KeysGenerator keysGenerator) {
        super(fullFileName, keysGenerator);
        try {
            this.cipher = Cipher.getInstance("Blowfish/OFB32/ISO10126Padding");

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public byte[] decryptText() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.keysGenerator.getKeySecret(), iv);
            byte[] decryptedText = this.cipher.doFinal(this.encryptedText);
            return decryptedText;
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(DecryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
