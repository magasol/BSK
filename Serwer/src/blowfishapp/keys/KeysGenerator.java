/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.keys;

import blowfishapp.BlowFishApp;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Magdalena
 */
public final class KeysGenerator {

    public SecretKey keySecret;

    public KeysGenerator() {
        createSessionKey();
    }

    public KeysGenerator(byte[] keySecretBytes) {
        this.keySecret = new SecretKeySpec(keySecretBytes, "Blowfish");
    }

    public void createSessionKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
            keyGenerator.init(128, new SecureRandom());
            this.keySecret = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void encryptSessionKey(byte[] publicKeyBytes) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,
                    KeyFactory.getInstance("RSA")
                            .generatePublic(new X509EncodedKeySpec(publicKeyBytes)));
            byte[] encryptedKeyBytes = cipher.doFinal(this.keySecret.getEncoded());
            this.keySecret = new SecretKeySpec(encryptedKeyBytes, "Blowfish");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SecretKey getKeySecret() {
        return keySecret;
    }
}
