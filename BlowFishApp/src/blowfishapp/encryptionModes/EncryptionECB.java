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
public class EncryptionECB extends Encryption {

    public EncryptionECB(String fullFileName, KeysGenerator keysGenerator) {
        super(fullFileName, keysGenerator);
        try {
            cipher = Cipher.getInstance("Blowfish/ECB/ISO10126Padding");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EncryptionECB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void encryptFile() throws IOException {
        System.out.println("szyfruj plik " + this.fullFileName + " w trybie ECB");
        byte[] fileText = this.readFile();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySecret);
            byte[] cipherText = cipher.doFinal(fileText);

            byte[] decryptedText = this.decryptText(cipherText);

            //System.out.println("\n\nZASZYFROWANY TEKST:\n" + new String(cipherText, "UTF8"));
            this.writeFile("E:\\semestr 6\\test_kot.jpg", decryptedText);
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
