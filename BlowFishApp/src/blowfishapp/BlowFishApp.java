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
import javafx.scene.control.PasswordField;

/**
 *
 * @author Magdalena
 */
public class BlowFishApp extends Application {

    private final Desktop desktop = Desktop.getDesktop();
    private final int ROW_HEIGHT = 25;
    private File file;
    private String chosenEncryptionType;

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

        //MessageDigest digest = MessageDigest.getInstance("SHA-256");
        //byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        
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
            Encryption fileToEncrypt = null;
            switch (value) {
                case "CBC":
                    System.out.println("tryb szyfrowania cbc");
                    fileToEncrypt = new EncryptionCBC(file.getPath());
                    break;
                case "CFB":
                    fileToEncrypt = new EncryptionCFB(file.getPath());
                    System.out.println("tryb szyfrowania cfb");
                    break;
                case "ECB":
                    fileToEncrypt = new EncryptionECB(file.getPath());
                    System.out.println("tryb szyfrowania ecb");
                    break;
                case "OFB":
                    fileToEncrypt = new EncryptionOFB(file.getPath());
                    System.out.println("tryb szyfrowania ofb");
                    break;
                default:
                    fileToEncrypt = new Encryption(file.getPath());
                    System.out.println("brak trybu szyfrowania");
            }
            if (fileToEncrypt != null) {
                fileToEncrypt.encryptFile();
            }
        }
    }
}
