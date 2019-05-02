/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp.keys;

import blowfishapp.BlowFishApp;
import blowfishapp.decryptionModes.Decryption;
import blowfishapp.decryptionModes.DecryptionCBC;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Magdalena
 */
public final class KeysGenerator {

    private SecretKey keySecret;
    final private String privateFolderName = ".\\private";
    final private String publicFolderName = ".\\public";
    final private String fileName = "key";

    public KeysGenerator(String pswd) {
        createRSAKeys(pswd);
    }

    public void createRSAKeys(String pswd) {
        try {
            String algorithm = "RSA"; // or RSA, DH, etc.
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
            keyGen.initialize(1024);
            KeyPair keypair = keyGen.genKeyPair();
            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();

            SecretKeySpec secretKeySpec = createKeyForRSAPrivateKeyEncryption(createPswdShortcut(pswd));
            DecryptionCBC encryption = new DecryptionCBC(null, null, this);
            //byte[] privateKeyBytes = encryption.encryptKey(privateKey.getEncoded(), secretKeySpec);

            //System.out.println("\n\n\nPrivte key:\n" + new String(privateKeyBytes) + "\n\n\nPublic key:\n");
            //System.out.println(new String(publicKey.getEncoded()));
            if (!new File(privateFolderName).exists()) {
                new File(privateFolderName).mkdir();
            }
            if (!new File(publicFolderName).exists()) {
                new File(publicFolderName).mkdir();
            }
            //writeFile("private", privateKeyBytes);
            writeFile("private", privateKey.getEncoded());
            writeFile("public", publicKey.getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private SecretKeySpec createKeyForRSAPrivateKeyEncryption(byte[] key) {
        return new SecretKeySpec(key, "Blowfish");
    }

    byte[] createPswdShortcut(String pswd) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] pswdShortcut = digest.digest(pswd.getBytes(StandardCharsets.UTF_8));
            //System.out.println("Skrót hasła: " + new String(pswdShortcut));
            return pswdShortcut;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void writeFile(String path, byte[] text) throws FileNotFoundException {
        try {
            FileOutputStream outputStream
                    = new FileOutputStream(path + "\\" + fileName);
            outputStream.write(text);
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void decryptSecretKey(byte[] encryptedSecretKey, String pswd) {
        PrivateKey privateKey = decryptPrivateKey(pswd);
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedSecretKeyBytes = cipher.doFinal(encryptedSecretKey);
            this.keySecret = new SecretKeySpec(decryptedSecretKeyBytes, "Blowfish");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private PrivateKey decryptPrivateKey(String pswd) {
        try {
            byte[] privateKeyBytes = readPrivateKey();
            //SecretKeySpec secretKeySpec = createKeyForRSAPrivateKeyEncryption(createPswdShortcut(pswd));
            //DecryptionCBC keyDecryption = new DecryptionCBC(null, null, this);
            //byte[] decryptedPrivateKeyBytes = keyDecryption.decryptKey(privateKeyBytes, secretKeySpec);
            //return KeyFactory.getInstance("RSA")
            //.generatePrivate(new X509EncodedKeySpec(decryptedPrivateKeyBytes));
            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchPaddingException ex) {
//            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidKeyException ex) {
//            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalBlockSizeException ex) {
//            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (BadPaddingException ex) {
//            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] readPublicKey() {
        try {
            return readFile(publicFolderName + "\\" + fileName);
        } catch (IOException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] readPrivateKey() {
        try {
            return readFile(privateFolderName + "\\" + fileName);
        } catch (IOException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] readFile(String fullFileName) throws FileNotFoundException, IOException {
        Path path = Paths.get(fullFileName);
        return Files.readAllBytes(path);
    }

    public SecretKey getKeySecret() {
        return keySecret;
    }

}
