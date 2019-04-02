/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blowfishapp;

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
import blowfishapp.encryptionModes.*;
import blowfishapp.keys.KeysGenerator;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author Magdalena
 */
public class BlowFishApp extends Application {

    private File file;
    private KeysGenerator keysGenerator;

    @Override
    public void start(Stage primaryStage) {

        Text encryptionTypeText = new Text("Tryb szyfrowania");
        Text inputFileNameText = new Text("Nazwa pliku");
                        
        ObservableList<String> names = FXCollections.observableArrayList(
                "ECB", "CBC", "CFB", "OFB", "NONE");
        ChoiceBox<String> encryptionChoiceBox = new ChoiceBox<>(names);

        final FileChooser fileChooser = new FileChooser();

        final Button chooseFileButton = new Button("Wybierz plik");

        chooseFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File f = fileChooser.showOpenDialog(primaryStage);
                if (f != null) {
                    file = f;
                    inputFileNameText.setText(f.getName());
                }
            }
        });
        Text outputFileNameText = new Text("Plik wyjściowy");
        TextField outputFileNameTextField = new TextField("Nazwa");
        
        Text pswdText = new Text("Hasło");
        PasswordField pswdField = new PasswordField();
        
        //na razie tworzenie kluczy, później będą tworzone w momencie nawiązania połączenia
        Button pswdButton = new Button();
        pswdButton.setText("Create Keys");
        pswdButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                generateKeys(pswdField.getText());
            }
        });

        Button encryptButton = new Button();
        encryptButton.setText("Encrypt");
        encryptButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    encrypt(encryptionChoiceBox.getValue(),outputFileNameTextField.getText());
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
        gridPane.add(inputFileNameText, 1, 1);
        gridPane.add(outputFileNameText, 0, 2);
        gridPane.add(outputFileNameTextField, 1, 2);
        gridPane.add(pswdText, 0, 3);
        gridPane.add(pswdField, 1, 3);
        gridPane.add(pswdButton, 2, 3);
        gridPane.add(encryptButton, 0, 4);

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

    private void encrypt(String value, String outputFileName) throws IOException {
        if (file != null) {
            Encryption encryption = null;
            switch (value) {
                case "CBC":
                    System.out.println("tryb szyfrowania cbc");
                    encryption = new EncryptionCBC(file.getPath(), outputFileName, this.keysGenerator);
                    break;
                case "CFB":
                    encryption = new EncryptionCFB(file.getPath(), outputFileName, this.keysGenerator);
                    System.out.println("tryb szyfrowania cfb");
                    break;
                case "ECB":
                    encryption = new EncryptionECB(file.getPath(), outputFileName, this.keysGenerator);
                    System.out.println("tryb szyfrowania ecb");
                    break;
                case "OFB":
                    encryption = new EncryptionOFB(file.getPath(), outputFileName, this.keysGenerator);
                    System.out.println("tryb szyfrowania ofb");
                    break;
                default:
                    encryption = new Encryption(file.getPath(), outputFileName, this.keysGenerator);
                    System.out.println("brak trybu szyfrowania");
            }
            if (encryption != null) {
                encryption.encryptFile();
            }
        }
    }

    private void generateKeys(String pswd) {
        this.keysGenerator = new KeysGenerator(pswd);
    }
}
