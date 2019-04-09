/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.keys;

import blowfishapp.BlowFishApp;
import blowfishapp.encryptionModes.EncryptionCBC;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Magdalena
 */
public final class KeysGenerator {

    private byte[] pswdShortcut;
    //do zmiany
    public SecretKey keySecret;

    public KeysGenerator(String pswd) {
        createRSAKeys(pswd);
        createSessionKey();
    }

    byte[] createPswdShortcut(String pswd) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            this.pswdShortcut = digest.digest(pswd.getBytes(StandardCharsets.UTF_8));
            System.out.println("Skrót hasła: " + new String(this.pswdShortcut));
            return this.pswdShortcut;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void createRSAKeys(String pswd) {
        try {
            String algorithm = "RSA"; // or RSA, DH, etc.
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
            keyGen.initialize(1024);
            KeyPair keypair = keyGen.genKeyPair();
            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();

            SecretKeySpec secretKeySpec = createKeyForRSAPrivateKeyEncryption(pswd);
            EncryptionCBC encryption = new EncryptionCBC(null,null,this);
            byte[] privateKeyBytes = encryption.encryptText(privateKey.getEncoded(), secretKeySpec);
            System.out.println("\n\n\nPrivte key:\n" + new String(privateKeyBytes) + "\n\n\nPublic key:\n");
            System.out.println(new String(publicKey.getEncoded()));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private SecretKeySpec createKeyForRSAPrivateKeyEncryption(String key) {
        byte[] keyData = key.getBytes();
        return new SecretKeySpec(keyData, "Blowfish");
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

    public SecretKey getKeySecret() {
        return keySecret;
    }
}
