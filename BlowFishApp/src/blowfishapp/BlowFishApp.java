/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import blowfishapp.files.Encryption;
import blowfishapp.files.EncryptionCBC;
import blowfishapp.files.EncryptionCFB;
import blowfishapp.files.EncryptionECB;
import blowfishapp.files.EncryptionOFB;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import javafx.scene.control.PasswordField;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Magdalena
 */
public class BlowFishApp extends Application {

    private final Desktop desktop = Desktop.getDesktop();
    private final int ROW_HEIGHT = 25;
    private File file;
    private String chosenEncryptionType;
    private byte[] pswdShortcut;

    @Override
    public void start(Stage primaryStage) {

        Text encryptionTypeText = new Text("Tryb szyfrowania");
        Text fileNameText = new Text("Nazwa pliku");

        ObservableList<String> names = FXCollections.observableArrayList(
                "ECB", "CBC", "CFB", "OFB", "NONE");
        ChoiceBox<String> encryptionChoiceBox = new ChoiceBox<>(names);

        final FileChooser fileChooser = new FileChooser();

        final Button chooseFileButton = new Button("Wybierz plik");

        chooseFileButton.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File f = fileChooser.showOpenDialog(primaryStage);
                if (f != null) {
                    file = f;
                    fileNameText.setText(f.getName());
                }
            }
        });

        Text pswdText = new Text("Hasło");
        PasswordField pswdField = new PasswordField();

        Button pswdButton = new Button();
        pswdButton.setText("Create RSA Keys");
        pswdButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createRSAKeys(pswdField.getText());
            }
        });

        Button encryptButton = new Button();
        encryptButton.setText("Encrypt");
        encryptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    encrypt(encryptionChoiceBox.getValue());
                } catch (IOException ex) {
                    Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        GridPane gridPane = new GridPane();

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        gridPane.add(encryptionTypeText, 0, 0);
        gridPane.add(encryptionChoiceBox, 1, 0);
        gridPane.add(chooseFileButton, 0, 1);
        gridPane.add(fileNameText, 1, 1);
        gridPane.add(pswdText, 0, 2);
        gridPane.add(pswdField, 1, 2);
        gridPane.add(pswdButton, 2, 2);
        gridPane.add(encryptButton, 0, 3);

        Scene scene = new Scene(gridPane, 400, 350);

        primaryStage.setTitle("Aplikacja szyfrująca");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void encrypt(String value) throws IOException {
        if (file != null) {
            Encryption encryption = null;
            switch (value) {
                case "CBC":
                    System.out.println("tryb szyfrowania cbc");
                    encryption = new EncryptionCBC(file.getPath());
                    break;
                case "CFB":
                    encryption = new EncryptionCFB(file.getPath());
                    System.out.println("tryb szyfrowania cfb");
                    break;
                case "ECB":
                    encryption = new EncryptionECB(file.getPath());
                    System.out.println("tryb szyfrowania ecb");
                    break;
                case "OFB":
                    encryption = new EncryptionOFB(file.getPath());
                    System.out.println("tryb szyfrowania ofb");
                    break;
                default:
                    encryption = new Encryption(file.getPath());
                    System.out.println("brak trybu szyfrowania");
            }
            if (encryption != null) {
                encryption.encryptFile();
            }
        }
    }

    // do wywołania przy nawiązywaniu połączenia, na razie nigdzie nie wywoływane
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

    void createRSAKeys(String pswd) {
        try {
            String algorithm = "RSA"; // or RSA, DH, etc.
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
            keyGen.initialize(1024);
            KeyPair keypair = keyGen.genKeyPair();
            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();
            
            SecretKeySpec secretKeySpec = createKeyForRSAPrivateKeyEncryption(pswd);
            EncryptionCBC encryption = new EncryptionCBC(null);
            byte[] privateKeyBytes = encryption.encryptText(privateKey.getEncoded(), secretKeySpec);
            System.out.println("\n\n\nPrivte key:\n" + new String(privateKeyBytes) + "\n\n\nPublic key:\n");
            System.out.println(new String(publicKey.getEncoded()));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlowFishApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    SecretKeySpec createKeyForRSAPrivateKeyEncryption(String key) {
        byte[] keyData = key.getBytes();
        return new SecretKeySpec(keyData, "Blowfish");
    }
}
