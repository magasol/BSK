/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.encryptionModes;

import blowfishapp.keys.KeysGenerator;
import java.io.IOException;
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
public class EncryptionCFB extends Encryption {

    public EncryptionCFB(String fullFileName, KeysGenerator keysGenerator) {
        super(fullFileName, keysGenerator);
        try {
            cipher = Cipher.getInstance("Blowfish/CFB32/ISO10126Padding");

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EncryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void encryptFile() throws IOException {

        System.out.println("szyfruj plik " + this.fullFileName + " w trybie CFB");
        byte[] fileText = this.readFile();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySecret);
            byte[] cipherText = cipher.doFinal(fileText);
            this.encryptedText = cipherText;

            this.ivBytes = cipher.getIV();
            System.out.println("KONIEC");

        } catch (InvalidKeyException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(EncryptionCBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
