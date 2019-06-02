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
import java.nio.ByteBuffer;
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

    public byte[] encryptedSessionKeyBytes;
    private SecretKey keySecret;
    private String privateFolderName = ".\\private";
    private String publicFolderName = ".\\public";
    final private String fileName = "key";

    public KeysGenerator(String pswd, String user) {
        createRSAKeys(pswd,user);
    }

    public void createRSAKeys(String pswd, String user) {
        try {
            String algorithm = "RSA"; // or RSA, DH, etc.
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
            keyGen.initialize(1024);
            KeyPair keypair = keyGen.genKeyPair();
            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();

            this.keySecret = createKeyForRSAPrivateKeyEncryption(createPswdShortcut(pswd));
            DecryptionCBC encryption = new DecryptionCBC(null, this);
            byte[] privateKeyBytes = encryption.encryptKey(privateKey.getEncoded());

            //System.out.println("\n\n\nPrivte key:\n" + new String(privateKeyBytes) + "\n\n\nPublic key:\n");
            //System.out.println(new String(publicKey.getEncoded()));
            
            this.publicFolderName=this.publicFolderName+"\\"+user;
            this.privateFolderName=this.privateFolderName+"\\"+user;
            if (!new File(this.privateFolderName).exists()) {
                new File(this.privateFolderName).mkdir();
            }
            if (!new File(this.publicFolderName).exists()) {
                new File(this.publicFolderName).mkdir();
            }

            byte[] ivLength = ByteBuffer.allocate(4).putInt(encryption.getIvBytes().length).array();

            writeFile(this.privateFolderName, joinByteArrays(ivLength, encryption.getIvBytes(),
                    privateKeyBytes));
            writeFile(this.publicFolderName, publicKey.getEncoded());
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
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] pswdShortcut = digest.digest(pswd.getBytes(StandardCharsets.UTF_8));
            //System.out.println("Skrót hasła: " + new String(pswdShortcut));
            return pswdShortcut;
            //return "1234567890123456".getBytes();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private byte[] joinByteArrays(byte[] one, byte[] two, byte[] three) {
        byte[] combined = new byte[one.length + two.length + three.length];
        System.arraycopy(one, 0, combined, 0, one.length);
        System.arraycopy(two, 0, combined, one.length, two.length);
        System.arraycopy(three, 0, combined, one.length + two.length, three.length);
        return combined;
    }

    public void writeFile(String path, byte[] text) throws FileNotFoundException {
        try {
            FileOutputStream outputStream
                    = new FileOutputStream(path + "\\" + this.fileName);
            outputStream.write(text);
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Decryption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void decryptSecretKey(String pswd) throws InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException {
        PrivateKey privateKey = decryptPrivateKey(pswd);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedSecretKeyBytes = cipher.doFinal(this.encryptedSessionKeyBytes);
        this.keySecret = new SecretKeySpec(decryptedSecretKeyBytes, "Blowfish");
    }

    private PrivateKey decryptPrivateKey(String pswd) {
        try {
            SecretKeySpec secretKeySpec = createKeyForRSAPrivateKeyEncryption(createPswdShortcut(pswd));
            this.keySecret = secretKeySpec;
            DecryptionCBC keyDecryption = new DecryptionCBC(null, this);

            byte[] privateKeyFileBytes = readPrivateKey();
            byte[] ivLength = new byte[4];
            System.arraycopy(privateKeyFileBytes, 0, ivLength, 0, 4);
            byte[] iv = new byte[ByteBuffer.wrap(ivLength).getInt()];
            System.arraycopy(privateKeyFileBytes, 4, iv, 0, ByteBuffer.wrap(ivLength).getInt());
            int keyLength = privateKeyFileBytes.length - (ByteBuffer.wrap(ivLength).getInt() + 4);
            byte[] privateKeyBytes = new byte[keyLength];
            System.arraycopy(privateKeyFileBytes, 4 + ByteBuffer.wrap(ivLength).getInt(), privateKeyBytes, 0, keyLength);

            keyDecryption.setIvParameterSpec(iv);
            byte[] decryptedPrivateKeyBytes = keyDecryption.decryptKey(privateKeyBytes);
            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKeyBytes));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
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
        return null;
    }

    public byte[] readPublicKey() {
        try {
            return readFile(this.publicFolderName + "\\" + this.fileName);
        } catch (IOException ex) {
            Logger.getLogger(KeysGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] readPrivateKey() {
        try {
            return readFile(this.privateFolderName + "\\" + this.fileName);
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
        return this.keySecret;
    }
}
