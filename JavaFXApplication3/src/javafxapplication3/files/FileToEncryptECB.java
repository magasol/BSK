/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication3.files;

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
public class FileToEncryptECB extends FileToEncrypt {

    public FileToEncryptECB(String fullFileName) {
        super(fullFileName);
        try {
            cipher = Cipher.getInstance("Blowfish/ECB/ISO10126Padding");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileToEncryptECB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(FileToEncryptECB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void encryptFile() throws IOException {
        System.out.println("szyfruj plik " + this.fullFileName + " w trybie ECB");
        String fileText = this.readFile();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySecret);
            byte[] cipherText = cipher.doFinal(fileText.getBytes("UTF8"));

            byte[] decryptedText = this.decryptText(cipherText);

            System.out.println("\n\nZASZYFROWANY TEKST:\n" + new String(cipherText, "UTF8"));
            System.out.println("\n\nODSZYFROWANY TEKST:\n" + new String(decryptedText, "UTF8"));

            //writeFile("C:\\Users\\Aleksandra\\Desktop\\test.txt", encrypted(encryptedText));
        } catch (InvalidKeyException ex) {
            Logger.getLogger(FileToEncryptCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(FileToEncryptCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(FileToEncryptCBC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FileToEncryptECB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(FileToEncryptECB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] decryptText(byte[] encryptedText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.DECRYPT_MODE, keySecret);
        byte[] decryptedText = cipher.doFinal(encryptedText);
        return decryptedText;

    }
}
